package game.codecolony.runtime;

public record CoreMovedEvent(int coreId, String position) implements MissionEvent {

    @Override
    public String description() {
        return "Moved CORE-%02d to %s.".formatted(coreId, position);
    }
}
