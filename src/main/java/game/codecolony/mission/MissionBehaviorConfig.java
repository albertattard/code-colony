package game.codecolony.mission;

import java.util.List;

public record MissionBehaviorConfig(int version,
                                    String missionId,
                                    List<String> allowedCommands,
                                    MissionExecutionSettings execution,
                                    MissionObjectiveSettings objective) {

    public MissionBehaviorConfig {
        allowedCommands = List.copyOf(allowedCommands);
    }

    public record MissionExecutionSettings(String temporaryDirectoryPrefix,
                                           String resultFileName,
                                           String compilationFailureSummary,
                                           String executionStoppedSummary) {
    }

    public record MissionObjectiveSettings(String kind, String successCondition) {
    }
}
