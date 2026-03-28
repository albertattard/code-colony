package game.codecolony.mission;

import java.util.List;
import java.util.Map;

public record MissionBehaviorConfig(int version,
                                    String missionId,
                                    List<String> allowedCommands,
                                    List<String> allowedRuntimeCommands,
                                    MissionExecutionSettings execution,
                                    MissionObjectiveSettings objective,
                                    MissionValidationSettings validation) {

    public MissionBehaviorConfig {
        allowedCommands = List.copyOf(allowedCommands);
        allowedRuntimeCommands = List.copyOf(allowedRuntimeCommands);
    }

    public record MissionExecutionSettings(String temporaryDirectoryPrefix,
                                           String resultFileName,
                                           String compilationFailureSummary,
                                           String executionStoppedSummary,
                                           String initialStatusNoteTemplate) {
    }

    public record MissionObjectiveSettings(String kind, String successCondition) {
    }

    public record MissionValidationSettings(String runtimeExpectation,
                                            String runtimeRetryHint,
                                            Map<String, String> messages) {
        public MissionValidationSettings {
            messages = Map.copyOf(messages);
        }
    }
}
