package game.codecolony.mission;

public final class GenericMissionWorker {

    private static final String DEFAULT_OBJECTIVE_KIND = "connect_once";
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
        final GenericMissionSimulator simulator = simulatorFor(objectiveKind, args);
        final GenericMissionValidator validator = new GenericMissionValidator();

        MissionWorkerRunner.run(args, simulator, simulator::finish,
                (simulation, runtimeError, stdout, stderr)
                        -> validator.validate(objectiveKind, simulation, runtimeError, stdout, stderr));
    }

    private static GenericMissionSimulator simulatorFor(final String objectiveKind, final String[] args) {
        return switch (objectiveKind) {
            case "connect_once", "charge_to_full" -> new GenericMissionSimulator(
                    objectiveKind,
                    argumentOrDefault(args, 2, DEFAULT_START_POSITION),
                    DEFAULT_DOCK_POSITION,
                    DEFAULT_REPAIR_POSITION,
                    DEFAULT_MAX_COLUMN,
                    integerArgumentOrDefault(args, 3, DEFAULT_BATTERY_LEVEL),
                    integerArgumentOrDefault(args, 4, DEFAULT_BATTERY_CAPACITY),
                    integerArgumentOrDefault(args, 5, DEFAULT_HEALTH_LEVEL),
                    integerArgumentOrDefault(args, 6, DEFAULT_HEALTH_CAPACITY)
            );
            case "repair_to_full" -> new GenericMissionSimulator(
                    objectiveKind,
                    argumentOrDefault(args, 2, DEFAULT_START_POSITION),
                    argumentOrDefault(args, 3, DEFAULT_DOCK_POSITION),
                    argumentOrDefault(args, 4, DEFAULT_REPAIR_POSITION),
                    integerArgumentOrDefault(args, 5, DEFAULT_MAX_COLUMN),
                    integerArgumentOrDefault(args, 6, DEFAULT_BATTERY_LEVEL),
                    integerArgumentOrDefault(args, 7, DEFAULT_BATTERY_CAPACITY),
                    integerArgumentOrDefault(args, 8, DEFAULT_HEALTH_LEVEL),
                    integerArgumentOrDefault(args, 9, DEFAULT_HEALTH_CAPACITY)
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
}
