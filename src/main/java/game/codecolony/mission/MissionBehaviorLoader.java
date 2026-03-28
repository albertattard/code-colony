package game.codecolony.mission;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MissionBehaviorLoader {

    private static final String BEHAVIOR_RESOURCE_PATH_TEMPLATE = "content/missions/%s/mission.yaml";

    public MissionBehaviorConfig load(final String missionId) {
        final String resourcePath = BEHAVIOR_RESOURCE_PATH_TEMPLATE.formatted(missionId);
        final String yaml = loadText(resourcePath);
        return parseYamlForMission(yaml, resourcePath, missionId);
    }

    static MissionBehaviorConfig parseYamlForMission(final String yamlText, final String sourceName, final String missionId) {
        final MissionBehaviorConfig config = parseYaml(yamlText, sourceName);
        if (!missionId.equals(config.missionId())) {
            throw new IllegalStateException(
                    "Invalid mission behavior %s: missionId '%s' does not match requested mission '%s'"
                            .formatted(sourceName, config.missionId(), missionId)
            );
        }
        return config;
    }

    static MissionBehaviorConfig parseYaml(final String yamlText, final String sourceName) {
        final Yaml yaml = new Yaml();
        final Object loaded = yaml.load(yamlText);
        if (!(loaded instanceof Map<?, ?> root)) {
            throw new IllegalStateException("Invalid mission behavior %s: root must be a mapping".formatted(sourceName));
        }

        final int version = requireInt(root, "version", sourceName);
        final String missionId = requireString(root, "missionId", sourceName);

        if (!MissionCatalog.isCurrentMission(missionId)) {
            throw new IllegalStateException(
                    "Invalid mission behavior %s: unsupported missionId '%s'".formatted(sourceName, missionId)
            );
        }

        final List<String> allowedCommands = requireStringList(root, "allowedCommands", sourceName);
        final Set<String> uniqueAllowedCommands = new LinkedHashSet<>(allowedCommands);
        if (uniqueAllowedCommands.size() != allowedCommands.size()) {
            throw new IllegalStateException("Invalid mission behavior %s: allowedCommands must not contain duplicates"
                    .formatted(sourceName));
        }

        final Map<?, ?> executionMap = requireMap(root, "execution", sourceName);
        final MissionBehaviorConfig.MissionExecutionSettings execution = new MissionBehaviorConfig.MissionExecutionSettings(
                requireString(executionMap, "temporaryDirectoryPrefix", sourceName),
                requireString(executionMap, "resultFileName", sourceName),
                requireString(executionMap, "compilationFailureSummary", sourceName),
                requireString(executionMap, "executionStoppedSummary", sourceName)
        );

        final Map<?, ?> objectiveMap = requireMap(root, "objective", sourceName);
        final MissionBehaviorConfig.MissionObjectiveSettings objective = new MissionBehaviorConfig.MissionObjectiveSettings(
                requireString(objectiveMap, "kind", sourceName),
                requireString(objectiveMap, "successCondition", sourceName)
        );

        return new MissionBehaviorConfig(version, missionId, uniqueAllowedCommands.stream().toList(), execution, objective);
    }

    private static Map<?, ?> requireMap(final Map<?, ?> source, final String key, final String sourceName) {
        final Object value = source.get(key);
        if (!(value instanceof Map<?, ?> mapValue)) {
            throw new IllegalStateException("Invalid mission behavior %s: '%s' must be a mapping".formatted(sourceName, key));
        }
        return mapValue;
    }

    private static List<String> requireStringList(final Map<?, ?> source, final String key, final String sourceName) {
        final Object value = source.get(key);
        if (!(value instanceof List<?> listValue)) {
            throw new IllegalStateException("Invalid mission behavior %s: '%s' must be a list".formatted(sourceName, key));
        }
        if (listValue.isEmpty()) {
            throw new IllegalStateException("Invalid mission behavior %s: '%s' must not be empty".formatted(sourceName, key));
        }

        return listValue.stream()
                .map(item -> {
                    if (item instanceof String stringValue && !stringValue.isBlank()) {
                        return stringValue;
                    }
                    throw new IllegalStateException(
                            "Invalid mission behavior %s: '%s' list must contain non-blank strings".formatted(sourceName, key)
                    );
                })
                .toList();
    }

    private static String requireString(final Map<?, ?> source, final String key, final String sourceName) {
        final Object value = source.get(key);
        if (!(value instanceof String stringValue) || stringValue.isBlank()) {
            throw new IllegalStateException("Invalid mission behavior %s: '%s' must be a non-blank string"
                    .formatted(sourceName, key));
        }
        return stringValue;
    }

    private static int requireInt(final Map<?, ?> source, final String key, final String sourceName) {
        final Object value = source.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }

        throw new IllegalStateException("Invalid mission behavior %s: '%s' must be an integer".formatted(sourceName, key));
    }

    private static String loadText(final String resourcePath) {
        final Path workingDirectoryPath = Path.of(resourcePath);
        if (Files.exists(workingDirectoryPath)) {
            try {
                return Files.readString(workingDirectoryPath, StandardCharsets.UTF_8);
            } catch (IOException ioException) {
                throw new IllegalStateException("Unable to load mission behavior: " + workingDirectoryPath, ioException);
            }
        }

        try (InputStream inputStream = MissionBehaviorLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalStateException("Unable to load mission behavior: " + resourcePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            throw new IllegalStateException("Unable to load mission behavior: " + resourcePath, ioException);
        }
    }
}
