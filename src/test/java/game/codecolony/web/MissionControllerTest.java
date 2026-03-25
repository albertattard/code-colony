package game.codecolony.web;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MissionControllerTest {

    @LocalServerPort
    private int port;

    @Test
    void homePageRendersIntroScreen() throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder(baseUri("/"))
                .GET()
                .build();
        final String body = send(request);

        assertThat(body).contains("Mission Briefing");
        assertThat(body).contains("Investigate the colony site and restore critical systems in stages.");
        assertThat(body).contains("Operational Briefing");
        assertThat(body).contains("Eryndor-IV");
        assertThat(body).contains("Colony Operations and Repair Engineers");
        assertThat(body).contains("/audio/briefings/intro.mp3");
        assertThat(body).contains("Start Mission");
        assertThat(body).contains("/missions/wake-the-core");
        assertThat(body).doesNotContain("Mission 01: Wake The CORE");
    }

    @Test
    void missionPageRenders() throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder(baseUri("/missions/wake-the-core"))
                .GET()
                .build();
        final String body = send(request);

        assertThat(body).contains("Mission 01: Wake The CORE");
        assertThat(body).contains("Maintenance Room Grid");
        assertThat(body).contains("Code Console");
        assertThat(body).contains("Mission Briefing");
        assertThat(body).contains("The only way to communicate with the unit is by issuing Java commands through the terminal.");
        assertThat(body).contains("<code>CORE.connect();</code>");
        assertThat(body).contains("/audio/briefings/mission-01.mp3");
        assertThat(body).contains("data-briefing-modal");
        assertThat(body).contains("data-briefing-open");
        assertThat(body).contains("CORE Status");
        assertThat(body).contains("Unit");
        assertThat(body).contains("CORE-01");
        assertThat(body).doesNotContain("Program Output");
        assertThat(body).contains("<textarea id=\"code\" name=\"code\" spellcheck=\"false\"></textarea>");
        assertThat(body).doesNotContain("Battery</dt>");
        assertThat(body).doesNotContain("Power</dt>");
        assertThat(body).doesNotContain("Health</dt>");
        assertThat(body).doesNotContain("Dock</dt>");
        assertThat(body).doesNotContain("Position</dt>");
        assertThat(body).contains("No telemetry available while offline.");
    }

    @Test
    void runEndpointReturnsMissionResultFragmentForHtmx() throws IOException, InterruptedException {
        final String formBody = "code=" + URLEncoder.encode("""
                CORE.connect();
                System.out.println("Hello!!");
                """, StandardCharsets.UTF_8);
        final HttpRequest request = HttpRequest.newBuilder(baseUri("/missions/wake-the-core/run"))
                .header("HX-Request", "true")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
                .build();
        final HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("CORE Online");
        assertThat(response.body()).contains("CORE Status");
        assertThat(response.body()).contains("CORE-01");
        assertThat(response.body()).contains("Online");
        assertThat(response.body()).contains("Power");
        assertThat(response.body()).contains("0 / 5");
        assertThat(response.body()).contains("Health");
        assertThat(response.body()).contains("1 / 5");
        assertThat(response.body()).contains("Program Output");
        assertThat(response.body()).contains("stdout");
        assertThat(response.body()).contains("Hello!!");
    }

    private URI baseUri(final String path) {
        return URI.create("http://localhost:" + port + path);
    }

    private String send(final HttpRequest request) throws IOException, InterruptedException {
        return HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString())
                .body();
    }
}
