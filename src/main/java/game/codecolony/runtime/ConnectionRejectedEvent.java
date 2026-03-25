package game.codecolony.runtime;

public record ConnectionRejectedEvent(String reason) implements MissionEvent {

    @Override
    public String description() {
        return reason;
    }
}
