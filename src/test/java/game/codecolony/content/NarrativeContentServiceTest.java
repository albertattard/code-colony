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
    }

    @Test
    void loadsMissionNarrativeFromMarkdownContent() {
        final NarrativeContentService.MissionNarrativeContent missionNarrative =
                narrativeContentService.loadMissionNarrative("mission-01");

        assertThat(missionNarrative.title()).isEqualTo("Mission 01: Wake The CORE");
        assertThat(missionNarrative.summary()).contains("Maintenance Room B-1049");
        assertThat(missionNarrative.objective()).isEqualTo("Call CORE.connect(); to bring CORE-01 online.");
        assertThat(missionNarrative.briefingHtml()).contains("The only way to communicate with the unit is by issuing Java commands through the terminal.");
        assertThat(missionNarrative.briefingHtml()).contains("<code>CORE.connect();</code>");
    }

    @Test
    void loadsMissionTwoNarrativeFromMarkdownContent() {
        final NarrativeContentService.MissionNarrativeContent missionNarrative =
                narrativeContentService.loadMissionNarrative("mission-02");

        assertThat(missionNarrative.title()).isEqualTo("Mission 02: Charge The CORE");
        assertThat(missionNarrative.summary()).contains("battery is fully depleted");
        assertThat(missionNarrative.objective()).isEqualTo("Charge CORE-01 to full power.");
        assertThat(missionNarrative.briefingHtml()).contains("Congratulations, engineer.");
        assertThat(missionNarrative.briefingHtml()).contains("var core = CORE.connect();");
        assertThat(missionNarrative.briefingHtml()).contains("core.charge();");
    }
}
