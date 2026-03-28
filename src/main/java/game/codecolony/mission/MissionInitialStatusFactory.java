package game.codecolony.mission;

public final class MissionInitialStatusFactory {

    private static final String CORE_UNIT_NAME = "CORE-01";

    private MissionInitialStatusFactory() {
    }

    public static MissionCoreStatus withoutTelemetry(final MissionMapSpawn coreSpawn,
                                                     final String note) {
        return new MissionCoreStatus(
                CORE_UNIT_NAME,
                toStatusState(coreSpawn.state()),
                null,
                null,
                null,
                null,
                "",
                "",
                note
        );
    }

    public static MissionCoreStatus withTelemetry(final MissionMapSpawn coreSpawn,
                                                  final String dock,
                                                  final String position,
                                                  final String note) {
        return new MissionCoreStatus(
                CORE_UNIT_NAME,
                toStatusState(coreSpawn.state()),
                coreSpawn.battery().level(),
                coreSpawn.battery().capacity(),
                coreSpawn.health().level(),
                coreSpawn.health().capacity(),
                dock,
                position,
                note
        );
    }

    private static String toStatusState(final String mapState) {
        return "online".equalsIgnoreCase(mapState) ? "Online" : "Offline";
    }
}
