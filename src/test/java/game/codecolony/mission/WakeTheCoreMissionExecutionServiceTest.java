package game.codecolony.mission;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WakeTheCoreMissionExecutionServiceTest {

    private final WakeTheCoreMissionExecutionService missionExecutionService = new WakeTheCoreMissionExecutionService();

    @Test
    void successfulConnectRunBringsCoreOnline() {
        final WakeTheCoreRunResult runResult = missionExecutionService.execute("CORE.connect();");

        assertThat(runResult.success()).isTrue();
        assertThat(runResult.headline()).isEqualTo("CORE Online");
        assertThat(runResult.coreStatus().state()).isEqualTo("Online");
        assertThat(runResult.coreStatus().batteryLevel()).isZero();
        assertThat(runResult.coreStatus().batteryCapacity()).isEqualTo(5);
        assertThat(runResult.coreStatus().healthLevel()).isEqualTo(1);
        assertThat(runResult.coreStatus().healthCapacity()).isEqualTo(5);
        assertThat(runResult.simulationEvents()).contains("Connected to CORE-01.");
    }

    @Test
    void missingConnectLeavesCoreOffline() {
        final WakeTheCoreRunResult runResult = missionExecutionService.execute("");

        assertThat(runResult.success()).isFalse();
        assertThat(runResult.headline()).isEqualTo("Mission Incomplete");
        assertThat(runResult.coreStatus().state()).isEqualTo("Offline");
        assertThat(runResult.coreStatus().batteryLevel()).isNull();
        assertThat(runResult.coreStatus().healthLevel()).isNull();
        assertThat(runResult.feedbackItems()).contains("Call CORE.connect(); to bring the CORE online.");
    }

    @Test
    void duplicateConnectShowsRuntimeFailure() {
        final WakeTheCoreRunResult runResult = missionExecutionService.execute("""
                CORE.connect();
                CORE.connect();
                """);

        assertThat(runResult.success()).isFalse();
        assertThat(runResult.headline()).isEqualTo("Run Failed");
        assertThat(runResult.summary()).contains("already connected");
        assertThat(runResult.coreStatus().state()).isEqualTo("Online");
        assertThat(runResult.simulationEvents()).contains("CORE-01 is already connected.");
    }

    @Test
    void compileFailureReturnsLearnerFacingFeedback() {
        final WakeTheCoreRunResult runResult = missionExecutionService.execute("core.connect();");

        assertThat(runResult.success()).isFalse();
        assertThat(runResult.headline()).isEqualTo("Compilation Failed");
        assertThat(runResult.feedbackItems()).anyMatch(item -> item.contains("Line 1"));
    }
}
