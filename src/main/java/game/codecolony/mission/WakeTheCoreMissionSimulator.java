package game.codecolony.mission;

import game.codecolony.runtime.ConnectCoreResult;
import game.codecolony.runtime.ConnectNextCoreCommand;
import game.codecolony.runtime.ConnectionRejectedEvent;
import game.codecolony.runtime.CoreConnectedEvent;
import game.codecolony.runtime.MissionCommand;
import game.codecolony.runtime.MissionCommandResult;
import game.codecolony.runtime.MissionExecutionException;
import game.codecolony.runtime.MissionEvent;
import game.codecolony.runtime.MissionSimulator;

import java.util.ArrayList;
import java.util.List;

final class WakeTheCoreMissionSimulator implements MissionSimulator {

    private static final int CORE_ID = 1;

    private final List<MissionEvent> events = new ArrayList<>();
    private boolean connected;
    private int connectAttempts;

    @Override
    public MissionCommandResult execute(final MissionCommand command) {
        if (command instanceof ConnectNextCoreCommand) {
            return executeConnect();
        }

        throw new IllegalArgumentException("Unsupported command: " + command.getClass().getName());
    }

    WakeTheCoreMissionSimulation finish() {
        return new WakeTheCoreMissionSimulation(connected, connectAttempts, List.copyOf(events));
    }

    private ConnectCoreResult executeConnect() {
        connectAttempts++;
        if (connected) {
            final String message = "CORE-01 is already connected.";
            events.add(new ConnectionRejectedEvent(message));
            throw new MissionExecutionException(message);
        }

        connected = true;
        events.add(new CoreConnectedEvent(CORE_ID));
        return new ConnectCoreResult(CORE_ID);
    }
}
