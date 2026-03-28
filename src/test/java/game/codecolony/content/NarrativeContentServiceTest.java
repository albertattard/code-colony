package game.codecolony.content;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NarrativeContentServiceTest {

    private final NarrativeContentService narrativeContentService = new NarrativeContentService();

    @Test
    void loadsIntroNarrativeFromMarkdownContent() {
        final NarrativeContentService.IntroNarrativeContent introNarrative =
                narrativeContentService.loadIntroNarrative();

        assertThat(introNarrative.title()).isEqualTo("Mission Briefing");
        assertThat(introNarrative.summary()).contains("Eryndor-IV");
        assertThat(introNarrative.objective()).isEqualTo("Investigate the colony site and restore critical systems in stages.");
        assertThat(introNarrative.briefingTitle()).isEqualTo("Operational Briefing");
        assertThat(introNarrative.briefingHtml()).contains("<p>");
        assertThat(introNarrative.briefingHtml()).contains("<em>Colony Operations and Repair Engineers</em>");
        assertThat(introNarrative.interfaceTitle()).isEqualTo("Legacy Control Interface");
        assertThat(introNarrative.interfaceOrientationHtml()).contains("Each <strong>Run</strong> starts from the mission start state");
    }

    @Test
    void loadsMissionNarrativeFromMarkdownContent() {
        final NarrativeContentService.MissionNarrativeContent missionNarrative =
                narrativeContentService.loadMissionNarrative("mission-01");

        assertThat(missionNarrative.title()).isEqualTo("Mission 01: Wake The CORE");
        assertThat(missionNarrative.summary()).contains("Maintenance Room B-1049");
        assertThat(missionNarrative.objective()).isEqualTo("Call Core.connect(); to bring CORE-01 online.");
        assertThat(missionNarrative.briefingHtml()).contains("The only way to communicate with the unit is by issuing Java commands through the terminal.");
        assertThat(missionNarrative.briefingHtml()).contains("<code>Core.connect();</code>");
    }

    @Test
    void loadsMissionTwoNarrativeFromMarkdownContent() {
        final NarrativeContentService.MissionNarrativeContent missionNarrative =
                narrativeContentService.loadMissionNarrative("mission-02");

        assertThat(missionNarrative.title()).isEqualTo("Mission 02: Charge The CORE");
        assertThat(missionNarrative.summary()).contains("battery is fully depleted");
        assertThat(missionNarrative.objective()).isEqualTo("Charge CORE-01 to full power.");
        assertThat(missionNarrative.briefingHtml()).contains("Congratulations, engineer.");
        assertThat(missionNarrative.briefingHtml()).contains("var core = Core.connect();");
        assertThat(missionNarrative.briefingHtml()).contains("core.charge();");
    }

    @Test
    void loadsMissionThreeNarrativeFromMarkdownContent() {
        final NarrativeContentService.MissionNarrativeContent missionNarrative =
                narrativeContentService.loadMissionNarrative("mission-03");

        assertThat(missionNarrative.title()).isEqualTo("Mission 03: Repair The CORE");
        assertThat(missionNarrative.summary()).contains("structural damage");
        assertThat(missionNarrative.objective()).isEqualTo("Move CORE-01 to the repair station and repair it.");
        assertThat(missionNarrative.briefingHtml()).contains("core.move();");
        assertThat(missionNarrative.briefingHtml()).contains("core.repair();");
    }

    @Test
    void loadsMissionExplanationFromMarkdownContent() {
        final NarrativeContentService.MissionExplanationContent explanation =
                narrativeContentService.loadMissionExplanation("mission-01");

        assertThat(explanation.headline()).isEqualTo("Wake CORE-01 with one line of code");
        assertThat(explanation.explanationHtml()).contains("Java is built around");
        assertThat(explanation.explanationHtml()).contains("Core.connect();");
        assertThat(explanation.explanationHtml()).contains("<pre><code>");
        assertThat(explanation.explanationHtml()).contains("class Car {");
        assertThat(explanation.explanationHtml()).doesNotContain("```");
        assertThat(explanation.explanationHtml()).contains("<ul>");
        assertThat(explanation.explanationHtml()).contains("<li>");
        assertThat(explanation.explanationHtml()).contains("<hr>");
        assertThat(explanation.explanationHtml()).contains("semicolon (<code>;</code>)");
        assertThat(explanation.explanationHtml()).contains("<a href=\"https://learn.java/learning/tutorials/creatingobjectsandcallingmethods/\"");
    }

    @Test
    void loadsMissionTwoExplanationFromMarkdownContent() {
        final NarrativeContentService.MissionExplanationContent explanation =
                narrativeContentService.loadMissionExplanation("mission-02");

        assertThat(explanation.headline()).isEqualTo("Charge CORE-01 one step at a time");
        assertThat(explanation.explanationHtml()).contains("<strong>charge its battery</strong>");
        assertThat(explanation.explanationHtml()).contains("<strong>Can we call <code>charge()</code> on the <code>Core</code> class instead?</strong>");
    }

    @Test
    void loadsMissionThreeExplanationFromMarkdownContent() {
        final NarrativeContentService.MissionExplanationContent explanation =
                narrativeContentService.loadMissionExplanation("mission-03");

        assertThat(explanation.headline()).isEqualTo("Repair CORE-01 step by step");
        assertThat(explanation.explanationHtml()).contains("core.move();");
        assertThat(explanation.explanationHtml()).contains("core.repair();");
        assertThat(explanation.explanationHtml()).contains("<blockquote>");
    }

    @Test
    void rendersBlockQuotesFromMarkdownContent() {
        final NarrativeContentService.StructuredMarkdownDocument document =
                NarrativeContentService.StructuredMarkdownDocument.parse("""
                        # Sample

                        ## Headline
                        Example

                        ## Explanation
                        > **Can we call `charge()` on the `Core` class instead?**
                        >
                        > No. You call `charge()` on a connected unit.
                        """, "inline-test.md");

        final String html = document.requiredHtml("explanation");
        assertThat(html).contains("<blockquote>");
        assertThat(html).contains("<strong>Can we call <code>charge()</code> on the <code>Core</code> class instead?</strong>");
        assertThat(html).contains("<p>No. You call <code>charge()</code> on a connected unit.</p>");
    }
}
