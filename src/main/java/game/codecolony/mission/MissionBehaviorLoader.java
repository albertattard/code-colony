package game.codecolony.mission;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

public final class MissionBehaviorLoader {

    private static final String BEHAVIOR_RESOURCE_PATH_TEMPLATE = "content/missions/%s/mission.yaml";
    private static final Set<String> SUPPORTED_RUNTIME_WORKERS = Set.of("generic-mission-worker");
    private static final Set<String> SUPPORTED_RUNTIME_SIMULATORS = Set.of("generic-mission-simulator");
    private static final Set<String> SUPPORTED_INITIAL_STATUS_MODES = Set.of("withoutTelemetry", "withTelemetry");
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{[^{}]+}");

    public MissionBehaviorConfig load(final String missionId) {
        MissionCatalog.requireMissionFiles(missionId);
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
        final List<String> allowedRuntimeCommands = optionalStringList(root, "allowedRuntimeCommands", sourceName);
        final Set<String> uniqueAllowedRuntimeCommands = new LinkedHashSet<>(allowedRuntimeCommands);
        if (uniqueAllowedRuntimeCommands.size() != allowedRuntimeCommands.size()) {
            throw new IllegalStateException("Invalid mission behavior %s: allowedRuntimeCommands must not contain duplicates"
                    .formatted(sourceName));
        }

        final Map<?, ?> executionMap = requireMap(root, "execution", sourceName);
        final MissionBehaviorConfig.MissionExecutionSettings execution = new MissionBehaviorConfig.MissionExecutionSettings(
                requireString(executionMap, "temporaryDirectoryPrefix", sourceName),
                requireString(executionMap, "resultFileName", sourceName),
                requireString(executionMap, "compilationFailureSummary", sourceName),
                requireString(executionMap, "executionStoppedSummary", sourceName),
                requireString(executionMap, "initialStatusNoteTemplate", sourceName)
        );

        final Map<?, ?> objectiveMap = requireMap(root, "objective", sourceName);
        final MissionBehaviorConfig.MissionObjectiveSettings objective = new MissionBehaviorConfig.MissionObjectiveSettings(
                requireString(objectiveMap, "kind", sourceName),
                requireString(objectiveMap, "successCondition", sourceName)
        );

        final Map<?, ?> validationMap = requireMap(root, "validation", sourceName);
        final MissionBehaviorConfig.MissionValidationSettings validation = new MissionBehaviorConfig.MissionValidationSettings(
                requireString(validationMap, "runtimeExpectation", sourceName),
                requireString(validationMap, "runtimeRetryHint", sourceName),
                requireStringMap(validationMap, "messages", sourceName)
        );
        final MissionBehaviorConfig.MissionRuntimeSettings runtime = optionalRuntimeSettings(root, sourceName);

        return new MissionBehaviorConfig(
                version,
                missionId,
                uniqueAllowedCommands.stream().toList(),
                uniqueAllowedRuntimeCommands.stream().toList(),
                execution,
                objective,
                validation,
                runtime
        );
    }

    private static MissionBehaviorConfig.MissionRuntimeSettings optionalRuntimeSettings(final Map<?, ?> source,
                                                                                         final String sourceName) {
        if (!source.containsKey("runtime")) {
            return null;
        }

        final Map<?, ?> runtimeMap = requireMap(source, "runtime", sourceName);
        final String worker = requireString(runtimeMap, "worker", sourceName);
        if (!SUPPORTED_RUNTIME_WORKERS.contains(worker)) {
            throw new IllegalStateException(
                    "Invalid mission behavior %s: 'runtime.worker' must be one of %s"
                            .formatted(sourceName, SUPPORTED_RUNTIME_WORKERS)
            );
        }

        final String simulator = requireString(runtimeMap, "simulator", sourceName);
        if (!SUPPORTED_RUNTIME_SIMULATORS.contains(simulator)) {
            throw new IllegalStateException(
                    "Invalid mission behavior %s: 'runtime.simulator' must be one of %s"
                            .formatted(sourceName, SUPPORTED_RUNTIME_SIMULATORS)
            );
        }

        final Map<?, ?> initialStatusMap = requireMap(runtimeMap, "initialStatus", sourceName);
        final String initialStatusMode = requireString(initialStatusMap, "mode", sourceName);
        if (!SUPPORTED_INITIAL_STATUS_MODES.contains(initialStatusMode)) {
            throw new IllegalStateException(
                    "Invalid mission behavior %s: 'runtime.initialStatus.mode' must be one of %s"
                            .formatted(sourceName, SUPPORTED_INITIAL_STATUS_MODES)
            );
        }

        final String initialStatusState = optionalString(initialStatusMap, "state", sourceName);
        final String initialStatusPosition = optionalString(initialStatusMap, "position", sourceName);
        final String initialStatusNoteTemplate = requireString(initialStatusMap, "noteTemplate", sourceName);
        validatePlaceholders(initialStatusPosition, sourceName, "runtime.initialStatus.position");
        validatePlaceholders(initialStatusNoteTemplate, sourceName, "runtime.initialStatus.noteTemplate");

        final List<?> argsRaw = requireList(runtimeMap, "args", sourceName);
        if (argsRaw.isEmpty()) {
            throw new IllegalStateException("Invalid mission behavior %s: 'runtime.args' must not be empty".formatted(sourceName));
        }

        final List<MissionBehaviorConfig.MissionRuntimeArgumentSettings> args = argsRaw.stream()
                .map(argument -> parseRuntimeArgument(argument, sourceName))
                .toList();
        final Set<String> argNames = args.stream()
                .map(MissionBehaviorConfig.MissionRuntimeArgumentSettings::name)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (argNames.size() != args.size()) {
            throw new IllegalStateException("Invalid mission behavior %s: 'runtime.args' must not contain duplicate names"
                    .formatted(sourceName));
        }

        return new MissionBehaviorConfig.MissionRuntimeSettings(
                worker,
                simulator,
                new MissionBehaviorConfig.MissionRuntimeInitialStatusSettings(
                        initialStatusMode,
                        initialStatusState,
                        initialStatusPosition,
                        initialStatusNoteTemplate
                ),
                args
        );
    }

