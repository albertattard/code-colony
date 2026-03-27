package game.codecolony.mission.mission02;

import game.codecolony.runtime.ChargeCoreCommand;
import game.codecolony.runtime.ChargeCoreResult;
import game.codecolony.runtime.ConnectCoreResult;
import game.codecolony.runtime.ConnectNextCoreCommand;
import game.codecolony.runtime.ConnectionRejectedEvent;
import game.codecolony.runtime.CoreChargeCappedEvent;
import game.codecolony.runtime.CoreChargedEvent;
import game.codecolony.runtime.CoreConnectedEvent;
import game.codecolony.runtime.MissionCommand;
import game.codecolony.runtime.MissionCommandResult;
import game.codecolony.runtime.MissionEvent;
import game.codecolony.runtime.MissionExecutionException;
import game.codecolony.runtime.MissionSimulator;

import java.util.ArrayList;
import java.util.List;

final class ChargeTheCoreMissionSimulator implements MissionSimulator {

    private static final int CORE_ID = 1;

    private final List<MissionEvent> events = new ArrayList<>();
    private final String startPosition;
    private final int batteryCapacity;
    private final int healthLevel;
    private final int healthCapacity;
    private boolean connected;
    private int connectAttempts;
    private int batteryLevel;

    ChargeTheCoreMissionSimulator(final String startPosition,
                                  final int batteryLevel,
                                  final int batteryCapacity,
                                  final int healthLevel,
                                  final int healthCapacity) {
        this.startPosition = startPosition;
        this.batteryLevel = batteryLevel;
        this.batteryCapacity = batteryCapacity;
        this.healthLevel = healthLevel;
        this.healthCapacity = healthCapacity;
    }

    @Override
    public MissionCommandResult execute(final MissionCommand command) {
        if (command instanceof ConnectNextCoreCommand) {
            return executeConnect();
        }
        if (command instanceof ChargeCoreCommand chargeCoreCommand) {
            return executeCharge(chargeCoreCommand);
        }

        throw new IllegalArgumentException("Unsupported command: " + command.getClass().getName());
    }

    ChargeTheCoreMissionSimulation finish() {
        return new ChargeTheCoreMissionSimulation(
                connected,
                connectAttempts,
                startPosition,
                batteryLevel,
                batteryCapacity,
                healthLevel,
                healthCapacity,
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
        if (!connected || command.coreId() != CORE_ID) {
            throw new MissionExecutionException("CORE-01 must be connected before it can be charged.");
        }

        if (batteryLevel >= batteryCapacity) {
            events.add(new CoreChargeCappedEvent(CORE_ID, batteryCapacity, batteryCapacity));
            return new ChargeCoreResult(CORE_ID, batteryCapacity, batteryCapacity);
        }

        batteryLevel++;
        events.add(new CoreChargedEvent(CORE_ID, batteryLevel, batteryCapacity));
        return new ChargeCoreResult(CORE_ID, batteryLevel, batteryCapacity);
    }
}
