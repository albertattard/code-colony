package game.codecolony.web;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.WaitForSelectorState;

@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MissionBrowserSmokeTest {

    @LocalServerPort
    private int port;

    private Playwright playwright;
    private Browser browser;

    @BeforeAll
    void setUpBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    void tearDownBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @Test
    void introPageLeadsIntoMissionOneMissionTwoAndMissionThree() {
        final Page page = browser.newPage();

        page.navigate(baseUrl() + "/",
                new Page.NavigateOptions().setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED));

        assertThat(page.locator(".mission-header h1").textContent()).contains("Mission Briefing");
        assertThat(page.locator("body").textContent())
                .contains("Investigate the colony site and restore critical systems in stages.");
        assertThat(page.locator("body").textContent()).contains("Operational Briefing");
        assertThat(page.locator("body").textContent()).contains("Eryndor-IV");
        assertThat(page.locator("body").textContent()).contains("Colony Operations and Repair Engineers");
        assertThat(page.locator("audio source").getAttribute("src")).isEqualTo("/audio/briefings/intro.mp3");

        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Start"))
                .click();
        page.waitForURL("**/sessions/**/missions/wake-the-core");

        assertThat(page.locator("h1").textContent()).contains("Mission 01: Wake The CORE");
        assertThat(page.locator("[data-briefing-modal]").isVisible()).isTrue();
        assertThat(page.locator("[data-briefing-modal]").textContent()).contains("Mission Briefing");
        assertThat(page.locator("[data-briefing-modal]").textContent()).contains("Core.connect();");
        assertThat(page.locator("[data-briefing-modal]").textContent())
                .contains("The only way to communicate with the unit is by issuing Java commands through the terminal.");
        assertThat(page.locator("[data-briefing-modal] audio source").getAttribute("src"))
                .isEqualTo("/audio/briefings/mission-01.mp3");
        page.locator("[data-briefing-modal] audio").evaluate("""
                audio => {
                    audio.currentTime = 2;
                    return audio.currentTime;
                }
                """);
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Close"))
                .click();
        page.locator("[data-briefing-modal]")
                .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        assertThat(page.locator("[data-briefing-modal]").isVisible()).isFalse();
        assertThat(page.locator("[data-briefing-modal] audio").evaluate("audio => audio.currentTime"))
                .isEqualTo(0);
        assertThat(page.locator("[data-briefing-modal] audio").evaluate("audio => audio.paused"))
                .isEqualTo(true);
        assertThat(page.locator(".grid-panel").textContent()).contains("Maintenance Room B-1049");
        assertThat(page.locator(".code-panel").textContent()).contains("Code Console");
        assertThat(page.locator(".status-panel").textContent()).contains("Offline");
        assertThat(page.locator(".status-panel").textContent()).contains("CORE-01");
        assertThat(page.locator(".status-panel").textContent()).doesNotContain("Power");
        assertThat(page.locator(".status-panel").textContent()).doesNotContain("Health");
        assertThat(page.locator(".status-panel").textContent()).doesNotContain("Dock");
        assertThat(page.locator(".status-panel").textContent()).doesNotContain("Position");
        assertThat(page.locator("textarea[name='code']").inputValue()).isEmpty();

        page.locator("textarea[name='code']").fill("""
                Core.connect();
                System.out.println("Hello!!");
                """);
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Briefing"))
                .click();
        page.locator("[data-briefing-modal]")
                .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        assertThat(page.locator("[data-briefing-modal]").isVisible()).isTrue();
        assertThat(page.locator("[data-briefing-modal] audio").evaluate("audio => audio.currentTime"))
                .isEqualTo(0);
        assertThat(page.locator("textarea[name='code']").inputValue()).isEqualTo("""
                Core.connect();
                System.out.println("Hello!!");
                """);
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Close"))
                .click();
        page.locator("[data-briefing-modal]")
                .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));

        final Response response = page.waitForResponse(
                runResponse -> runResponse.url().contains("/missions/wake-the-core/run")
                        && runResponse.url().contains("/sessions/"),
                () -> page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                        new Page.GetByRoleOptions().setName("Run"))
                        .click()
        );
        assertThat(response.ok()).isTrue();
        page.waitForLoadState();

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
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }
}
