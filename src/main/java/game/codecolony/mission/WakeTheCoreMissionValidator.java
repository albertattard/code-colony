package game.codecolony.mission;

import game.codecolony.runtime.ConnectionRejectedEvent;
import game.codecolony.runtime.CoreConnectedEvent;
import game.codecolony.runtime.MissionEvent;

import java.util.List;

final class WakeTheCoreMissionValidator {

    WakeTheCoreRunResult validate(final WakeTheCoreMissionSimulation simulation, final String runtimeError) {
        final List<String> simulationEvents = simulation.events().stream()
                .map(MissionEvent::description)
                .toList();

        if (runtimeError != null && !runtimeError.isBlank()) {
            return new WakeTheCoreRunResult(
                    "Run Failed",
                    runtimeError,
                    simulationEvents,
                    List.of(
                            "Mission 01 allows the CORE to be connected once.",
                            "Fix the runtime problem and run the code again."
                    ),
                    statusFor(simulation.connected(), simulation.connectAttempts()),
                    false
            );
        }

        final long successfulConnections = simulation.events().stream()
                .filter(CoreConnectedEvent.class::isInstance)
                .count();
        final boolean rejectedConnection = simulation.events().stream()
                .anyMatch(ConnectionRejectedEvent.class::isInstance);

        if (successfulConnections == 1 && !rejectedConnection) {
            return new WakeTheCoreRunResult(
                    "CORE Online",
                    "Control link established. CORE-01 is online.",
                    simulationEvents,
                    List.of(
                            "Mission objective completed.",
                            "CORE.connect(); changed the visible state of the world."
                    ),
                    statusFor(true, simulation.connectAttempts()),
                    true
            );
        }

        return new WakeTheCoreRunResult(
                "Mission Incomplete",
                "CORE-01 is still offline.",
                simulationEvents,
                List.of(
                        "Call CORE.connect(); to bring the CORE online.",
                        "Mission 01 only expects a single successful connection."
                ),
                statusFor(false, simulation.connectAttempts()),
                false
        );
    }

    private WakeTheCoreCoreStatus statusFor(final boolean connected, final int connectAttempts) {
        if (connected) {
            final String note = connectAttempts > 1
                    ? "Connection established, then an invalid duplicate connect was attempted"
                    : "Control link established";
            return new WakeTheCoreCoreStatus("Online", "Full", "Ready", "B1", note);
        }

        return new WakeTheCoreCoreStatus("Offline", "", "", "", "No telemetry available while offline.");
    }
}
