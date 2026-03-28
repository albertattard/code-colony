package game.codecolony.mission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class MissionManifestLoaderTest {

    private final MissionManifestLoader loader = new MissionManifestLoader();

    @Test
    void loadsManifestFromFilesystem() {
        final MissionManifest manifest = loader.load();

        assertThat(manifest.version()).isEqualTo(1);
        assertThat(manifest.missions()).containsExactly(
                new MissionManifestEntry("wake-the-core", "mission-01", true),
                new MissionManifestEntry("charge-the-core", "mission-02", true),
                new MissionManifestEntry("repair-the-core", "mission-03", true)
        );
        assertThat(manifest.enabledMissions()).hasSize(3);
    }

    @Test
    void parseYamlFailsWhenMissionNamesAreDuplicated() {
        final String yaml = """
                version: 1
                missions:
                  - name: wake-the-core
                    content: mission-01
                  - name: wake-the-core
                    content: mission-02
                """;

        assertThatThrownBy(() -> MissionManifestLoader.parseYaml(yaml, "inline"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("duplicate mission name");
    }

    @Test
    void parseYamlFailsWhenMissionContentIsDuplicated() {
        final String yaml = """
                version: 1
                missions:
                  - name: wake-the-core
                    content: mission-01
                  - name: charge-the-core
                    content: mission-01
                """;

        assertThatThrownBy(() -> MissionManifestLoader.parseYaml(yaml, "inline"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("duplicate mission content");
    }

    @Test
    void parseYamlFailsWhenMissionNameIsNotSlug() {
        final String yaml = """
                version: 1
                missions:
                  - name: Wake The Core
                    content: mission-01
                """;

        assertThatThrownBy(() -> MissionManifestLoader.parseYaml(yaml, "inline"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("must be a slug");
    }

    @Test
    void parseYamlFailsWhenAllMissionsAreDisabled() {
        final String yaml = """
                version: 1
                missions:
                  - name: wake-the-core
                    content: mission-01
                    enabled: false
                  - name: charge-the-core
                    content: mission-02
                    enabled: false
                """;

        assertThatThrownBy(() -> MissionManifestLoader.parseYaml(yaml, "inline"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least one mission must be enabled");
    }
}
