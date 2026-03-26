package game.codecolony.runtime;

public record CoreRepairedEvent(int coreId, int healthLevel, int healthCapacity) implements MissionEvent {

    @Override
    public String description() {
        return "Repaired CORE-%02d to %d/%d health.".formatted(coreId, healthLevel, healthCapacity);
    }
}
