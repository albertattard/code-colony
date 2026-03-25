package game.codecolony.runtime;

public sealed interface MissionEvent permits ConnectionRejectedEvent, CoreChargeCappedEvent, CoreChargedEvent, CoreConnectedEvent {

    String description();
}
