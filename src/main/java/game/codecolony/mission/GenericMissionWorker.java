package game.codecolony.mission;

public final class GenericMissionWorker {

    private static final String DEFAULT_OBJECTIVE_KIND = "connect_once";
    private static final String DEFAULT_VALIDATION_PAYLOAD = "";
    private static final String DEFAULT_ALLOWED_RUNTIME_COMMANDS_PAYLOAD = "";
    private static final String DEFAULT_START_POSITION = "B1";
    private static final String DEFAULT_DOCK_POSITION = "B1";
    private static final String DEFAULT_REPAIR_POSITION = "B3";
    private static final int DEFAULT_MAX_COLUMN = 3;
    private static final int DEFAULT_BATTERY_LEVEL = 0;
    private static final int DEFAULT_BATTERY_CAPACITY = 5;
    private static final int DEFAULT_HEALTH_LEVEL = 1;
    private static final int DEFAULT_HEALTH_CAPACITY = 5;

    private GenericMissionWorker() {
    }

    public static void main(final String[] args) throws Exception {
        final String objectiveKind = argumentOrDefault(args, 1, DEFAULT_OBJECTIVE_KIND);
        final String validationPayload = argumentOrDefault(args, 2, DEFAULT_VALIDATION_PAYLOAD);
        final String allowedRuntimeCommandsPayload = argumentOrDefault(args, 3, DEFAULT_ALLOWED_RUNTIME_COMMANDS_PAYLOAD);
        final GenericMissionValidationCopy validationCopy = decodeValidationPayload(validationPayload);
        final java.util.Set<String> allowedRuntimeCommands = decodeAllowedRuntimeCommands(allowedRuntimeCommandsPayload);
        final GenericMissionSimulator simulator = simulatorFor(objectiveKind, args, allowedRuntimeCommands);
        final GenericMissionValidator validator = new GenericMissionValidator();

        MissionWorkerRunner.run(args, simulator, simulator::finish,
                (simulation, runtimeError, stdout, stderr)
                        -> validator.validate(objectiveKind, validationCopy, simulation, runtimeError, stdout, stderr));
    }

    private static GenericMissionSimulator simulatorFor(final String objectiveKind,
                                                        final String[] args,
                                                        final java.util.Set<String> allowedRuntimeCommands) {
        return switch (objectiveKind) {
            case "connect_once", "charge_to_full" -> new GenericMissionSimulator(
                    objectiveKind,
                    allowedRuntimeCommands,
                    argumentOrDefault(args, 4, DEFAULT_START_POSITION),
                    DEFAULT_DOCK_POSITION,
                    DEFAULT_REPAIR_POSITION,
                    DEFAULT_MAX_COLUMN,
                    integerArgumentOrDefault(args, 5, DEFAULT_BATTERY_LEVEL),
                    integerArgumentOrDefault(args, 6, DEFAULT_BATTERY_CAPACITY),
                    integerArgumentOrDefault(args, 7, DEFAULT_HEALTH_LEVEL),
                    integerArgumentOrDefault(args, 8, DEFAULT_HEALTH_CAPACITY)
            );
            case "repair_to_full" -> new GenericMissionSimulator(
                    objectiveKind,
                    allowedRuntimeCommands,
                    argumentOrDefault(args, 4, DEFAULT_START_POSITION),
                    argumentOrDefault(args, 5, DEFAULT_DOCK_POSITION),
                    argumentOrDefault(args, 6, DEFAULT_REPAIR_POSITION),
                    integerArgumentOrDefault(args, 7, DEFAULT_MAX_COLUMN),
                    integerArgumentOrDefault(args, 8, DEFAULT_BATTERY_LEVEL),
                    integerArgumentOrDefault(args, 9, DEFAULT_BATTERY_CAPACITY),
                    integerArgumentOrDefault(args, 10, DEFAULT_HEALTH_LEVEL),
                    integerArgumentOrDefault(args, 11, DEFAULT_HEALTH_CAPACITY)
            );
            default -> throw new IllegalStateException("Unsupported objective kind for worker: " + objectiveKind);
        };
    }

    private static String argumentOrDefault(final String[] args, final int index, final String defaultValue) {
        return args.length > index ? args[index] : defaultValue;
    }

    private static int integerArgumentOrDefault(final String[] args, final int index, final int defaultValue) {
        return args.length > index ? Integer.parseInt(args[index]) : defaultValue;
    }

    private static GenericMissionValidationCopy decodeValidationPayload(final String payload) {
        if (payload == null || payload.isBlank()) {
            return new GenericMissionValidationCopy(
                    "Fix the runtime problem and run the code again.",
                    "Fix the runtime problem and run the code again.",
                    java.util.Map.of()
            );
        }

        final String decoded = new String(java.util.Base64.getDecoder().decode(payload), java.nio.charset.StandardCharsets.UTF_8);
        final java.util.Map<String, String> values = new java.util.HashMap<>();
        for (final String line : decoded.lines().toList()) {
            if (line.isBlank()) {
                continue;
            }
            final int separator = line.indexOf('=');
            if (separator <= 0) {
                continue;
            }
            final String key = line.substring(0, separator).trim();
            final String value = line.substring(separator + 1).trim();
            if (!key.isBlank() && !value.isBlank()) {
                values.put(key, value);
            }
        }

        final String runtimeExpectation = values.getOrDefault("runtimeExpectation", "Fix the runtime problem and run the code again.");
        final String runtimeRetryHint = values.getOrDefault("runtimeRetryHint", "Fix the runtime problem and run the code again.");
        values.remove("runtimeExpectation");
        values.remove("runtimeRetryHint");
        return new GenericMissionValidationCopy(runtimeExpectation, runtimeRetryHint, values);
    }

    private static java.util.Set<String> decodeAllowedRuntimeCommands(final String payload) {
        if (payload == null || payload.isBlank()) {
            return java.util.Set.of();
        }

        final java.util.Set<String> commands = new java.util.LinkedHashSet<>();
        for (final String token : payload.split(",")) {
            final String command = token.trim();
            if (!command.isBlank()) {
                commands.add(command);
            }
        }
        return java.util.Set.copyOf(commands);
    }
}
