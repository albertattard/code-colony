package game.codecolony.runtime;

public sealed interface MissionEvent permits CoreConnectedEvent, ConnectionRejectedEvent {

    String description();
}
