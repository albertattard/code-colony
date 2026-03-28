package game.codecolony.web;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MissionBrowserSmokeTest {

    @LocalServerPort
    private int port;

    @Test
    void playAllMissions() {

        try (WebApplication application = new WebApplication(port)) {
            application.navigateToHomePage()
                    .assertThatPageShowsIntroBriefing()
                    .clickOnIntroNext()
                    .clickOnStart()
                    .waitForMission01Page()
                    .assertThatPageShowsMission01Briefing()
                    .seekBriefingAudioToTwoSeconds()
                    .clickOnClose()
                    .waitForBriefingModalToBeHidden()
                    .assertThatPageShowsMission01InitialState()
                    .fillCode("""
                            Core.connect();
                            System.out.println("Hello!!");
                            """)
                    .clickOnBriefing()
                    .waitForBriefingModalToBeVisible()
                    .clickOnClose()
                    .waitForBriefingModalToBeHidden()
                    .assertThatCodeIsEqualTo("""
                            Core.connect();
                            System.out.println("Hello!!");
                            """)
                    .clickRunAndWaitForWakeTheCoreRunResponse()
                    .assertThatPageShowsMission01CompletedState()
                    .clickOnNext()
                    .waitForMission02Page()
                    .assertThatPageShowsMission02BriefingAndInitialState()
                    .clickOnClose()
                    .waitForBriefingModalToBeHidden()
                    .fillCode("""
                            var core = Core.connect();
                            core.charge();
                            core.charge();
                            core.charge();
                            core.charge();
                            core.charge();
                            System.out.println("Charged");
                            """)
                    .clickRunAndWaitForChargeTheCoreRunResponse()
                    .assertThatPageShowsMission02CompletedState()
                    .clickOnNext()
                    .waitForMission03Page()
                    .assertThatPageShowsMission03InitialState()
                    .closeBriefingModalIfVisible()
                    .fillCode("""
                            var core = Core.connect();
                            core.move();
                            core.move();
                            core.repair();
                            core.repair();
                            core.repair();
                            core.repair();
                            System.out.println("Repaired");
                            """)
                    .clickRunAndWaitForRepairTheCoreRunResponse()
                    .assertThatPageShowsMission03CompletedState();
        }
    }

    private static class WebApplication implements AutoCloseable {

        private final Playwright playwright;
        private final Browser browser;
        private final Page page;
        private final int port;

        private WebApplication(final int port) {
            playwright = Playwright.create();
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            page = browser.newPage();

            this.port = port;
        }

        private WebApplication navigateToHomePage() {
            page.navigate(baseUrl() + "/", new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
            return this;
        }

        private WebApplication clickOnStart() {
            return clickOn("Start");
        }

        private WebApplication clickOnIntroNext() {
            return clickOn("Next");
        }

        private WebApplication clickOnClose() {
            return clickOn("Close");
        }

        private WebApplication clickOnBriefing() {
            return clickOn("Briefing");
        }

        private WebApplication clickOnNext() {
            clickOnLink(page, "Next");
            return this;
        }

        private WebApplication clickOn(final String name) {
            clickOnButton(page, name);
            return this;
        }

        private WebApplication clickRunAndWaitForWakeTheCoreRunResponse() {
            return clickRunAndWaitForResponse("wake-the-core");
        }

        private WebApplication clickRunAndWaitForChargeTheCoreRunResponse() {
            return clickRunAndWaitForResponse("charge-the-core");
        }

        private WebApplication clickRunAndWaitForRepairTheCoreRunResponse() {
            return clickRunAndWaitForResponse("repair-the-core");
        }

        private WebApplication clickRunAndWaitForResponse(final String name) {
            final Response response = page.waitForResponse(
                    runResponse -> runResponse.url().contains("/missions/" + name + "/run")
                                   && runResponse.url().contains("/sessions/"),
                    () -> clickOnButton(page, "Run"));
            assertThat(response.ok()).isTrue();
            page.waitForLoadState();
            return this;
        }

        private WebApplication waitForBriefingModalToBeHidden() {
            return waitForBriefingModalToBe(WaitForSelectorState.HIDDEN);
        }

        private WebApplication waitForBriefingModalToBeVisible() {
            return waitForBriefingModalToBe(WaitForSelectorState.VISIBLE);
        }

        private WebApplication waitForBriefingModalToBe(final WaitForSelectorState state) {
            page.locator("[data-briefing-modal]").waitFor(new Locator.WaitForOptions().setState(state));
            return this;
        }

        private WebApplication waitForMission01Page() {
            return waitForPage("**/sessions/**/missions/wake-the-core");
        }

        private WebApplication waitForMission02Page() {
            return waitForPage("**/sessions/**/missions/charge-the-core**");
        }

        private WebApplication waitForMission03Page() {
            return waitForPage("**/sessions/**/missions/repair-the-core**");
        }

        private WebApplication waitForPage(final String url) {
            page.waitForURL(url);
            return this;
        }

        private WebApplication seekBriefingAudioToTwoSeconds() {
            page.locator("[data-briefing-modal] audio")
                    .evaluate("""
                            audio => {
                                audio.currentTime = 2;
                                return audio.currentTime;
                            }
                            """);
            return this;
        }

        private WebApplication fillCode(final String code) {
            page.locator("textarea[name='code']")
                    .fill(code);
            return this;
        }

        private WebApplication assertThatPageShowsIntroBriefing() {
            assertThat(page.locator(".mission-header h1").textContent()).contains("Mission Briefing");
            assertThat(page.locator("body").textContent()).contains("Investigate the colony site and restore critical systems in stages.");
            assertThat(page.locator("body").textContent()).contains("Operational Briefing");
            assertThat(page.locator("body").textContent()).contains("Eryndor-IV");
            assertThat(page.locator("body").textContent()).contains("Colony Operations and Repair Engineers");
            assertThat(page.locator("body").textContent()).contains("Legacy Control Interface");
            assertThat(page.locator("body").textContent()).contains("Each Run starts from the mission start state");
            assertThat(page.locator(".intro-diagram img").getAttribute("src")).isEqualTo("/images/intro/mission-flow.svg");
            assertThat(page.locator("audio source").getAttribute("src")).isEqualTo("/audio/briefings/intro.mp3");
            return this;
        }

        private WebApplication assertThatPageShowsMission01Briefing() {
            assertThat(page.locator("h1").textContent()).contains("Mission 01: Wake The CORE");
            assertThat(page.locator("[data-briefing-modal]").isVisible()).isTrue();
            assertThat(page.locator("[data-briefing-modal]").textContent()).contains("Mission Briefing");
            assertThat(page.locator("[data-briefing-modal]").textContent()).contains("Core.connect();");
            assertThat(page.locator("[data-briefing-modal]").textContent()).contains("The only way to communicate with the unit is by issuing Java commands through the terminal.");
            assertThat(page.locator("[data-briefing-modal] audio source").getAttribute("src")).isEqualTo("/audio/briefings/mission-01.mp3");
            assertThat(page.locator("[data-briefing-modal] audio").evaluate("audio => audio.currentTime")).isEqualTo(0);
            return this;
        }

        private WebApplication assertThatPageShowsMission01InitialState() {
            assertThat(page.locator("[data-briefing-modal]").isVisible()).isFalse();
            assertThat(page.locator("[data-briefing-modal] audio").evaluate("audio => audio.currentTime")).isEqualTo(0);
            assertThat(page.locator("[data-briefing-modal] audio").evaluate("audio => audio.paused")).isEqualTo(true);
            assertThat(page.locator(".grid-panel").textContent()).contains("Maintenance Room B-1049");
            assertThat(page.locator(".code-panel").textContent()).contains("Code Console");
            assertThat(page.locator(".status-panel").textContent()).contains("Offline");
            assertThat(page.locator(".status-panel").textContent()).contains("CORE-01");
            assertThat(page.locator(".status-panel").textContent()).doesNotContain("Power");
            assertThat(page.locator(".status-panel").textContent()).doesNotContain("Health");
            assertThat(page.locator(".status-panel").textContent()).doesNotContain("Dock");
            assertThat(page.locator(".status-panel").textContent()).doesNotContain("Position");
            assertThat(page.locator("textarea[name='code']").inputValue()).isEmpty();
            return this;
        }

        private WebApplication assertThatCodeIsEqualTo(final String expected) {
            assertThat(page.locator("textarea[name='code']").inputValue()).isEqualTo(expected);
            return this;
        }

        private WebApplication assertThatPageShowsMission01CompletedState() {
            assertThat(page.locator(".status-panel").textContent()).contains("Online");
            assertThat(page.locator(".status-panel").textContent()).contains("CORE-01");
            assertThat(page.locator(".status-panel").textContent()).contains("Power");
            assertThat(page.locator(".status-panel").textContent()).contains("0 / 5");
            assertThat(page.locator(".status-panel").textContent()).contains("Health");
            assertThat(page.locator(".status-panel").textContent()).contains("1 / 5");
            assertThat(page.locator(".meter-battery .status-meter-box").count()).isEqualTo(5);
            assertThat(page.locator(".meter-battery .status-meter-box.filled").count()).isZero();
            assertThat(page.locator(".meter-health .status-meter-box").count()).isEqualTo(5);
            assertThat(page.locator(".meter-health .status-meter-box.filled").count()).isEqualTo(1);
            assertThat(page.locator(".feedback-panel").textContent()).contains("CORE Online");
            assertThat(page.locator(".feedback-panel").textContent())
                    .contains("Control link established. CORE-01 is online, but telemetry shows a depleted battery and structural damage.");
            assertThat(page.locator(".output-panel").textContent()).contains("Program Output");
            assertThat(page.locator(".output-panel").textContent()).contains("stdout");
            assertThat(page.locator(".output-panel").textContent()).contains("Hello!!");
            assertThat(page.locator("textarea[name='code']").getAttribute("readonly")).isEqualTo("readonly");
            assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Run")).count()).isZero();
            assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Reset")).count()).isZero();
            assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Next")).isVisible()).isTrue();
            return this;
        }

        private WebApplication assertThatPageShowsMission02BriefingAndInitialState() {
            assertThat(page.locator("h1").textContent()).contains("Mission 02: Charge The CORE");
            assertThat(page.locator("[data-briefing-modal]").isVisible()).isTrue();
            assertThat(page.locator("[data-briefing-modal]").textContent()).contains("Congratulations, engineer.");
            assertThat(page.locator("[data-briefing-modal] audio source").getAttribute("src"))
                    .isEqualTo("/audio/briefings/mission-02.mp3");
            assertThat(page.locator(".status-panel").textContent()).contains("Online");
            assertThat(page.locator(".status-panel").textContent()).contains("0 / 5");
            assertThat(page.locator(".status-panel").textContent()).contains("1 / 5");
            assertThat(page.locator(".status-panel").textContent()).contains("Connected");
            assertThat(page.locator(".status-panel").textContent()).contains("B1");
            assertThat(page.locator("textarea[name='code']").inputValue()).isEqualTo("""
                    Core.connect();
                    System.out.println("Hello!!");
                    """);
            return this;
        }

        private WebApplication assertThatPageShowsMission02CompletedState() {
            assertThat(page.locator(".feedback-panel").textContent()).contains("CORE Charged");
            assertThat(page.locator(".status-panel").textContent()).contains("5 / 5");
            assertThat(page.locator(".meter-battery .status-meter-box.filled").count()).isEqualTo(5);
            assertThat(page.locator(".output-panel").textContent()).contains("Charged");
            assertThat(page.locator("textarea[name='code']").getAttribute("readonly")).isEqualTo("readonly");
            assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Run")).count()).isZero();
            assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Reset")).count()).isZero();
            assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Next")).isVisible()).isTrue();
            return this;
        }

        private WebApplication assertThatPageShowsMission03InitialState() {
            assertThat(page.locator("h1").textContent()).contains("Mission 03: Repair The CORE");
            assertThat(page.locator(".code-panel").textContent()).contains("core.repair()");
            assertThat(page.locator("textarea[name='code']").inputValue()).contains("var core = Core.connect();");
            return this;
        }

        private WebApplication assertThatPageShowsMission03CompletedState() {
            assertThat(page.locator(".feedback-panel").textContent()).contains("CORE Repaired");
            assertThat(page.locator(".status-panel").textContent()).contains("5 / 5");
            assertThat(page.locator(".output-panel").textContent()).contains("Repaired");
            assertThat(page.locator(".tile-core-repair").count()).isEqualTo(1);
            assertThat(page.locator("textarea[name='code']").getAttribute("readonly")).isEqualTo("readonly");
            assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Run")).count()).isZero();
            assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Reset")).count()).isZero();
            return this;
        }

        private WebApplication closeBriefingModalIfVisible() {
            if (page.locator("[data-briefing-modal]").isVisible()) {
                clickOnClose();
                waitForBriefingModalToBeHidden();
            }
            return this;
        }

        @Override
        public void close() {
            browser.close();
            playwright.close();
        }

        private String baseUrl() {
            return "http://localhost:" + port;
        }

        private static void clickOnButton(final Page page, final String name) {
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(name)).click();
        }

        private static void clickOnLink(final Page page, final String name) {
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(name)).click();
        }
    }
}
