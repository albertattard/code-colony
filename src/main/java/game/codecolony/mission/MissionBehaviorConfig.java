package game.codecolony.mission;

import java.util.List;
import java.util.Map;

public record MissionBehaviorConfig(int version,
                                    String missionId,
                                    List<String> allowedCommands,
                                    List<String> allowedRuntimeCommands,
                                    MissionExecutionSettings execution,
                                    MissionObjectiveSettings objective,
                                    MissionValidationSettings validation,
                                    MissionRuntimeSettings runtime) {

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

    public record MissionRuntimeSettings(String worker,
                                         String simulator,
                                         MissionRuntimeInitialStatusSettings initialStatus,
                                         List<MissionRuntimeArgumentSettings> args) {
        public MissionRuntimeSettings {
            args = List.copyOf(args);
        }
    }

    public record MissionRuntimeInitialStatusSettings(String mode,
                                                      String state,
                                                      String position,
                                                      String noteTemplate) {
    }

    public record MissionRuntimeArgumentSettings(String name, String value) {
    }
}
