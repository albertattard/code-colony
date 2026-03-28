package game.codecolony.content;

import game.codecolony.mission.CommandReference;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public final class NarrativeContentService {

    public IntroNarrativeContent loadIntroNarrative() {
        final StructuredMarkdownDocument document = loadDocument("content/intro/briefing.md");
        return new IntroNarrativeContent(
                document.title(),
                document.requiredPlainText("summary"),
                document.requiredPlainText("objective"),
                document.requiredPlainText("briefing title"),
                document.requiredHtml("briefing"),
                document.requiredPlainText("interface title"),
                document.requiredHtml("interface orientation")
        );
    }

    public MissionNarrativeContent loadMissionNarrative(final String missionId) {
        final StructuredMarkdownDocument document = loadDocument("content/missions/" + missionId + "/content.md");
        return new MissionNarrativeContent(
                document.title(),
                document.requiredPlainText("summary"),
                document.requiredPlainText("objective"),
                document.requiredHtml("briefing")
        );
    }

    public MissionConsoleContent loadMissionConsoleContent(final String missionId) {
        final StructuredMarkdownDocument document = loadDocument("content/missions/" + missionId + "/content.md");
        return new MissionConsoleContent(
                document.requiredHtmlList("hints"),
                parseCommandReferences(document.requiredPlainTextList("available commands"), missionId)
        );
    }

    public MissionInitialRunContent loadMissionInitialRunContent(final String missionId) {
        final StructuredMarkdownDocument document = loadDocument("content/missions/" + missionId + "/content.md");
        return new MissionInitialRunContent(
                document.requiredPlainText("initial run headline"),
                document.requiredPlainText("initial run summary"),
                document.requiredPlainTextList("initial run events"),
                document.requiredPlainTextList("initial run feedback"),
                document.requiredPlainText("initial run status note")
        );
    }

    public MissionExplanationContent loadMissionExplanation(final String missionId) {
        final StructuredMarkdownDocument document = loadDocument("content/missions/" + missionId + "/content.md");
        return new MissionExplanationContent(
                document.requiredPlainText("headline"),
                document.requiredHtml("explanation")
        );
    }

    private StructuredMarkdownDocument loadDocument(final String resourcePath) {
        final String markdown = loadText(resourcePath);
        return StructuredMarkdownDocument.parse(markdown, resourcePath);
    }

    private String loadText(final String resourcePath) {
        final Path path = Path.of(resourcePath);
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load narrative content from " + path, exception);
        }
    }

    public record IntroNarrativeContent(String title, String summary, String objective, String briefingTitle,
                                        String briefingHtml,
                                        String interfaceTitle,
                                        String interfaceOrientationHtml) {
    }

    public record MissionNarrativeContent(String title, String summary, String objective, String briefingHtml) {
    }

    public record MissionConsoleContent(List<String> hints, List<CommandReference> commands) {
        public MissionConsoleContent {
            hints = List.copyOf(hints);
            commands = List.copyOf(commands);
        }
    }

    public record MissionInitialRunContent(String headline, String summary, List<String> events, List<String> feedback,
                                           String statusNote) {
        public MissionInitialRunContent {
            events = List.copyOf(events);
            feedback = List.copyOf(feedback);
        }
    }

    public record MissionExplanationContent(String headline, String explanationHtml) {
    }

    private static List<CommandReference> parseCommandReferences(final List<String> commandLines, final String missionId) {
        if (commandLines.isEmpty()) {
            return List.of();
        }

        final List<CommandReference> commands = new ArrayList<>();
        for (final String commandLine : commandLines) {
            final String[] parts = commandLine.split("\\|", 2);
            if (parts.length != 2) {
                throw new IllegalStateException(
                        "Invalid command entry in " + missionId + " briefing. Expected '<signature> | <description>' but found: "
                                + commandLine
                );
            }

            final String signature = parts[0].trim();
            final String description = parts[1].trim();
            if (signature.isBlank() || description.isBlank()) {
                throw new IllegalStateException(
                        "Invalid command entry in " + missionId + " briefing. Signature and description are required: "
                                + commandLine
                );
            }
            commands.add(new CommandReference(signature, description));
        }
        return commands;
    }

    static final class StructuredMarkdownDocument {

        private static final Pattern UNORDERED_LIST_ITEM = Pattern.compile("^[-*]\\s+(.+)$");
        private static final Pattern ORDERED_LIST_ITEM = Pattern.compile("^\\d+\\.\\s+(.+)$");
        private static final Pattern HORIZONTAL_RULE = Pattern.compile("^---+$");

        private final String title;
        private final Map<String, String> sections;

        private StructuredMarkdownDocument(final String title, final Map<String, String> sections) {
            this.title = title;
            this.sections = sections;
        }

        static StructuredMarkdownDocument parse(final String markdown, final String sourceName) {
            final String[] lines = markdown.split("\\R", -1);
            String title = null;
            String currentSection = null;
            final StringBuilder sectionBuffer = new StringBuilder();
            final Map<String, String> sections = new LinkedHashMap<>();

            for (final String line : lines) {
                if (line.startsWith("# ")) {
                    if (title != null) {
                        throw new IllegalStateException("Content file " + sourceName + " defines more than one title.");
                    }
                    title = line.substring(2).trim();
                    continue;
                }

                if (line.startsWith("## ")) {
                    storeSection(sections, currentSection, sectionBuffer);
                    currentSection = normalizeSectionName(line.substring(3));
                    sectionBuffer.setLength(0);
                    continue;
                }

                if (currentSection != null) {
                    if (!sectionBuffer.isEmpty()) {
                        sectionBuffer.append('\n');
                    }
                    sectionBuffer.append(line);
                }
            }

            storeSection(sections, currentSection, sectionBuffer);

            if (title == null || title.isBlank()) {
                throw new IllegalStateException("Content file " + sourceName + " must define a # title.");
            }

            return new StructuredMarkdownDocument(title, sections);
        }

        String title() {
            return title;
        }

        String requiredPlainText(final String sectionName) {
            final String markdown = requiredSection(sectionName);
            return markdown
                    .replace("`", "")
                    .replace("*", "")
                    .replace('\n', ' ')
                    .replaceAll("\\s+", " ")
                    .trim();
        }

        String requiredHtml(final String sectionName) {
            final String markdown = requiredSection(sectionName);
            return renderMarkdown(markdown);
        }

        List<String> requiredPlainTextList(final String sectionName) {
            final String markdown = requiredSection(sectionName);
            final List<String> values = new ArrayList<>();
            for (final String line : markdown.split("\\R", -1)) {
                final String normalized = toPlainTextLine(line);
                if (!normalized.isBlank()) {
                    values.add(normalized);
                }
            }
            if (values.isEmpty()) {
                throw new IllegalStateException("Missing content section: " + sectionName);
            }
            return List.copyOf(values);
        }

        List<String> requiredHtmlList(final String sectionName) {
            final String markdown = requiredSection(sectionName);
            final List<String> values = new ArrayList<>();
            for (final String line : markdown.split("\\R", -1)) {
                final String normalized = stripListPrefix(line);
                if (!normalized.isBlank()) {
                    values.add(renderInlineMarkdown(normalized));
                }
            }
            if (values.isEmpty()) {
                throw new IllegalStateException("Missing content section: " + sectionName);
            }
            return List.copyOf(values);
        }

        private String requiredSection(final String sectionName) {
            final String markdown = sections.get(normalizeSectionName(sectionName));
            if (markdown == null || markdown.isBlank()) {
                throw new IllegalStateException("Missing content section: " + sectionName);
            }
            return markdown;
        }

        private static String toPlainTextLine(final String line) {
            String normalized = stripListPrefix(line);
            return normalized
                    .replace("`", "")
                    .replace("*", "")
                    .replaceAll("\\s+", " ")
                    .trim();
        }

        private static String stripListPrefix(final String line) {
            String normalized = line.trim();
            normalized = normalized.replaceFirst("^[-*]\\s+", "");
            normalized = normalized.replaceFirst("^\\d+\\.\\s+", "");
            return normalized;
        }

        private static void storeSection(final Map<String, String> sections,
                                         final String currentSection,
                                         final StringBuilder sectionBuffer) {
            if (currentSection == null) {
                return;
            }
            sections.put(currentSection, sectionBuffer.toString().trim());
        }

        private static String normalizeSectionName(final String sectionName) {
            return sectionName.trim().toLowerCase();
        }

        private static String renderMarkdown(final String markdown) {
            final StringBuilder html = new StringBuilder();
            final List<String> paragraphLines = new ArrayList<>();
            final List<String> listItems = new ArrayList<>();
            String activeListTag = null;
            boolean inCodeFence = false;
            String codeFenceLanguage = "";
            final StringBuilder codeFenceContent = new StringBuilder();
            final StringBuilder blockQuoteContent = new StringBuilder();

            for (final String rawLine : markdown.split("\\R", -1)) {
                final String trimmedLine = rawLine.trim();

                if (inCodeFence) {
                    if (trimmedLine.startsWith("```")) {
                        html.append("<pre><code");
                        if (!codeFenceLanguage.isBlank()) {
                            html.append(" class=\"language-")
                                    .append(HtmlUtils.htmlEscape(codeFenceLanguage))
                                    .append('"');
                        }
                        html.append(">")
                                .append(HtmlUtils.htmlEscape(codeFenceContent.toString()))
                                .append("</code></pre>");
                        inCodeFence = false;
                        codeFenceLanguage = "";
                        codeFenceContent.setLength(0);
                    } else {
                        if (!codeFenceContent.isEmpty()) {
                            codeFenceContent.append('\n');
                        }
                        codeFenceContent.append(rawLine);
                    }
                    continue;
                }

                final String strippedLine = rawLine.stripLeading();
                if (strippedLine.startsWith(">")) {
                    appendParagraph(html, paragraphLines);
                    appendList(html, listItems, activeListTag);
                    listItems.clear();
                    activeListTag = null;

                    String quoteLine = strippedLine.substring(1);
                    if (quoteLine.startsWith(" ")) {
                        quoteLine = quoteLine.substring(1);
                    }
                    if (!blockQuoteContent.isEmpty()) {
                        blockQuoteContent.append('\n');
                    }
                    blockQuoteContent.append(quoteLine);
                    continue;
                }

                appendBlockQuote(html, blockQuoteContent);

                if (trimmedLine.startsWith("```")) {
                    appendParagraph(html, paragraphLines);
                    appendList(html, listItems, activeListTag);
                    listItems.clear();
                    activeListTag = null;
                    inCodeFence = true;
                    codeFenceLanguage = trimmedLine.substring(3).trim();
                    continue;
                }

                if (trimmedLine.isEmpty()) {
                    appendParagraph(html, paragraphLines);
                    appendList(html, listItems, activeListTag);
                    listItems.clear();
                    activeListTag = null;
                    continue;
                }

                if (HORIZONTAL_RULE.matcher(trimmedLine).matches()) {
                    appendParagraph(html, paragraphLines);
                    appendList(html, listItems, activeListTag);
                    listItems.clear();
                    activeListTag = null;
                    html.append("<hr>");
                    continue;
                }

                final var unorderedItemMatcher = UNORDERED_LIST_ITEM.matcher(trimmedLine);
                if (unorderedItemMatcher.matches()) {
                    appendParagraph(html, paragraphLines);
                    if (!"ul".equals(activeListTag)) {
                        appendList(html, listItems, activeListTag);
                        listItems.clear();
                        activeListTag = "ul";
                    }
                    listItems.add(unorderedItemMatcher.group(1).trim());
                    continue;
                }

                final var orderedItemMatcher = ORDERED_LIST_ITEM.matcher(trimmedLine);
                if (orderedItemMatcher.matches()) {
                    appendParagraph(html, paragraphLines);
                    if (!"ol".equals(activeListTag)) {
                        appendList(html, listItems, activeListTag);
                        listItems.clear();
                        activeListTag = "ol";
                    }
                    listItems.add(orderedItemMatcher.group(1).trim());
                    continue;
                }

                appendList(html, listItems, activeListTag);
                listItems.clear();
                activeListTag = null;
                paragraphLines.add(trimmedLine);
            }

            appendParagraph(html, paragraphLines);
            appendList(html, listItems, activeListTag);
            appendBlockQuote(html, blockQuoteContent);

            if (inCodeFence) {
                html.append("<pre><code")
                        .append(codeFenceLanguage.isBlank() ? "" : " class=\"language-"
                                + HtmlUtils.htmlEscape(codeFenceLanguage) + "\"")
                        .append(">")
                        .append(HtmlUtils.htmlEscape(codeFenceContent.toString()))
                        .append("</code></pre>");
            }

            return html.toString();
        }

        private static void appendParagraph(final StringBuilder html, final List<String> paragraphLines) {
            if (paragraphLines.isEmpty()) {
                return;
            }
            html.append("<p>")
                    .append(renderInlineMarkdown(String.join(" ", paragraphLines)))
                    .append("</p>");
            paragraphLines.clear();
        }

        private static void appendList(final StringBuilder html,
                                       final List<String> items,
                                       final String listTag) {
            if (listTag == null || items.isEmpty()) {
                return;
            }
            html.append('<').append(listTag).append('>');
            for (final String item : items) {
                html.append("<li>")
                        .append(renderInlineMarkdown(item))
                        .append("</li>");
            }
            html.append("</").append(listTag).append('>');
        }

        private static void appendBlockQuote(final StringBuilder html, final StringBuilder blockQuoteContent) {
            if (blockQuoteContent.isEmpty()) {
                return;
            }
            html.append("<blockquote>")
                    .append(renderMarkdown(blockQuoteContent.toString()))
                    .append("</blockquote>");
            blockQuoteContent.setLength(0);
        }

        private static String renderInlineMarkdown(final String markdown) {
            final StringBuilder html = new StringBuilder();
            int index = 0;
            while (index < markdown.length()) {
                final char character = markdown.charAt(index);
                if (character == '[') {
                    final int closingBracketIndex = markdown.indexOf(']', index + 1);
                    final int openingParenIndex = closingBracketIndex + 1;
                    if (closingBracketIndex > index + 1
                            && openingParenIndex < markdown.length()
                            && markdown.charAt(openingParenIndex) == '(') {
                        final int closingParenIndex = markdown.indexOf(')', openingParenIndex + 1);
                        if (closingParenIndex > openingParenIndex + 1) {
                            final String label = markdown.substring(index + 1, closingBracketIndex);
                            final String href = markdown.substring(openingParenIndex + 1, closingParenIndex).trim();
                            html.append("<a href=\"")
                                    .append(HtmlUtils.htmlEscape(href))
                                    .append("\" target=\"_blank\" rel=\"noreferrer noopener\">")
                                    .append(HtmlUtils.htmlEscape(label))
                                    .append("</a>");
                            index = closingParenIndex + 1;
                            continue;
                        }
                    }
                }
                if (character == '`') {
                    final int closingIndex = markdown.indexOf('`', index + 1);
                    if (closingIndex > index + 1) {
                        html.append("<code>")
                                .append(HtmlUtils.htmlEscape(markdown.substring(index + 1, closingIndex)))
                                .append("</code>");
                        index = closingIndex + 1;
                        continue;
                    }
                }
                if (character == '*') {
                    if (index + 1 < markdown.length() && markdown.charAt(index + 1) == '*') {
                        final int closingIndex = markdown.indexOf("**", index + 2);
                        if (closingIndex > index + 2) {
                            final String strongContent = markdown.substring(index + 2, closingIndex);
                            html.append("<strong>")
                                    .append(renderInlineMarkdown(strongContent))
                                    .append("</strong>");
                            index = closingIndex + 2;
                            continue;
                        }
                    }
                    final int closingIndex = markdown.indexOf('*', index + 1);
                    if (closingIndex > index + 1) {
                        final String emphasizedContent = markdown.substring(index + 1, closingIndex);
                        html.append("<em>")
                                .append(renderInlineMarkdown(emphasizedContent))
                                .append("</em>");
                        index = closingIndex + 1;
                        continue;
                    }
                }
                html.append(HtmlUtils.htmlEscape(String.valueOf(character)));
                index++;
            }
            return html.toString();
        }
    }
}
