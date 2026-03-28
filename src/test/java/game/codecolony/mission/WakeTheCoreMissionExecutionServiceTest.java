package game.codecolony.mission;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WakeTheCoreMissionExecutionServiceTest {

    private final MissionExecutionFacade missionExecutionFacade = new MissionExecutionFacade();
    private final MissionMapLoader missionMapLoader = new MissionMapLoader();

    @Test
    void successfulConnectRunBringsCoreOnline() {
        final MissionMapSpawn coreSpawn = missionMapLoader.load("mission-01").requireCoreSpawn("core_01");
        final MissionRunResult runResult = missionExecutionFacade.execute("mission-01", "Core.connect();");

        assertThat(runResult.success()).isTrue();
        assertThat(runResult.headline()).isEqualTo("CORE Online");
        assertThat(runResult.coreStatus().unitName()).isEqualTo("CORE-01");
        assertThat(runResult.coreStatus().state()).isEqualTo("Online");
        assertThat(runResult.coreStatus().position()).isEqualTo(coreSpawn.at());
        assertThat(runResult.coreStatus().batteryLevel()).isEqualTo(coreSpawn.battery().level());
        assertThat(runResult.coreStatus().batteryCapacity()).isEqualTo(coreSpawn.battery().capacity());
        assertThat(runResult.coreStatus().healthLevel()).isEqualTo(coreSpawn.health().level());
        assertThat(runResult.coreStatus().healthCapacity()).isEqualTo(coreSpawn.health().capacity());
        assertThat(runResult.simulationEvents()).contains("Connected to CORE-01.");
        assertThat(runResult.stdout()).isEmpty();
        assertThat(runResult.stderr()).isEmpty();
    }

    @Test
    void successfulConnectCanAlsoShowLearnerStdout() {
        final MissionRunResult runResult = missionExecutionFacade.execute("mission-01", """
                Core.connect();
                System.out.println("Hello!!");
                """);

        assertThat(runResult.success()).isTrue();
        assertThat(runResult.stdout()).isEqualTo("Hello!!");
        assertThat(runResult.stderr()).isEmpty();
    }

    @Test
    void runtimeFailureCanAlsoShowLearnerStderr() {
        final MissionRunResult runResult = missionExecutionFacade.execute("mission-01", """
                System.err.println("before-failure");
                throw new RuntimeException("boom");
                """);

        assertThat(runResult.success()).isFalse();
        assertThat(runResult.headline()).isEqualTo("Run Failed");
        assertThat(runResult.stderr()).isEqualTo("before-failure");
    }

    @Test
    void missingConnectLeavesCoreOffline() {
        final MissionRunResult runResult = missionExecutionFacade.execute("mission-01", "");

        assertThat(runResult.success()).isFalse();
        assertThat(runResult.headline()).isEqualTo("Mission Incomplete");
        assertThat(runResult.coreStatus().unitName()).isEqualTo("CORE-01");
        assertThat(runResult.coreStatus().state()).isEqualTo("Offline");
        assertThat(runResult.coreStatus().batteryLevel()).isNull();
        assertThat(runResult.coreStatus().healthLevel()).isNull();
        assertThat(runResult.feedbackItems()).contains("Call Core.connect(); to bring the CORE online.");
    }

    @Test
    void duplicateConnectShowsRuntimeFailure() {
        final MissionRunResult runResult = missionExecutionFacade.execute("mission-01", """
                Core.connect();
                Core.connect();
                """);

        assertThat(runResult.success()).isFalse();
        assertThat(runResult.headline()).isEqualTo("Run Failed");
        assertThat(runResult.summary()).contains("already connected");
        assertThat(runResult.coreStatus().state()).isEqualTo("Online");
        assertThat(runResult.simulationEvents()).contains("CORE-01 is already connected.");
    }

    @Test
    void compileFailureReturnsLearnerFacingFeedback() {
        final MissionRunResult runResult = missionExecutionFacade.execute("mission-01", "core.connect();");

        assertThat(runResult.success()).isFalse();
        assertThat(runResult.headline()).isEqualTo("Compilation Failed");
        assertThat(runResult.feedbackItems()).anyMatch(item -> item.contains("Line 1"));
    }
}
