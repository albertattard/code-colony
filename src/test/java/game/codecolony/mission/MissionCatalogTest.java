package game.codecolony.mission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

class MissionCatalogTest {

    private static final Path MISSIONS_DIRECTORY = Path.of("content", "missions");

    @Test
    void discoversCurrentMissionIdsFromManifestOrder() {
        assertThat(MissionCatalog.currentMissionIds())
                .containsExactly("mission-01", "mission-02", "mission-03", "mission-04");
    }

    @Test
    void reportsMissionAsCurrentWhenDirectoryExists() {
        assertThat(MissionCatalog.isCurrentMission("mission-01")).isTrue();
        assertThat(MissionCatalog.isCurrentMission("mission-does-not-exist")).isFalse();
    }

    @Test
    void requireMissionFilesFailsWhenDirectoryIsMissing() {
        assertThatThrownBy(() -> MissionCatalog.requireMissionFiles("mission-does-not-exist"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Missing mission directory");
    }

    @Test
    void requireMissionFilesFailsWhenRequiredFileIsMissing() throws IOException {
        final String missionId = "mission-test-missing-file";
        final Path missionDirectory = MISSIONS_DIRECTORY.resolve(missionId);

        Files.createDirectories(missionDirectory);
        Files.writeString(missionDirectory.resolve("content.md"), "# Test\n");
        Files.writeString(missionDirectory.resolve("map.yaml"), "version: 1\n");

        try {
            assertThatThrownBy(() -> MissionCatalog.requireMissionFiles(missionId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Missing required mission file");
        } finally {
            deleteDirectoryRecursively(missionDirectory);
        }
    }

    private static void deleteDirectoryRecursively(final Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }

        final List<Path> paths;
        try (var walk = Files.walk(root)) {
            paths = walk.toList();
        }

        for (int index = paths.size() - 1; index >= 0; index--) {
            Files.deleteIfExists(paths.get(index));
        }
    }
}
