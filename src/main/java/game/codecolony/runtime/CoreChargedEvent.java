package game.codecolony.runtime;

public record CoreChargedEvent(int coreId, int batteryLevel, int batteryCapacity) implements MissionEvent {

    @Override
    public String description() {
        return "Charged CORE-%02d to %d/%d.".formatted(coreId, batteryLevel, batteryCapacity);
    }
}
