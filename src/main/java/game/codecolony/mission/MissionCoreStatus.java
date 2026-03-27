package game.codecolony.mission;

public record MissionCoreStatus(String unitName,
                                String state,
                                MissionStatusMeter battery,
                                MissionStatusMeter health,
                                String dock,
                                String position,
                                String note) {

    public MissionCoreStatus(final String unitName,
                             final String state,
                             final Integer batteryLevel,
                             final Integer batteryCapacity,
                             final Integer healthLevel,
                             final Integer healthCapacity,
                             final String dock,
                             final String position,
                             final String note) {
        this(
                unitName,
                state,
                MissionStatusMeter.ofNullable(batteryLevel, batteryCapacity),
                MissionStatusMeter.ofNullable(healthLevel, healthCapacity),
                dock,
                position,
                note
        );
    }

    public Integer batteryLevel() {
        return battery == null ? null : battery.level();
    }

    public Integer batteryCapacity() {
        return battery == null ? null : battery.capacity();
    }

    public Integer healthLevel() {
        return health == null ? null : health.level();
    }

    public Integer healthCapacity() {
        return health == null ? null : health.capacity();
    }
}
