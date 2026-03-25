package game.codecolony.runtime;

public record CoreConnectedEvent(int coreId) implements MissionEvent {

    @Override
    public String description() {
        return "Connected to CORE-%02d.".formatted(coreId);
    }
}
