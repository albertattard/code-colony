package game.codecolony.runtime;

public sealed interface MissionCommand permits ChargeCoreCommand, ConnectNextCoreCommand, MoveCoreCommand,
        RepairCoreCommand {
}
