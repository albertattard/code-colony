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

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MissionBrowserSmokeTest {

    @LocalServerPort
    private int port;

    @Test
    void introPageLeadsIntoMissionOneMissionTwoAndMissionThree() {

        try (WebApplication application = new WebApplication(port)) {
            application.navigateToHomePage()
                    .assertThatPageShowsIntroBriefing()
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
                    .withPage(page -> {
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
                        assertThat(page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                                new Page.GetByRoleOptions().setName("Run")).count()).isZero();
                        assertThat(page.getByRole(com.microsoft.playwright.options.AriaRole.LINK,
                                new Page.GetByRoleOptions().setName("Reset")).count()).isZero();
                        assertThat(page.getByRole(com.microsoft.playwright.options.AriaRole.LINK,
                                new Page.GetByRoleOptions().setName("Next")).isVisible()).isTrue();

                        page.getByRole(com.microsoft.playwright.options.AriaRole.LINK,
                                        new Page.GetByRoleOptions().setName("Next"))
                                .click();
                        page.waitForURL("**/sessions/**/missions/charge-the-core**");
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
                        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                                        new Page.GetByRoleOptions().setName("Close"))
                                .click();
                        page.locator("[data-briefing-modal]")
                                .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));

                        page.locator("textarea[name='code']").fill("""
                                var core = Core.connect();
                                core.charge();
                                core.charge();
                                core.charge();
                                core.charge();
                                core.charge();
                                System.out.println("Charged");
                                """);

                        final Response missionTwoResponse = page.waitForResponse(
                                runResponse -> runResponse.url().contains("/missions/charge-the-core/run")
                                               && runResponse.url().contains("/sessions/"),
                                () -> page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                                                new Page.GetByRoleOptions().setName("Run"))
                                        .click()
                        );
                        assertThat(missionTwoResponse.ok()).isTrue();
                        page.waitForLoadState();

                        assertThat(page.locator(".feedback-panel").textContent()).contains("CORE Charged");
                        assertThat(page.locator(".status-panel").textContent()).contains("5 / 5");
                        assertThat(page.locator(".meter-battery .status-meter-box.filled").count()).isEqualTo(5);
                        assertThat(page.locator(".output-panel").textContent()).contains("Charged");
                        assertThat(page.locator("textarea[name='code']").getAttribute("readonly")).isEqualTo("readonly");
                        assertThat(page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                                new Page.GetByRoleOptions().setName("Run")).count()).isZero();
                        assertThat(page.getByRole(com.microsoft.playwright.options.AriaRole.LINK,
                                new Page.GetByRoleOptions().setName("Reset")).count()).isZero();
                        assertThat(page.getByRole(com.microsoft.playwright.options.AriaRole.LINK,
                                new Page.GetByRoleOptions().setName("Next")).isVisible()).isTrue();

                        page.getByRole(com.microsoft.playwright.options.AriaRole.LINK,
                                        new Page.GetByRoleOptions().setName("Next"))
                                .click();
                        page.waitForURL("**/sessions/**/missions/repair-the-core**");
                        assertThat(page.locator("h1").textContent()).contains("Mission 03: Repair The CORE");
                        assertThat(page.locator(".code-panel").textContent()).contains("core.repair()");
                        assertThat(page.locator("textarea[name='code']").inputValue()).contains("var core = Core.connect();");
                    });
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

        private WebApplication clickOnClose() {
            return clickOn("Close");
        }

        private WebApplication clickOnBriefing() {
            return clickOn("Briefing");
        }

        private WebApplication clickOn(final String name) {
            clickOn(page, name);
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

        private WebApplication withPage(final Consumer<Page> consumer) {
            consumer.accept(page);
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

        private WebApplication clickRunAndWaitForWakeTheCoreRunResponse() {
            final Response response = page.waitForResponse(
                    runResponse -> runResponse.url().contains("/missions/wake-the-core/run")
                                   && runResponse.url().contains("/sessions/"),
                    () -> clickOn(page, "Run"));
            assertThat(response.ok()).isTrue();
            page.waitForLoadState();
            return this;
        }

        private static void clickOn(final Page page, final String name) {
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(name)).click();
        }
    }
}
