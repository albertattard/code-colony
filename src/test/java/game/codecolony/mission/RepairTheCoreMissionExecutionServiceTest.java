package game.codecolony.mission;

import static org.assertj.core.api.Assertions.assertThat;

import game.codecolony.mission.repaircore.RepairTheCoreMissionExecutionService;
import org.junit.jupiter.api.Test;

class RepairTheCoreMissionExecutionServiceTest {

    private final RepairTheCoreMissionExecutionService missionExecutionService =
            new RepairTheCoreMissionExecutionService();

    @Test
    void movingToRepairStationAndRepairingCompletesMissionThree() {
        final MissionRunResult runResult = missionExecutionService.execute("""
                var core = Core.connect();
                core.move();
                core.move();
                core.repair();
                """);

        assertThat(runResult.success()).isTrue();
        assertThat(runResult.headline()).isEqualTo("CORE Repaired");
        assertThat(runResult.coreStatus().state()).isEqualTo("Online");
        assertThat(runResult.coreStatus().healthLevel()).isEqualTo(5);
        assertThat(runResult.coreStatus().position()).isEqualTo("B3");
    }

    @Test
    void reachingRepairStationWithoutRepairIsIncomplete() {
        final MissionRunResult runResult = missionExecutionService.execute("""
                var core = Core.connect();
                core.move();
                core.move();
                """);

        assertThat(runResult.success()).isFalse();
        assertThat(runResult.headline()).isEqualTo("Mission Incomplete");
        assertThat(runResult.summary()).contains("repair was not completed");
        assertThat(runResult.coreStatus().position()).isEqualTo("B3");
    }

    @Test
    void missionTwoChargeSequenceStillAllowsMovementAndConsumesBattery() {
        final MissionRunResult runResult = missionExecutionService.execute("""
                var core = Core.connect();
                core.charge();
                core.charge();
                core.charge();
                core.charge();
                core.charge();
                core.move();
                """);

        assertThat(runResult.success()).isFalse();
        assertThat(runResult.headline()).isEqualTo("Mission Incomplete");
        assertThat(runResult.coreStatus().position()).isEqualTo("B2");
        assertThat(runResult.coreStatus().batteryLevel()).isEqualTo(4);
    }

    @Test
    void repairAwayFromStationFailsRun() {
        final MissionRunResult runResult = missionExecutionService.execute("""
                var core = Core.connect();
                core.repair();
                """);

        assertThat(runResult.success()).isFalse();
        assertThat(runResult.headline()).isEqualTo("Run Failed");
        assertThat(runResult.summary()).contains("repair station tile");
    }

    @Test
    void compileFailureReturnsLearnerFacingFeedback() {
        final MissionRunResult runResult = missionExecutionService.execute("for int i = 0;");

        assertThat(runResult.success()).isFalse();
        assertThat(runResult.headline()).isEqualTo("Compilation Failed");
        assertThat(runResult.feedbackItems()).anyMatch(item -> item.contains("Line"));
    }
}
