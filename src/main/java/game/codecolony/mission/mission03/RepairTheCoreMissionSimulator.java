package game.codecolony.mission.mission03;

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

import java.util.ArrayList;
import java.util.List;

final class RepairTheCoreMissionSimulator implements MissionSimulator {

    private static final int CORE_ID = 1;
    private static final int BATTERY_CAPACITY = 5;
    private static final int HEALTH_CAPACITY = 5;

    private final List<MissionEvent> events = new ArrayList<>();
    private boolean connected;
    private int connectAttempts;
    private int moves;
    private String position = "B1";
    private int batteryLevel = BATTERY_CAPACITY;
    private int healthLevel = 1;
    private boolean repaired;

    @Override
    public MissionCommandResult execute(final MissionCommand command) {
        if (command instanceof ConnectNextCoreCommand) {
            return executeConnect();
        }
        if (command instanceof ChargeCoreCommand chargeCoreCommand) {
            return executeCharge(chargeCoreCommand);
        }
        if (command instanceof MoveCoreCommand moveCoreCommand) {
            return executeMove(moveCoreCommand);
        }
        if (command instanceof RepairCoreCommand repairCoreCommand) {
            return executeRepair(repairCoreCommand);
        }

        throw new IllegalArgumentException("Unsupported command: " + command.getClass().getName());
    }

    RepairTheCoreMissionSimulation finish() {
        return new RepairTheCoreMissionSimulation(
                connected,
                connectAttempts,
                position,
                moves,
                batteryLevel,
                BATTERY_CAPACITY,
                healthLevel,
                HEALTH_CAPACITY,
                repaired,
                List.copyOf(events)
        );
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

    private ChargeCoreResult executeCharge(final ChargeCoreCommand command) {
        requireConnectedCore(command.coreId());
        if (!"B1".equals(position)) {
            throw new MissionExecutionException("CORE-01 must be on docking station B1 before charge().");
        }
        if (batteryLevel >= BATTERY_CAPACITY) {
            events.add(new CoreChargeCappedEvent(CORE_ID, batteryLevel, BATTERY_CAPACITY));
            return new ChargeCoreResult(CORE_ID, batteryLevel, BATTERY_CAPACITY);
        }

        batteryLevel++;
        events.add(new CoreChargedEvent(CORE_ID, batteryLevel, BATTERY_CAPACITY));
        return new ChargeCoreResult(CORE_ID, batteryLevel, BATTERY_CAPACITY);
    }

    private MoveCoreResult executeMove(final MoveCoreCommand command) {
        requireConnectedCore(command.coreId());
        if (batteryLevel <= 0) {
            throw new MissionExecutionException("CORE-01 has no battery remaining.");
        }
        if ("B1".equals(position)) {
            position = "B2";
        } else if ("B2".equals(position)) {
            position = "B3";
        } else {
            throw new MissionExecutionException("CORE-01 cannot move further east in this room.");
        }
        batteryLevel--;
        moves++;
        events.add(new CoreMovedEvent(CORE_ID, position));
        return new MoveCoreResult(CORE_ID, position);
    }

    private RepairCoreResult executeRepair(final RepairCoreCommand command) {
        requireConnectedCore(command.coreId());
        if (!"B3".equals(position)) {
            throw new MissionExecutionException("CORE-01 must be on the repair station tile before repair().");
        }
        healthLevel = HEALTH_CAPACITY;
        repaired = true;
        events.add(new CoreRepairedEvent(CORE_ID, healthLevel, HEALTH_CAPACITY));
        return new RepairCoreResult(CORE_ID, healthLevel, HEALTH_CAPACITY);
    }

    private void requireConnectedCore(final int coreId) {
        if (!connected || coreId != CORE_ID) {
            throw new MissionExecutionException("CORE-01 must be connected before this action.");
        }
    }
}
