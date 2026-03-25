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
    void introPageLeadsIntoMissionAndRunUpdatesCoreStatus() {
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

        page.getByRole(com.microsoft.playwright.options.AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Start Mission"))
                .click();
        page.waitForURL("**/missions/wake-the-core");

        assertThat(page.locator("h1").textContent()).contains("Mission 01: Wake The CORE");
        assertThat(page.locator("[data-briefing-modal]").isVisible()).isTrue();
        assertThat(page.locator("[data-briefing-modal]").textContent()).contains("Mission Briefing");
        assertThat(page.locator("[data-briefing-modal]").textContent()).contains("CORE.connect();");
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
                new Page.GetByRoleOptions().setName("Close Briefing"))
                .click();
        page.locator("[data-briefing-modal]")
                .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        assertThat(page.locator("[data-briefing-modal]").isVisible()).isFalse();
        assertThat(page.locator("[data-briefing-modal] audio").evaluate("audio => audio.currentTime"))
                .isEqualTo(0);
        assertThat(page.locator("[data-briefing-modal] audio").evaluate("audio => audio.paused"))
                .isEqualTo(true);
        assertThat(page.locator(".grid-panel").textContent()).contains("Maintenance Room Grid");
        assertThat(page.locator(".code-panel").textContent()).contains("Code Console");
        assertThat(page.locator(".status-panel").textContent()).contains("Offline");
        assertThat(page.locator(".status-panel").textContent()).doesNotContain("Battery");
        assertThat(page.locator(".status-panel").textContent()).doesNotContain("Dock");
        assertThat(page.locator(".status-panel").textContent()).doesNotContain("Position");
        assertThat(page.locator("textarea[name='code']").inputValue()).isEmpty();

        page.locator("textarea[name='code']").fill("CORE.connect();");
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Briefing"))
                .click();
        page.locator("[data-briefing-modal]")
                .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        assertThat(page.locator("[data-briefing-modal]").isVisible()).isTrue();
        assertThat(page.locator("[data-briefing-modal] audio").evaluate("audio => audio.currentTime"))
                .isEqualTo(0);
        assertThat(page.locator("textarea[name='code']").inputValue()).isEqualTo("CORE.connect();");
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Close Briefing"))
                .click();
        page.locator("[data-briefing-modal]")
                .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));

        final Response response = page.waitForResponse(
                runResponse -> runResponse.url().contains("/missions/wake-the-core/run"),
                () -> page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                        new Page.GetByRoleOptions().setName("Run"))
                        .click()
        );
        assertThat(response.ok()).isTrue();
        page.waitForLoadState();

        assertThat(page.locator(".status-panel").textContent()).contains("Online");
        assertThat(page.locator(".feedback-panel").textContent()).contains("CORE Online");
        assertThat(page.locator(".feedback-panel").textContent())
                .contains("Control link established. CORE-01 is online.");
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }
}
