package game.codecolony.mission.mission01;

import game.codecolony.mission.*;

import game.codecolony.runtime.ConnectionRejectedEvent;
import game.codecolony.runtime.CoreConnectedEvent;
import game.codecolony.runtime.MissionEvent;

import java.util.List;

final class WakeTheCoreMissionValidator {

    MissionRunResult validate(final WakeTheCoreMissionSimulation simulation, final String runtimeError,
                              final String stdout, final String stderr) {
        final List<String> simulationEvents = simulation.events().stream()
                .map(MissionEvent::description)
                .toList();

        if (runtimeError != null && !runtimeError.isBlank()) {
            return new MissionRunResult(
                    "Run Failed",
                    runtimeError,
                    simulationEvents,
                    List.of(
                            "Mission 01 allows the CORE to be connected once.",
                            "Fix the runtime problem and run the code again."
                    ),
                    statusFor(simulation.connected(), simulation.connectAttempts()),
                    stdout,
                    stderr,
                    false
            );
        }

        final long successfulConnections = simulation.events().stream()
                .filter(CoreConnectedEvent.class::isInstance)
                .count();
        final boolean rejectedConnection = simulation.events().stream()
                .anyMatch(ConnectionRejectedEvent.class::isInstance);

        if (successfulConnections == 1 && !rejectedConnection) {
            return new MissionRunResult(
                    "CORE Online",
                    "Control link established. CORE-01 is online, but telemetry shows a depleted battery and structural damage.",
                    simulationEvents,
                    List.of(
                            "Mission objective completed.",
                            "Core.connect(); changed the visible state of the world.",
                            "The CORE still needs charging and repair before it can return to field work."
                    ),
                    statusFor(true, simulation.connectAttempts()),
                    stdout,
                    stderr,
                    true
            );
        }

        return new MissionRunResult(
                "Mission Incomplete",
                "CORE-01 is still offline.",
                simulationEvents,
                List.of(
                        "Call Core.connect(); to bring the CORE online.",
                        "Mission 01 only expects a single successful connection."
                ),
                statusFor(false, simulation.connectAttempts()),
                stdout,
                stderr,
                false
        );
    }

    private MissionCoreStatus statusFor(final boolean connected, final int connectAttempts) {
        if (connected) {
            final String note = connectAttempts > 1
                    ? "Connection established, then an invalid duplicate connect was attempted"
                    : "Telemetry online. Battery depleted. Structural damage detected.";
            return new MissionCoreStatus("CORE-01", "Online", 0, 5, 1, 5, "Connected", "B1", note);
        }

        return new MissionCoreStatus("CORE-01", "Offline", null, null, null, null, "", "", "No telemetry available while offline.");
    }
}
