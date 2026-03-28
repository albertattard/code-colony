package game.codecolony.mission;

import game.codecolony.runtime.ConnectionRejectedEvent;
import game.codecolony.runtime.CoreConnectedEvent;
import game.codecolony.runtime.MissionEvent;

import java.util.List;

final class GenericMissionValidator {

    MissionRunResult validate(final String objectiveKind,
                              final String runtimeExpectation,
                              final GenericMissionSimulation simulation,
                              final String runtimeError,
                              final String stdout,
                              final String stderr) {
        return switch (objectiveKind) {
            case "connect_once" -> validateMission01(runtimeExpectation, simulation, runtimeError, stdout, stderr);
            case "charge_to_full" -> validateMission02(runtimeExpectation, simulation, runtimeError, stdout, stderr);
            case "repair_to_full" -> validateMission03(runtimeExpectation, simulation, runtimeError, stdout, stderr);
            default -> throw new IllegalStateException("Unsupported objective kind for validation: " + objectiveKind);
        };
    }

    private MissionRunResult validateMission01(final String runtimeExpectation,
                                               final GenericMissionSimulation simulation,
                                               final String runtimeError,
                                               final String stdout,
                                               final String stderr) {
        final List<String> simulationEvents = simulation.events().stream()
                .map(MissionEvent::description)
                .toList();

        if (runtimeError != null && !runtimeError.isBlank()) {
            return new MissionRunResult(
                    "Run Failed",
                    runtimeError,
                    simulationEvents,
                    List.of(
                            runtimeExpectation,
                            "Fix the runtime problem and run the code again."
                    ),
                    mission01StatusFor(simulation.connected(), simulation.connectAttempts(), simulation),
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
                    mission01StatusFor(true, simulation.connectAttempts(), simulation),
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
                mission01StatusFor(false, simulation.connectAttempts(), simulation),
                stdout,
                stderr,
                false
        );
    }

    private MissionRunResult validateMission02(final String runtimeExpectation,
                                               final GenericMissionSimulation simulation,
                                               final String runtimeError,
                                               final String stdout,
                                               final String stderr) {
        final List<String> simulationEvents = simulation.events().stream()
                .map(MissionEvent::description)
                .toList();

        if (runtimeError != null && !runtimeError.isBlank()) {
            return new MissionRunResult(
                    "Run Failed",
                    runtimeError,
                    simulationEvents,
                    List.of(
                            runtimeExpectation,
                            "Fix the runtime problem and run the code again."
                    ),
                    mission02StatusFor(simulation.connected(), simulation.connectAttempts(), simulation),
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

        if (successfulConnections == 1 && !rejectedConnection
                && simulation.batteryLevel() == simulation.batteryCapacity()) {
            return new MissionRunResult(
                    "CORE Charged",
                    "CORE-01 battery restored to full capacity. The unit now has enough power for basic movement.",
                    simulationEvents,
                    List.of(
                            "Mission objective completed.",
                            "Core.connect() returned a Core instance you could reuse.",
                            "Each successful core.charge(); call restored one power segment."
                    ),
                    mission02StatusFor(true, simulation.connectAttempts(), simulation),
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
                            "Call Core.connect() and store the returned Core in a variable before charging it.",
                            "Mission 02 is solved when the battery reaches %d / %d."
                                    .formatted(simulation.batteryCapacity(), simulation.batteryCapacity())
                    ),
                    mission02StatusFor(false, simulation.connectAttempts(), simulation),
                    stdout,
                    stderr,
                    false
            );
        }

        final int remainingCharge = simulation.batteryCapacity() - simulation.batteryLevel();
        return new MissionRunResult(
                "Mission Incomplete",
                "CORE-01 is online, but the battery is not full yet.",
                simulationEvents,
                List.of(
                        "Store the connected CORE in a variable so you can call core.charge(); repeatedly.",
                        "Charge CORE-01 %d more time(s) to reach %d / %d."
                                .formatted(remainingCharge, simulation.batteryCapacity(), simulation.batteryCapacity())
                ),
                mission02StatusFor(true, simulation.connectAttempts(), simulation),
                stdout,
                stderr,
                false
        );
    }

    private MissionRunResult validateMission03(final String runtimeExpectation,
                                               final GenericMissionSimulation simulation,
                                               final String runtimeError,
                                               final String stdout,
                                               final String stderr) {
        final List<String> simulationEvents = simulation.events().stream()
                .map(MissionEvent::description)
                .toList();

        if (runtimeError != null && !runtimeError.isBlank()) {
            return new MissionRunResult(
                    "Run Failed",
                    runtimeError,
                    simulationEvents,
                    List.of(
                            runtimeExpectation,
                            "Fix the runtime problem and run the code again."
                    ),
                    mission03StatusFor(simulation),
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

        if (successfulConnections == 1 && !rejectedConnection && simulation.healthLevel() >= simulation.healthCapacity()) {
            return new MissionRunResult(
                    "CORE Repaired",
                    "CORE-01 reached the repair station and restored full structural health.",
                    simulationEvents,
                    List.of(
                            "Mission objective completed.",
                            "Movement sequence reached B3.",
                            "Repair station restored CORE-01 to 5 / 5 health."
                    ),
                    mission03StatusFor(simulation),
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
                            "Call Core.connect() and store the returned Core in a variable before movement and repair.",
                            "Mission 03 is solved when CORE-01 is repaired at station B3."
                    ),
                    mission03StatusFor(simulation),
                    stdout,
                    stderr,
                    false
            );
        }

        if (!"B3".equals(simulation.position())) {
            return new MissionRunResult(
                    "Mission Incomplete",
                    "CORE-01 has not reached the repair station yet.",
                    simulationEvents,
                    List.of(
                            "Use core.move(); to move from B1 to B3.",
                            "Current position: " + simulation.position() + ". Target position: B3."
                    ),
                    mission03StatusFor(simulation),
                    stdout,
                    stderr,
                    false
            );
        }

        return new MissionRunResult(
                "Mission Incomplete",
                "CORE-01 is at the repair station, but repair was not completed.",
                simulationEvents,
                List.of(
                        "Call core.repair(); while CORE-01 is on B3 until health reaches 5 / 5.",
                        "Repair is complete when health reaches 5 / 5."
                ),
                mission03StatusFor(simulation),
                stdout,
                stderr,
                false
        );
    }

    private MissionCoreStatus mission01StatusFor(final boolean connected,
                                                 final int connectAttempts,
                                                 final GenericMissionSimulation simulation) {
        if (connected) {
            final String note = connectAttempts > 1
                    ? "Connection established, then an invalid duplicate connect was attempted"
                    : "Telemetry online. Battery depleted. Structural damage detected.";
            return new MissionCoreStatus(
                    "CORE-01",
                    "Online",
                    simulation.batteryLevel(),
                    simulation.batteryCapacity(),
                    simulation.healthLevel(),
                    simulation.healthCapacity(),
                    "Connected",
                    simulation.startPosition(),
                    note
            );
        }

        return new MissionCoreStatus("CORE-01", "Offline", null, null, null, null, "", "", "No telemetry available while offline.");
    }

    private MissionCoreStatus mission02StatusFor(final boolean connected,
                                                 final int connectAttempts,
                                                 final GenericMissionSimulation simulation) {
        final String note = connectAttempts > 1
                ? "Control reference acquired, then an invalid duplicate connect was attempted."
                : connected && simulation.batteryLevel() == simulation.batteryCapacity()
                ? "Battery restored. CORE-01 is ready for low-power field work."
                : connected
                ? "Telemetry online. Battery charging in progress. Structural damage still detected."
                : "CORE-01 remains online from Mission 01. Use Core.connect() to obtain a control reference for this run.";
        return new MissionCoreStatus(
                "CORE-01",
                "Online",
                simulation.batteryLevel(),
                simulation.batteryCapacity(),
                simulation.healthLevel(),
                simulation.healthCapacity(),
                "Connected",
                simulation.startPosition(),
                note
        );
    }

    private MissionCoreStatus mission03StatusFor(final GenericMissionSimulation simulation) {
        final String dock = "B1".equals(simulation.position()) ? "Connected" : "";
        final String note = simulation.repaired()
                ? "Repair complete. CORE-01 structural integrity restored."
                : "B3 repair station can restore health when repair() is called.";

        return new MissionCoreStatus(
                "CORE-01",
                "Online",
                simulation.batteryLevel(),
                simulation.batteryCapacity(),
                simulation.healthLevel(),
                simulation.healthCapacity(),
                dock,
                simulation.position(),
                note
        );
    }
}
