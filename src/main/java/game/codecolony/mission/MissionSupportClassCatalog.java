package game.codecolony.mission;

import game.codecolony.runtime.ChargeCoreCommand;
import game.codecolony.runtime.ChargeCoreResult;
import game.codecolony.runtime.ConnectCoreResult;
import game.codecolony.runtime.ConnectNextCoreCommand;
import game.codecolony.runtime.ConnectionRejectedEvent;
import game.codecolony.runtime.CoreChargeCappedEvent;
import game.codecolony.runtime.CoreChargedEvent;
import game.codecolony.runtime.CoreConnectedEvent;
import game.codecolony.runtime.CoreMovedEvent;
import game.codecolony.runtime.CoreRepairedEvent;
import game.codecolony.runtime.MissionCommand;
import game.codecolony.runtime.MissionCommandResult;
import game.codecolony.runtime.MissionEvent;
import game.codecolony.runtime.MissionExecutionException;
import game.codecolony.runtime.MissionSimulator;
import game.codecolony.runtime.MoveCoreCommand;
import game.codecolony.runtime.MoveCoreResult;
import game.codecolony.runtime.RepairCoreCommand;
import game.codecolony.runtime.RepairCoreResult;
import game.codecolony.student.Core;

import java.util.List;

final class MissionSupportClassCatalog {

    private MissionSupportClassCatalog() {
    }

    static List<Class<?>> commonSupportClasses() {
        return List.of(
                Core.class,
                MissionCommand.class,
                MissionCommandResult.class,
                MissionEvent.class,
                MissionExecutionException.class,
                MissionSimulator.class,
                ChargeCoreCommand.class,
                ChargeCoreResult.class,
                ConnectCoreResult.class,
                ConnectNextCoreCommand.class,
                MoveCoreCommand.class,
                MoveCoreResult.class,
                RepairCoreCommand.class,
                RepairCoreResult.class,
                ConnectionRejectedEvent.class,
                CoreChargeCappedEvent.class,
                CoreChargedEvent.class,
                CoreConnectedEvent.class,
                CoreMovedEvent.class,
                CoreRepairedEvent.class,
                MissionCoreStatus.class,
                MissionResultValidator.class,
                MissionStatusMeter.class,
                MissionRunResult.class,
                MissionRunResultFileCodec.class,
                MissionWorkerRunner.class
        );
    }
}
