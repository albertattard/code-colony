package game.codecolony.mission.mission03;

import game.codecolony.mission.MissionWorkerRunner;

public final class RepairTheCoreMissionWorker {

    private static final String DEFAULT_START_POSITION = "B1";
    private static final String DEFAULT_DOCK_POSITION = "B1";
    private static final String DEFAULT_REPAIR_POSITION = "B3";
    private static final int DEFAULT_MAX_COLUMN = 3;
    private static final int DEFAULT_BATTERY_LEVEL = 5;
    private static final int DEFAULT_BATTERY_CAPACITY = 5;
    private static final int DEFAULT_HEALTH_LEVEL = 1;
    private static final int DEFAULT_HEALTH_CAPACITY = 5;

    private RepairTheCoreMissionWorker() {
    }

    public static void main(final String[] args) throws Exception {
        final String startPosition = argumentOrDefault(args, 1, DEFAULT_START_POSITION);
        final String dockPosition = argumentOrDefault(args, 2, DEFAULT_DOCK_POSITION);
        final String repairPosition = argumentOrDefault(args, 3, DEFAULT_REPAIR_POSITION);
        final int maxColumn = integerArgumentOrDefault(args, 4, DEFAULT_MAX_COLUMN);
        final int batteryLevel = integerArgumentOrDefault(args, 5, DEFAULT_BATTERY_LEVEL);
        final int batteryCapacity = integerArgumentOrDefault(args, 6, DEFAULT_BATTERY_CAPACITY);
        final int healthLevel = integerArgumentOrDefault(args, 7, DEFAULT_HEALTH_LEVEL);
        final int healthCapacity = integerArgumentOrDefault(args, 8, DEFAULT_HEALTH_CAPACITY);

        final RepairTheCoreMissionSimulator simulator = new RepairTheCoreMissionSimulator(
                startPosition,
                dockPosition,
                repairPosition,
                maxColumn,
                batteryLevel,
                batteryCapacity,
                healthLevel,
                healthCapacity
        );
        final RepairTheCoreMissionValidator validator = new RepairTheCoreMissionValidator();

        MissionWorkerRunner.run(args, simulator, simulator::finish, validator::validate);
    }

    private static String argumentOrDefault(final String[] args, final int index, final String defaultValue) {
        return args.length > index ? args[index] : defaultValue;
    }

    private static int integerArgumentOrDefault(final String[] args, final int index, final int defaultValue) {
        return args.length > index ? Integer.parseInt(args[index]) : defaultValue;
    }
}
