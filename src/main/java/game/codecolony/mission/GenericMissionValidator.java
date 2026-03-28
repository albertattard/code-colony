package game.codecolony.mission;

import game.codecolony.runtime.ConnectionRejectedEvent;
import game.codecolony.runtime.CoreConnectedEvent;
import game.codecolony.runtime.MissionEvent;

import java.util.List;

final class GenericMissionValidator {

    MissionRunResult validate(final String objectiveKind,
                              final GenericMissionValidationCopy validationCopy,
                              final GenericMissionSimulation simulation,
                              final String runtimeError,
                              final String stdout,
                              final String stderr) {
        return switch (objectiveKind) {
            case "connect_once" -> validateMission01(validationCopy, simulation, runtimeError, stdout, stderr);
            case "charge_to_full" -> validateMission02(validationCopy, simulation, runtimeError, stdout, stderr);
            case "repair_to_full" -> validateMission03(validationCopy, simulation, runtimeError, stdout, stderr);
            default -> throw new IllegalStateException("Unsupported objective kind for validation: " + objectiveKind);
        };
    }

    private MissionRunResult validateMission01(final GenericMissionValidationCopy validationCopy,
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
                            validationCopy.runtimeExpectation(),
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
                validationCopy.requireMessage("successHeadline"),
                validationCopy.requireMessage("successSummary"),
                simulationEvents,
                List.of(
                        validationCopy.requireMessage("successFeedback1"),
                        validationCopy.requireMessage("successFeedback2"),
                        validationCopy.requireMessage("successFeedback3")
                ),
                mission01StatusFor(true, simulation.connectAttempts(), simulation),
                stdout,
                stderr,
                true
        );
        }

        return new MissionRunResult(
                validationCopy.requireMessage("incompleteHeadline"),
                validationCopy.requireMessage("incompleteSummary"),
                simulationEvents,
                List.of(
                        validationCopy.requireMessage("incompleteFeedback1"),
                        validationCopy.requireMessage("incompleteFeedback2")
                ),
                mission01StatusFor(false, simulation.connectAttempts(), simulation),
                stdout,
                stderr,
                false
        );
    }

    private MissionRunResult validateMission02(final GenericMissionValidationCopy validationCopy,
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
                            validationCopy.runtimeExpectation(),
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
                validationCopy.requireMessage("successHeadline"),
                validationCopy.requireMessage("successSummary"),
                simulationEvents,
                List.of(
                        validationCopy.requireMessage("successFeedback1"),
                        validationCopy.requireMessage("successFeedback2"),
                        validationCopy.requireMessage("successFeedback3")
                ),
                mission02StatusFor(true, simulation.connectAttempts(), simulation),
                stdout,
                stderr,
                true
            );
        }

        if (!simulation.connected()) {
            return new MissionRunResult(
                validationCopy.requireMessage("incompleteNoConnectionHeadline"),
                validationCopy.requireMessage("incompleteNoConnectionSummary"),
                simulationEvents,
                List.of(
                        validationCopy.requireMessage("incompleteNoConnectionFeedback1"),
                        applyTokens(validationCopy.requireMessage("incompleteNoConnectionFeedback2Template"), simulation)
                ),
                mission02StatusFor(false, simulation.connectAttempts(), simulation),
                stdout,
                stderr,
                false
            );
        }

        final int remainingCharge = simulation.batteryCapacity() - simulation.batteryLevel();
        return new MissionRunResult(
                validationCopy.requireMessage("incompleteChargingHeadline"),
                validationCopy.requireMessage("incompleteChargingSummary"),
                simulationEvents,
                List.of(
                        validationCopy.requireMessage("incompleteChargingFeedback1"),
                        applyTokens(
                                validationCopy.requireMessage("incompleteChargingFeedback2Template"),
                                simulation,
                                "remainingCharge", Integer.toString(remainingCharge)
                        )
                ),
                mission02StatusFor(true, simulation.connectAttempts(), simulation),
                stdout,
                stderr,
                false
        );
    }

    private MissionRunResult validateMission03(final GenericMissionValidationCopy validationCopy,
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
                            validationCopy.runtimeExpectation(),
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
                validationCopy.requireMessage("successHeadline"),
                validationCopy.requireMessage("successSummary"),
                simulationEvents,
                List.of(
                        validationCopy.requireMessage("successFeedback1"),
                        validationCopy.requireMessage("successFeedback2"),
                        validationCopy.requireMessage("successFeedback3")
                ),
                mission03StatusFor(simulation),
                stdout,
                stderr,
                true
            );
        }

        if (!simulation.connected()) {
            return new MissionRunResult(
                validationCopy.requireMessage("incompleteNoConnectionHeadline"),
                validationCopy.requireMessage("incompleteNoConnectionSummary"),
                simulationEvents,
                List.of(
                        validationCopy.requireMessage("incompleteNoConnectionFeedback1"),
                        validationCopy.requireMessage("incompleteNoConnectionFeedback2")
                ),
                mission03StatusFor(simulation),
                stdout,
                stderr,
                false
            );
        }

        if (!"B3".equals(simulation.position())) {
            return new MissionRunResult(
                validationCopy.requireMessage("incompleteNotAtRepairHeadline"),
                validationCopy.requireMessage("incompleteNotAtRepairSummary"),
                simulationEvents,
                List.of(
                        validationCopy.requireMessage("incompleteNotAtRepairFeedback1"),
                        applyTokens(
                                validationCopy.requireMessage("incompleteNotAtRepairFeedback2Template"),
                                simulation,
                                "targetPosition", "B3"
                        )
                ),
                mission03StatusFor(simulation),
                stdout,
                stderr,
                false
            );
        }

        return new MissionRunResult(
                validationCopy.requireMessage("incompleteRepairHeadline"),
                validationCopy.requireMessage("incompleteRepairSummary"),
                simulationEvents,
                List.of(
                        validationCopy.requireMessage("incompleteRepairFeedback1"),
                        validationCopy.requireMessage("incompleteRepairFeedback2")
                ),
                mission03StatusFor(simulation),
                stdout,
                stderr,
                false
        );
    }

    private String applyTokens(final String template,
                               final GenericMissionSimulation simulation,
                               final String... extraPairs) {
        String resolved = template
                .replace("{batteryCapacity}", Integer.toString(simulation.batteryCapacity()))
                .replace("{healthCapacity}", Integer.toString(simulation.healthCapacity()))
                .replace("{position}", simulation.position());

        for (int i = 0; i + 1 < extraPairs.length; i += 2) {
            resolved = resolved.replace("{" + extraPairs[i] + "}", extraPairs[i + 1]);
        }

        return resolved;
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
