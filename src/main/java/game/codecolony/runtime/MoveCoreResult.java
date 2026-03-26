package game.codecolony.runtime;

public record MoveCoreResult(int coreId, String position) implements MissionCommandResult {
}
