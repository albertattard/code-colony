package game.codecolony.mission;

import game.codecolony.runtime.ConnectionRejectedEvent;
import game.codecolony.runtime.CoreConnectedEvent;
import game.codecolony.runtime.MissionEvent;

import java.util.List;

final class ChargeTheCoreMissionValidator {

    MissionRunResult validate(final ChargeTheCoreMissionSimulation simulation, final String runtimeError,
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
                            "Mission 02 expects one successful CORE.connect() call to obtain a control reference and enough charge actions to reach full power.",
                            "Fix the runtime problem and run the code again."
                    ),
                    statusFor(simulation.connected(), simulation.connectAttempts(), simulation.batteryLevel()),
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

        if (successfulConnections == 1 && !rejectedConnection && simulation.batteryLevel() == 5) {
            return new MissionRunResult(
                    "CORE Charged",
                    "CORE-01 battery restored to full capacity. The unit now has enough power for basic movement.",
                    simulationEvents,
                    List.of(
                            "Mission objective completed.",
                            "CORE.connect() returned a CORE instance you could reuse.",
                            "Each successful core.charge(); call restored one power segment."
                    ),
                    statusFor(true, simulation.connectAttempts(), simulation.batteryLevel()),
                    stdout,
                    stderr,
                    true
            );
        }

        if (!simulation.connected()) {
            return new MissionRunResult(
                    "Mission Incomplete",
                    "CORE-01 is online, but your program did not obtain a control reference.",
                    simulationEvents,
                    List.of(
                            "Call CORE.connect() and store the returned CORE in a variable before charging it.",
                            "Mission 02 is solved when the battery reaches 5 / 5."
                    ),
                    statusFor(false, simulation.connectAttempts(), simulation.batteryLevel()),
                    stdout,
                    stderr,
                    false
            );
        }

        final int remainingCharge = 5 - simulation.batteryLevel();
        return new MissionRunResult(
                "Mission Incomplete",
                "CORE-01 is online, but the battery is not full yet.",
                simulationEvents,
                List.of(
                        "Store the connected CORE in a variable so you can call core.charge(); repeatedly.",
                        "Charge CORE-01 %d more time(s) to reach 5 / 5.".formatted(remainingCharge)
                ),
                statusFor(true, simulation.connectAttempts(), simulation.batteryLevel()),
                stdout,
                stderr,
                false
        );
    }

    private MissionCoreStatus statusFor(final boolean connected, final int connectAttempts, final int batteryLevel) {
        final String note = connectAttempts > 1
                ? "Control reference acquired, then an invalid duplicate connect was attempted."
                : connected && batteryLevel == 5
                ? "Battery restored. CORE-01 is ready for low-power field work."
                : connected
                ? "Telemetry online. Battery charging in progress. Structural damage still detected."
                : "CORE-01 remains online from Mission 01. Use CORE.connect() to obtain a control reference for this run.";
        return new MissionCoreStatus("CORE-01", "Online", batteryLevel, 5, 1, 5, "Connected", "B1", note);
    }
}
