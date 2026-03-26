package game.codecolony.content;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.core.io.ClassPathResource;
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
                document.requiredHtml("briefing")
        );
    }

    public MissionNarrativeContent loadMissionNarrative(final String missionId) {
        final StructuredMarkdownDocument document = loadDocument("content/missions/" + missionId + "/briefing.md");
        return new MissionNarrativeContent(
                document.title(),
                document.requiredPlainText("summary"),
                document.requiredPlainText("objective"),
                document.requiredHtml("briefing")
        );
    }

    public MissionExplanationContent loadMissionExplanation(final String missionId) {
        final StructuredMarkdownDocument document = loadDocument("content/missions/" + missionId + "/explain.md");
        return new MissionExplanationContent(
                document.requiredPlainText("headline"),
                document.requiredHtml("explanation")
        );
    }

    private StructuredMarkdownDocument loadDocument(final String classpathLocation) {
        final ClassPathResource resource = new ClassPathResource(classpathLocation);
        try (InputStream inputStream = resource.getInputStream()) {
            final String markdown = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return StructuredMarkdownDocument.parse(markdown, classpathLocation);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load narrative content from " + classpathLocation, exception);
        }
    }

    public record IntroNarrativeContent(String title, String summary, String objective, String briefingTitle,
                                        String briefingHtml) {
    }

    public record MissionNarrativeContent(String title, String summary, String objective, String briefingHtml) {
    }

    public record MissionExplanationContent(String headline, String explanationHtml) {
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

        private String requiredSection(final String sectionName) {
            final String markdown = sections.get(normalizeSectionName(sectionName));
            if (markdown == null || markdown.isBlank()) {
                throw new IllegalStateException("Missing content section: " + sectionName);
            }
            return markdown;
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
