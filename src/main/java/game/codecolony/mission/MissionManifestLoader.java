package game.codecolony.mission;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MissionManifestLoader {

    private static final Path MANIFEST_PATH = Path.of("content", "missions", "missions.yaml");

    public MissionManifest load() {
        final String yaml = loadText(MANIFEST_PATH);
        return parseYaml(yaml, MANIFEST_PATH.toString());
    }

    static MissionManifest parseYaml(final String yamlText, final String sourceName) {
        final Yaml yaml = new Yaml();
        final Object loaded = yaml.load(yamlText);
        if (!(loaded instanceof Map<?, ?> root)) {
            throw new IllegalStateException("Invalid missions manifest %s: root must be a mapping".formatted(sourceName));
        }

        final int version = requireInt(root, "version", sourceName);
        final List<?> rawMissions = requireList(root, "missions", sourceName);
        if (rawMissions.isEmpty()) {
            throw new IllegalStateException("Invalid missions manifest %s: 'missions' must not be empty".formatted(sourceName));
        }

        final List<MissionManifestEntry> missions = new ArrayList<>();
        final Set<String> names = new HashSet<>();
        final Set<String> contentIds = new HashSet<>();

        for (int index = 0; index < rawMissions.size(); index++) {
            final Object rawMission = rawMissions.get(index);
            if (!(rawMission instanceof Map<?, ?> missionMap)) {
                throw new IllegalStateException(
                        "Invalid missions manifest %s: mission entry %d must be a mapping".formatted(sourceName, index + 1)
                );
            }

            final String name = requireString(missionMap, "name", sourceName);
            if (!name.matches("[a-z0-9]+(?:-[a-z0-9]+)*")) {
                throw new IllegalStateException(
                        "Invalid missions manifest %s: mission name '%s' must be a slug".formatted(sourceName, name)
                );
            }
            if (!names.add(name)) {
                throw new IllegalStateException(
                        "Invalid missions manifest %s: duplicate mission name '%s'".formatted(sourceName, name)
                );
            }

            final String content = requireString(missionMap, "content", sourceName);
            if (!contentIds.add(content)) {
                throw new IllegalStateException(
                        "Invalid missions manifest %s: duplicate mission content '%s'".formatted(sourceName, content)
                );
            }

            final boolean enabled = parseEnabled(missionMap, sourceName);
            MissionCatalog.requireMissionFiles(content);
            missions.add(new MissionManifestEntry(name, content, enabled));
        }

        if (missions.stream().noneMatch(MissionManifestEntry::enabled)) {
            throw new IllegalStateException("Invalid missions manifest %s: at least one mission must be enabled".formatted(sourceName));
        }

        return new MissionManifest(version, List.copyOf(missions));
    }

    private static boolean parseEnabled(final Map<?, ?> missionMap, final String sourceName) {
        final Object value = missionMap.get("enabled");
        if (value == null) {
            return true;
        }
        if (value instanceof Boolean boolValue) {
            return boolValue;
        }
        throw new IllegalStateException(
                "Invalid missions manifest %s: 'enabled' must be a boolean when present".formatted(sourceName)
        );
    }

    private static List<?> requireList(final Map<?, ?> source, final String key, final String sourceName) {
        final Object value = source.get(key);
        if (!(value instanceof List<?> listValue)) {
            throw new IllegalStateException("Invalid missions manifest %s: '%s' must be a list".formatted(sourceName, key));
        }
        return listValue;
    }

    private static String requireString(final Map<?, ?> source, final String key, final String sourceName) {
        final Object value = source.get(key);
        if (!(value instanceof String stringValue) || stringValue.isBlank()) {
            throw new IllegalStateException(
                    "Invalid missions manifest %s: '%s' must be a non-blank string".formatted(sourceName, key)
            );
        }
        return stringValue;
    }

    private static int requireInt(final Map<?, ?> source, final String key, final String sourceName) {
        final Object value = source.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }

        throw new IllegalStateException("Invalid missions manifest %s: '%s' must be an integer".formatted(sourceName, key));
    }

    private static String loadText(final Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            throw new IllegalStateException("Unable to load missions manifest: " + path.toAbsolutePath(), ioException);
        }
    }
}
