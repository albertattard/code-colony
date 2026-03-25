package game.codecolony.runtime;

public record CoreChargeCappedEvent(int coreId, int batteryLevel, int batteryCapacity) implements MissionEvent {

    @Override
    public String description() {
        return "CORE-%02d battery is already full at %d/%d.".formatted(coreId, batteryLevel, batteryCapacity);
    }
}
