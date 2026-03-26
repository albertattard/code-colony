package game.codecolony.mission.repaircore;

import game.codecolony.mission.MissionCoreStatus;
import game.codecolony.mission.MissionRunResult;
import game.codecolony.runtime.ConnectionRejectedEvent;
import game.codecolony.runtime.CoreConnectedEvent;
import game.codecolony.runtime.MissionEvent;

import java.util.List;

final class RepairTheCoreMissionValidator {

    MissionRunResult validate(final RepairTheCoreMissionSimulation simulation,
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
                            "Mission 03 expects one successful Core.connect() call, movement to B3, then core.repair().",
                            "Fix the runtime problem and run the code again."
                    ),
                    statusFor(simulation),
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

        if (successfulConnections == 1 && !rejectedConnection && simulation.repaired()) {
            return new MissionRunResult(
                    "CORE Repaired",
                    "CORE-01 reached the repair station and restored full structural health.",
                    simulationEvents,
                    List.of(
                            "Mission objective completed.",
                            "Movement sequence reached B3.",
                            "Repair station restored CORE-01 to 5 / 5 health."
                    ),
                    statusFor(simulation),
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
                    statusFor(simulation),
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
                    statusFor(simulation),
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
                        "Call core.repair(); while CORE-01 is on B3.",
                        "Repair is complete when health reaches 5 / 5."
                ),
                statusFor(simulation),
                stdout,
                stderr,
                false
        );
    }

    private MissionCoreStatus statusFor(final RepairTheCoreMissionSimulation simulation) {
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
