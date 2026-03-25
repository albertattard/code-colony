package game.codecolony.runtime;

public record ChargeCoreResult(int coreId, int batteryLevel, int batteryCapacity) implements MissionCommandResult {
}
