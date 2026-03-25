package game.codecolony.content;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

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

    static final class StructuredMarkdownDocument {

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
            final String[] blocks = markdown.trim().split("(?:\\R\\s*){2,}");
            for (final String block : blocks) {
                final String trimmedBlock = block.trim();
                if (trimmedBlock.isEmpty()) {
                    continue;
                }
                html.append("<p>")
                        .append(renderInlineMarkdown(trimmedBlock.replace('\n', ' ')))
                        .append("</p>");
            }
            return html.toString();
        }

        private static String renderInlineMarkdown(final String markdown) {
            final StringBuilder html = new StringBuilder();
            int index = 0;
            while (index < markdown.length()) {
                final char character = markdown.charAt(index);
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
                        html.append("<em>")
                                .append(HtmlUtils.htmlEscape(markdown.substring(index + 1, closingIndex)))
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
