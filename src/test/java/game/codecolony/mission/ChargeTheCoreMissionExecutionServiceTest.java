package game.codecolony.mission;

import static org.assertj.core.api.Assertions.assertThat;

import game.codecolony.mission.mission02.ChargeTheCoreMissionExecutionService;
import org.junit.jupiter.api.Test;

class ChargeTheCoreMissionExecutionServiceTest {

    private final ChargeTheCoreMissionExecutionService missionExecutionService = new ChargeTheCoreMissionExecutionService();
    private final MissionMapLoader missionMapLoader = new MissionMapLoader();

    @Test
    void chargingToFullCompletesMissionTwo() {
        final MissionMapSpawn coreSpawn = missionMapLoader.load("mission-02").requireCoreSpawn("core_01");
        final MissionRunResult runResult = missionExecutionService.execute("""
                var core = Core.connect();
                core.charge();
                core.charge();
                core.charge();
                core.charge();
                core.charge();
                """);

        assertThat(runResult.success()).isTrue();
        assertThat(runResult.headline()).isEqualTo("CORE Charged");
        assertThat(runResult.coreStatus().state()).isEqualTo("Online");
        assertThat(runResult.coreStatus().position()).isEqualTo(coreSpawn.at());
        assertThat(runResult.coreStatus().batteryLevel()).isEqualTo(coreSpawn.battery().capacity());
        assertThat(runResult.coreStatus().batteryCapacity()).isEqualTo(coreSpawn.battery().capacity());
        assertThat(runResult.coreStatus().healthLevel()).isEqualTo(coreSpawn.health().level());
        assertThat(runResult.coreStatus().healthCapacity()).isEqualTo(coreSpawn.health().capacity());
        assertThat(runResult.simulationEvents()).contains("Connected to CORE-01.");
        assertThat(runResult.simulationEvents()).contains("Charged CORE-01 to 5/5.");
    }

    @Test
    void partialChargeLeavesMissionIncomplete() {
        final MissionRunResult runResult = missionExecutionService.execute("""
                var core = Core.connect();
                core.charge();
                """);

        assertThat(runResult.success()).isFalse();
        assertThat(runResult.headline()).isEqualTo("Mission Incomplete");
        assertThat(runResult.summary()).contains("battery is not full yet");
        assertThat(runResult.coreStatus().batteryLevel()).isEqualTo(1);
        assertThat(runResult.feedbackItems()).anyMatch(item -> item.contains("4 more time"));
    }

    @Test
    void missingConnectLeavesCoreVisibleButWithoutReference() {
        final MissionRunResult runResult = missionExecutionService.execute("""
                System.out.println("No reference");
                """);

        assertThat(runResult.success()).isFalse();
        assertThat(runResult.headline()).isEqualTo("Mission Incomplete");
        assertThat(runResult.summary()).contains("did not obtain a control reference");
        assertThat(runResult.coreStatus().state()).isEqualTo("Online");
        assertThat(runResult.coreStatus().batteryLevel()).isZero();
        assertThat(runResult.coreStatus().healthLevel()).isEqualTo(1);
        assertThat(runResult.feedbackItems()).anyMatch(item -> item.contains("store the returned Core"));
    }

    @Test
    void chargingAfterFullDoesNotFailMission() {
        final MissionRunResult runResult = missionExecutionService.execute("""
                var core = Core.connect();
                core.charge();
                core.charge();
                core.charge();
                core.charge();
                core.charge();
                core.charge();
                """);

        assertThat(runResult.success()).isTrue();
        assertThat(runResult.coreStatus().batteryLevel()).isEqualTo(5);
        assertThat(runResult.simulationEvents()).contains("CORE-01 battery is already full at 5/5.");
    }
}
