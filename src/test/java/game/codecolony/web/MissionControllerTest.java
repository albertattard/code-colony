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
    void missionPageRenders() throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder(baseUri("/missions/wake-the-core"))
                .GET()
                .build();
        final String body = send(request);

        assertThat(body).contains("Mission 01: Wake The CORE");
        assertThat(body).contains("Maintenance Room Grid");
        assertThat(body).contains("Code Console");
        assertThat(body).contains("CORE Status");
    }

    @Test
    void runEndpointReturnsPlaceholderFragmentForHtmx() throws IOException, InterruptedException {
        final String formBody = "code=" + URLEncoder.encode("core.moveEast();", StandardCharsets.UTF_8);
        final HttpRequest request = HttpRequest.newBuilder(baseUri("/missions/wake-the-core/run"))
                .header("HX-Request", "true")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
                .build();
        final HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("Placeholder Run Complete");
        assertThat(response.body()).contains("CORE Status");
        assertThat(response.body()).contains("Offline");
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
