package game.codecolony.mission;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public final class MissionCatalog {

    private static final Path MISSIONS_DIRECTORY = Path.of("content", "missions");
    private static final Set<String> REQUIRED_FILES = Set.of("content.md", "map.yaml", "mission.yaml");
    private static final MissionManifestLoader MANIFEST_LOADER = new MissionManifestLoader();

    private MissionCatalog() {
    }

    public static List<String> currentMissionIds() {
        return MANIFEST_LOADER.load().enabledMissions().stream()
                .map(MissionManifestEntry::content)
                .toList();
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
