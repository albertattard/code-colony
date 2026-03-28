package game.codecolony.mission;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

class MissionExecutionConfigTest {

    @Test
    void buildFailsWhenTemporaryDirectoryPrefixIsMissing() {
        assertThatThrownBy(() -> baseBuilder()
                .temporaryDirectoryPrefix(" ")
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("temporaryDirectoryPrefix is required");
    }

    @Test
    void buildFailsWhenMissionInitialStatusIsMissing() {
        assertThatThrownBy(() -> baseBuilder()
                .missionInitialStatus(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("missionInitialStatus is required");
    }

    @Test
    void buildFailsWhenWorkerClassIsMissing() {
        assertThatThrownBy(() -> baseBuilder()
                .workerClass(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("workerClass is required");
    }

    @Test
    void buildFailsWhenMissionSupportClassesAreMissing() {
        assertThatThrownBy(() -> baseBuilder()
                .missionSupportClasses(List.of())
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("missionSupportClasses must not be empty");
    }

    private MissionExecutionConfig.Builder baseBuilder() {
        return MissionExecutionConfig.builder()
                .temporaryDirectoryPrefix("mission-")
                .resultFileName("result.properties")
                .workerClass(MissionExecutionConfig.class)
                .compilationFailureSummary("compile failed")
                .executionStoppedSummary("stopped")
                .missionInitialStatus(new MissionCoreStatus("CORE-01", "Offline", null, null, null, null, "", "", "n/a"))
                .missionSupportClasses(List.of(MissionExecutionConfig.class));
    }
}
