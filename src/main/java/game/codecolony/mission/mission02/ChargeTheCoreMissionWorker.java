package game.codecolony.mission.mission02;

import game.codecolony.mission.MissionWorkerRunner;

public final class ChargeTheCoreMissionWorker {

    private static final String DEFAULT_START_POSITION = "B1";
    private static final int DEFAULT_BATTERY_LEVEL = 0;
    private static final int DEFAULT_BATTERY_CAPACITY = 5;
    private static final int DEFAULT_HEALTH_LEVEL = 1;
    private static final int DEFAULT_HEALTH_CAPACITY = 5;

    private ChargeTheCoreMissionWorker() {
    }

    public static void main(final String[] args) throws Exception {
        final String startPosition = argumentOrDefault(args, 1, DEFAULT_START_POSITION);
        final int batteryLevel = integerArgumentOrDefault(args, 2, DEFAULT_BATTERY_LEVEL);
        final int batteryCapacity = integerArgumentOrDefault(args, 3, DEFAULT_BATTERY_CAPACITY);
        final int healthLevel = integerArgumentOrDefault(args, 4, DEFAULT_HEALTH_LEVEL);
        final int healthCapacity = integerArgumentOrDefault(args, 5, DEFAULT_HEALTH_CAPACITY);

        final ChargeTheCoreMissionSimulator simulator = new ChargeTheCoreMissionSimulator(
                startPosition,
                batteryLevel,
                batteryCapacity,
                healthLevel,
                healthCapacity
        );
        final ChargeTheCoreMissionValidator validator = new ChargeTheCoreMissionValidator();

        MissionWorkerRunner.run(args, simulator, simulator::finish, validator::validate);
    }

    private static String argumentOrDefault(final String[] args, final int index, final String defaultValue) {
        return args.length > index ? args[index] : defaultValue;
    }

    private static int integerArgumentOrDefault(final String[] args, final int index, final int defaultValue) {
        return args.length > index ? Integer.parseInt(args[index]) : defaultValue;
    }
}