    private static MissionBehaviorConfig.MissionRuntimeArgumentSettings parseRuntimeArgument(final Object argumentRaw,
                                                                                              final String sourceName) {
        if (!(argumentRaw instanceof Map<?, ?> argumentMap)) {
            throw new IllegalStateException("Invalid mission behavior %s: 'runtime.args' entries must be mappings"
                    .formatted(sourceName));
        }

        final String name = requireString(argumentMap, "name", sourceName);
        final String value = requireString(argumentMap, "value", sourceName);
        validatePlaceholders(value, sourceName, "runtime.args[" + name + "].value");
        return new MissionBehaviorConfig.MissionRuntimeArgumentSettings(name, value);
    }

    private static void validatePlaceholders(final String text, final String sourceName, final String fieldPath) {
        if (text == null || text.isBlank()) {
            return;
        }

        if (text.contains("{") != text.contains("}")) {
            throw new IllegalStateException(
                    "Invalid mission behavior %s: '%s' contains malformed placeholder syntax".formatted(sourceName, fieldPath)
            );
        }

        int index = 0;
        while (index < text.length()) {
            final int open = text.indexOf('{', index);
            if (open < 0) {
                return;
            }
            final int close = text.indexOf('}', open + 1);
            if (close < 0) {
                throw new IllegalStateException(
                        "Invalid mission behavior %s: '%s' contains malformed placeholder syntax"
                                .formatted(sourceName, fieldPath)
                );
            }

            final String placeholder = text.substring(open, close + 1);
            if (!PLACEHOLDER_PATTERN.matcher(placeholder).matches()) {
                throw new IllegalStateException(
                        "Invalid mission behavior %s: '%s' contains malformed placeholder syntax"
                                .formatted(sourceName, fieldPath)
                );
            }
            index = close + 1;
        }
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

    private static List<String> optionalStringList(final Map<?, ?> source, final String key, final String sourceName) {
        if (!source.containsKey(key)) {
            return List.of();
        }
        return requireStringList(source, key, sourceName);
    }

    private static String optionalString(final Map<?, ?> source, final String key, final String sourceName) {
        if (!source.containsKey(key)) {
            return null;
        }

        return requireString(source, key, sourceName);
    }

    private static List<?> requireList(final Map<?, ?> source, final String key, final String sourceName) {
        final Object value = source.get(key);
        if (!(value instanceof List<?> listValue)) {
            throw new IllegalStateException("Invalid mission behavior %s: '%s' must be a list".formatted(sourceName, key));
        }
        return listValue;
    }

    private static String requireString(final Map<?, ?> source, final String key, final String sourceName) {
        final Object value = source.get(key);
        if (!(value instanceof String stringValue) || stringValue.isBlank()) {
            throw new IllegalStateException("Invalid mission behavior %s: '%s' must be a non-blank string"
                    .formatted(sourceName, key));
        }
        return stringValue;
    }

    private static Map<String, String> requireStringMap(final Map<?, ?> source, final String key, final String sourceName) {
        final Object value = source.get(key);
        if (!(value instanceof Map<?, ?> mapValue) || mapValue.isEmpty()) {
            throw new IllegalStateException("Invalid mission behavior %s: '%s' must be a non-empty mapping"
                    .formatted(sourceName, key));
        }

        final Map<String, String> result = mapValue.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> {
                            final Object rawKey = entry.getKey();
                            if (rawKey instanceof String stringKey && !stringKey.isBlank()) {
                                return stringKey;
                            }
                            throw new IllegalStateException(
                                    "Invalid mission behavior %s: '%s' keys must be non-blank strings"
                                            .formatted(sourceName, key)
                            );
                        },
                        entry -> {
                            final Object rawValue = entry.getValue();
                            if (rawValue instanceof String stringValue && !stringValue.isBlank()) {
                                return stringValue;
                            }
                            throw new IllegalStateException(
                                    "Invalid mission behavior %s: '%s' values must be non-blank strings"
                                            .formatted(sourceName, key)
                            );
                        }
                ));

        if (result.size() != mapValue.size()) {
            throw new IllegalStateException("Invalid mission behavior %s: '%s' must not contain duplicate keys"
                    .formatted(sourceName, key));
        }

        return result;
    }

    private static int requireInt(final Map<?, ?> source, final String key, final String sourceName) {
        final Object value = source.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }

        throw new IllegalStateException("Invalid mission behavior %s: '%s' must be an integer".formatted(sourceName, key));
    }

    private static String loadText(final String resourcePath) {
        final Path path = Path.of(resourcePath);
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            throw new IllegalStateException("Unable to load mission behavior: " + path, ioException);
        }
    }
}
