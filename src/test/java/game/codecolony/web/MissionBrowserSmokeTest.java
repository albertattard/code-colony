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
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Response;

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

        assertThat(page.locator(".intro-briefing h1").textContent()).contains("Code Colony");
        assertThat(page.locator("body").textContent()).contains("Helix Dynamics Briefing");
        assertThat(page.locator("body").textContent()).contains("Eryndor-IV");
        assertThat(page.locator("body").textContent()).contains("Colony Operations and Repair Engineers");

        page.getByRole(com.microsoft.playwright.options.AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Start Mission"))
                .click();
        page.waitForURL("**/missions/wake-the-core");

        assertThat(page.locator("h1").textContent()).contains("Mission 01: Wake The CORE");
        assertThat(page.locator(".grid-panel").textContent()).contains("Maintenance Room Grid");
        assertThat(page.locator(".code-panel").textContent()).contains("Code Console");
        assertThat(page.locator(".status-panel").textContent()).contains("Offline");
        assertThat(page.locator(".status-panel").textContent()).doesNotContain("Battery");
        assertThat(page.locator(".status-panel").textContent()).doesNotContain("Dock");
        assertThat(page.locator(".status-panel").textContent()).doesNotContain("Position");
        assertThat(page.locator("textarea[name='code']").inputValue()).isEmpty();

        page.locator("textarea[name='code']").fill("CORE.connect();");
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
