package game.codecolony.runtime;

public record RepairCoreResult(int coreId, int healthLevel, int healthCapacity) implements MissionCommandResult {
}
