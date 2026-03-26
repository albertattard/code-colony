package game.codecolony.web;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class MissionControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void homePageRendersIntroScreen() throws Exception {
        final MvcResult result = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn();
        final String body = result.getResponse().getContentAsString();

        assertThat(body).contains("Mission Briefing");
        assertThat(body).contains("Investigate the colony site and restore critical systems in stages.");
        assertThat(body).contains("Operational Briefing");
        assertThat(body).contains("Eryndor-IV");
        assertThat(body).contains("Colony Operations and Repair Engineers");
        assertThat(body).contains("/audio/briefings/intro.mp3");
        assertThat(body).contains("Start");
        assertThat(body).contains("action=\"/game-sessions\"");
        assertThat(body).doesNotContain("Mission 01: Wake The CORE");
    }

    @Test
    void missionPageRenders() throws Exception {
        final String missionOnePath = createSessionAndGetMissionOnePath();
        final MvcResult result = mockMvc.perform(get(missionOnePath))
                .andExpect(status().isOk())
                .andReturn();
        final String body = result.getResponse().getContentAsString();

        assertThat(body).contains("Mission 01: Wake The CORE");
        assertThat(body).contains("Maintenance Room B-1049");
        assertThat(body).contains("Code Console");
        assertThat(body).contains("Mission Briefing");
        assertThat(body).contains("The only way to communicate with the unit is by issuing Java commands through the terminal.");
        assertThat(body).contains("Core.connect();");
        assertThat(body).contains("/audio/briefings/mission-01.mp3");
        assertThat(body).contains("data-briefing-modal");
        assertThat(body).contains("data-briefing-open");
        assertThat(body).contains("CORE Status");
        assertThat(body).contains("Unit");
        assertThat(body).contains("CORE-01");
        assertThat(body).doesNotContain("Program Output");
        assertThat(body).contains("<textarea id=\"code\" name=\"code\" spellcheck=\"false\"></textarea>");
        assertThat(body).contains(">Reset</a>");
        assertThat(body).contains("Explain");
        assertThat(body).contains(">Run</button>");
        assertThat(body).doesNotContain(">Next<");
        assertThat(body).contains(missionOnePath + "/reset");
        assertThat(body).doesNotContain("Battery</dt>");
        assertThat(body).doesNotContain("Power</dt>");
        assertThat(body).doesNotContain("Health</dt>");
        assertThat(body).doesNotContain("Dock</dt>");
        assertThat(body).doesNotContain("Position</dt>");
        assertThat(body).contains("No telemetry available while offline.");
    }

    @Test
    void runEndpointReturnsMissionResultFragmentForHtmx() throws Exception {
        final String missionOnePath = createSessionAndGetMissionOnePath();
        final String missionTwoPath = missionOnePath.replace("/wake-the-core", "/charge-the-core");
        final MvcResult result = mockMvc.perform(post(missionOnePath + "/run")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header("HX-Request", "true")
                        .param("code", """
                                Core.connect();
                                System.out.println(\"Hello!!\");
                                """))
                .andExpect(status().isOk())
                .andReturn();
        final String body = result.getResponse().getContentAsString();

        assertThat(body).contains("CORE Online");
        assertThat(body).contains("CORE Status");
        assertThat(body).contains("CORE-01");
        assertThat(body).contains("Online");
        assertThat(body).contains("Power");
        assertThat(body).contains("0 / 5");
        assertThat(body).contains("Health");
        assertThat(body).contains("1 / 5");
        assertThat(body).contains("Program Output");
        assertThat(body).contains("stdout");
        assertThat(body).contains("Hello!!");
        assertThat(body).contains("readonly=\"readonly\"");
        assertThat(body).contains("Explain");
        assertThat(body).contains(">Next</a>");
        assertThat(body).contains(missionTwoPath);
        assertThat(body).doesNotContain(">Run</button>");
        assertThat(body).doesNotContain(">Reset</a>");
    }

    @Test
    void nextMissionPageRendersWithCarriedCodeFromSession() throws Exception {
        final String missionOnePath = createSessionAndGetMissionOnePath();
        final String missionTwoPath = missionOnePath.replace("/wake-the-core", "/charge-the-core");
        runMissionOne(missionOnePath, """
                Core.connect();
                System.out.println(\"Hello!!\");
                """);

        final MvcResult result = mockMvc.perform(get(missionTwoPath))
                .andExpect(status().isOk())
                .andReturn();
        final String body = result.getResponse().getContentAsString();

        assertThat(body).contains("Mission 02: Charge The CORE");
        assertThat(body).contains("Maintenance Room B-1049");
        assertThat(body).contains("Charge CORE-01 to full power.");
        assertThat(body).contains("Congratulations, engineer.");
        assertThat(body).contains("/audio/briefings/mission-02.mp3");
        assertThat(body).contains("Online");
        assertThat(body).contains("0 / 5");
        assertThat(body).contains("1 / 5");
        assertThat(body).contains("Connected");
        assertThat(body).contains("B1");
        assertThat(body).contains("name=\"initialCode\"");
        assertThat(body).contains("System.out.println(&quot;Hello!!&quot;);");
        assertThat(body).contains("""
                <textarea id="code" name="code" spellcheck="false">Core.connect();
                System.out.println(&quot;Hello!!&quot;);
                </textarea>""");
    }

    @Test
    void explainEndpointReturnsCodeExplanationWithoutRunningSimulation() throws Exception {
        final String missionOnePath = createSessionAndGetMissionOnePath();
        final MvcResult result = mockMvc.perform(post(missionOnePath + "/explain")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header("HX-Request", "true")
                        .param("code", "System.out.println(\"Hello!!\");"))
                .andExpect(status().isOk())
                .andReturn();
        final String body = result.getResponse().getContentAsString();

        assertThat(body).contains("Wake CORE-01 with one line of code");
        assertThat(body).contains("id=\"explain-section\"");
        assertThat(body).contains("Java is built around");
        assertThat(body).contains("Core.connect();");
        assertThat(body).contains("<pre><code>");
        assertThat(body).doesNotContain("```");
        assertThat(body).contains("<ul>");
        assertThat(body).contains("Creating Objects and Calling Methods");
    }

    @Test
    void completedMissionOneRemainsLockedAfterReload() throws Exception {
        final String missionOnePath = createSessionAndGetMissionOnePath();
        runMissionOne(missionOnePath, "Core.connect();");

        final MvcResult result = mockMvc.perform(get(missionOnePath))
                .andExpect(status().isOk())
                .andReturn();
        final String body = result.getResponse().getContentAsString();

        assertThat(body).contains("readonly=\"readonly\"");
        assertThat(body).contains("Explain");
        assertThat(body).contains(">Next</a>");
        assertThat(body).doesNotContain(">Run</button>");
        assertThat(body).doesNotContain(">Reset</a>");
    }

    @Test
    void missionTwoRunEndpointReturnsUpdatedBatteryState() throws Exception {
        final String missionOnePath = createSessionAndGetMissionOnePath();
        final String missionTwoPath = missionOnePath.replace("/wake-the-core", "/charge-the-core");
        final String missionThreePath = missionOnePath.replace("/wake-the-core", "/repair-the-core");
        runMissionOne(missionOnePath, "Core.connect();");

        final MvcResult result = mockMvc.perform(post(missionTwoPath + "/run")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header("HX-Request", "true")
                        .param("code", """
                                var core = Core.connect();
                                core.charge();
                                core.charge();
                                core.charge();
                                core.charge();
                                core.charge();
                                """))
                .andExpect(status().isOk())
                .andReturn();
        final String body = result.getResponse().getContentAsString();

        assertThat(body).contains("CORE Charged");
        assertThat(body).contains("5 / 5");
        assertThat(body).contains("Charged CORE-01 to 5/5.");
        assertThat(body).doesNotContain(">Run</button>");
        assertThat(body).doesNotContain(">Reset</a>");
        assertThat(body).contains(">Next</a>");
        assertThat(body).contains(missionThreePath);
        assertThat(body).contains("readonly=\"readonly\"");
        assertThat(body).contains("Explain");
    }

    @Test
    void missionThreePageRendersWithCarriedCodeFromMissionTwo() throws Exception {
        final String missionOnePath = createSessionAndGetMissionOnePath();
        final String missionTwoPath = missionOnePath.replace("/wake-the-core", "/charge-the-core");
        final String missionThreePath = missionOnePath.replace("/wake-the-core", "/repair-the-core");
        runMissionOne(missionOnePath, "Core.connect();");
        runMissionTwo(missionTwoPath, """
                var core = Core.connect();
                core.charge();
                core.charge();
                core.charge();
                core.charge();
                core.charge();
                """);

        final MvcResult result = mockMvc.perform(get(missionThreePath))
                .andExpect(status().isOk())
                .andReturn();
        final String body = result.getResponse().getContentAsString();

        assertThat(body).contains("Mission 03: Repair The CORE");
        assertThat(body).contains("Move CORE-01 to the repair station and repair it.");
        assertThat(body).contains("name=\"initialCode\"");
        assertThat(body).contains("var core = Core.connect();");
        assertThat(body).contains("core.charge();");
    }

    @Test
    void missionThreeRunEndpointRequiresForLoopForSuccess() throws Exception {
        final String missionOnePath = createSessionAndGetMissionOnePath();
        final String missionTwoPath = missionOnePath.replace("/wake-the-core", "/charge-the-core");
        final String missionThreePath = missionOnePath.replace("/wake-the-core", "/repair-the-core");
        runMissionOne(missionOnePath, "Core.connect();");
        runMissionTwo(missionTwoPath, """
                var core = Core.connect();
                core.charge();
                core.charge();
                core.charge();
                core.charge();
                core.charge();
                """);

        final MvcResult result = mockMvc.perform(post(missionThreePath + "/run")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header("HX-Request", "true")
                        .param("code", """
                                var core = Core.connect();
                                core.move();
                                core.move();
                                core.repair();
                                """))
                .andExpect(status().isOk())
                .andReturn();
        final String body = result.getResponse().getContentAsString();

        assertThat(body).contains("CORE Repaired");
        assertThat(body).contains("B3 · CORE unit on repair station");
        assertThat(body).contains("tile-core-repair");
        assertThat(body).contains("5 / 5");
        assertThat(body).doesNotContain(">Run</button>");
        assertThat(body).doesNotContain(">Reset</a>");
        assertThat(body).contains(">Next</button>");
        assertThat(body).contains("readonly=\"readonly\"");
    }

    @Test
    void missionThreeRunRendersCoreOnB2AfterSingleMove() throws Exception {
        final String missionOnePath = createSessionAndGetMissionOnePath();
        final String missionTwoPath = missionOnePath.replace("/wake-the-core", "/charge-the-core");
        final String missionThreePath = missionOnePath.replace("/wake-the-core", "/repair-the-core");
        runMissionOne(missionOnePath, "Core.connect();");
        runMissionTwo(missionTwoPath, """
                var core = Core.connect();
                core.charge();
                core.charge();
                core.charge();
                core.charge();
                core.charge();
                """);

        final MvcResult result = mockMvc.perform(post(missionThreePath + "/run")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("code", """
                                var core = Core.connect();
                                core.move();
                                """))
                .andExpect(status().isOk())
                .andReturn();
        final String body = result.getResponse().getContentAsString();

        assertThat(body).contains("B2 · CORE unit");
        assertThat(body).contains("tile-core-floor");
        assertThat(body).doesNotContain("B1 · Docked CORE unit");
        assertThat(body).contains("B1 · Docking station");
        assertThat(body).contains("tile-dock");
        assertThat(body).contains("4 / 5");
    }

    @Test
    void unknownGameSessionShowsExpiredSessionPage() throws Exception {
        final UUID gameSessionId = UUID.randomUUID();
        final MvcResult result = mockMvc.perform(get("/sessions/" + gameSessionId + "/missions/wake-the-core"))
                .andExpect(status().isNotFound())
                .andReturn();
        final String body = result.getResponse().getContentAsString();

        assertThat(body).contains("Session Expired");
        assertThat(body).contains("Start New Session");
        assertThat(body).contains("action=\"/game-sessions\"");
        assertThat(body).contains("<body class=\"flat-bg\">");
    }

    private String createSessionAndGetMissionOnePath() throws Exception {
        final MvcResult result = mockMvc.perform(post("/game-sessions"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/sessions/*/missions/wake-the-core"))
                .andReturn();
        final String location = result.getResponse().getRedirectedUrl();
        assertThat(location).isNotBlank();
        return location;
    }

    private void runMissionOne(final String missionOnePath, final String code) throws Exception {
        mockMvc.perform(post(missionOnePath + "/run")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header("HX-Request", "true")
                        .param("code", code))
                .andExpect(status().isOk());
    }

    private void runMissionTwo(final String missionTwoPath, final String code) throws Exception {
        mockMvc.perform(post(missionTwoPath + "/run")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header("HX-Request", "true")
                        .param("code", code))
                .andExpect(status().isOk());
    }
}
