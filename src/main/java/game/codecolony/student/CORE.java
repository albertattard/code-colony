package game.codecolony.student;

import game.codecolony.runtime.ChargeCoreCommand;
import game.codecolony.runtime.ChargeCoreResult;
import game.codecolony.runtime.ConnectCoreResult;
import game.codecolony.runtime.ConnectNextCoreCommand;
import game.codecolony.runtime.MissionCommandResult;
import game.codecolony.runtime.MissionExecutionException;
import game.codecolony.runtime.MissionSimulator;
import game.codecolony.runtime.MoveCoreCommand;
import game.codecolony.runtime.MoveCoreResult;
import game.codecolony.runtime.RepairCoreCommand;
import game.codecolony.runtime.RepairCoreResult;

public final class Core {

    private static MissionSimulator simulator;

    private final int coreId;

    private Core(final int coreId) {
        this.coreId = coreId;
    }

    public static void attachSimulator(final MissionSimulator missionSimulator) {
        simulator = missionSimulator;
    }

    public static void detachSimulator() {
        simulator = null;
    }

    public static Core connect() {
        final MissionCommandResult result = simulator().execute(new ConnectNextCoreCommand());
        if (result instanceof ConnectCoreResult connectCoreResult) {
            return new Core(connectCoreResult.coreId());
        }

        throw new MissionExecutionException("Unable to connect to a CORE unit.");
    }

    public int coreId() {
        return coreId;
    }

    public void charge() {
        final MissionCommandResult result = simulator().execute(new ChargeCoreCommand(coreId));
        if (result instanceof ChargeCoreResult) {
            return;
        }

        throw new MissionExecutionException("Unable to charge the CORE unit.");
    }

    public void move() {
        final MissionCommandResult result = simulator().execute(new MoveCoreCommand(coreId));
        if (result instanceof MoveCoreResult) {
            return;
        }

        throw new MissionExecutionException("Unable to move the CORE unit.");
    }

    public void repair() {
        final MissionCommandResult result = simulator().execute(new RepairCoreCommand(coreId));
        if (result instanceof RepairCoreResult) {
            return;
        }

        throw new MissionExecutionException("Unable to repair the CORE unit.");
    }

    private static MissionSimulator simulator() {
        if (simulator == null) {
            throw new IllegalStateException("Mission simulator is not attached.");
        }

        return simulator;
    }
}
