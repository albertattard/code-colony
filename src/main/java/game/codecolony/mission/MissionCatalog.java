package game.codecolony.mission;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public final class MissionCatalog {

    private static final Path MISSIONS_DIRECTORY = Path.of("content", "missions");
    private static final Set<String> REQUIRED_FILES = Set.of("content.md", "map.yaml", "mission.yaml");

    private MissionCatalog() {
    }

    public static List<String> currentMissionIds() {
        try (Stream<Path> stream = Files.list(MISSIONS_DIRECTORY)) {
            return stream
                    .filter(Files::isDirectory)
                    .map(path -> path.getFileName().toString())
                    .sorted(Comparator.naturalOrder())
                    .toList();
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to discover missions under " + MISSIONS_DIRECTORY.toAbsolutePath(),
                    exception
            );
        }
    }

    public static boolean isCurrentMission(final String missionId) {
        return currentMissionIds().contains(missionId);
    }

    public static void requireMissionFiles(final String missionId) {
        final Path missionDirectory = MISSIONS_DIRECTORY.resolve(missionId);
        if (!Files.isDirectory(missionDirectory)) {
            throw new IllegalStateException("Missing mission directory: " + missionDirectory.toAbsolutePath());
        }

        for (final String requiredFile : REQUIRED_FILES) {
            final Path requiredPath = missionDirectory.resolve(requiredFile);
            if (!Files.isRegularFile(requiredPath)) {
                throw new IllegalStateException("Missing required mission file: " + requiredPath.toAbsolutePath());
            }
        }
    }
}
