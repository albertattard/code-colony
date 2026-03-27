package game.codecolony.mission.mission02;

import game.codecolony.mission.MissionExecutionConfig;
import game.codecolony.mission.MissionCoreStatus;
import game.codecolony.mission.MissionExecutionRunner;
import game.codecolony.mission.MissionRunResult;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public final class ChargeTheCoreMissionExecutionService {

    private static final MissionExecutionRunner RUNNER = new MissionExecutionRunner();

    public MissionRunResult execute(final String code) {
        return RUNNER.execute(code, config());
    }

    private MissionExecutionConfig config() {
        return MissionExecutionConfig.builder()
                .temporaryDirectoryPrefix("charge-the-core-")
                .resultFileName("charge-the-core-result.properties")
                .workerClassName("game.codecolony.mission.mission02.ChargeTheCoreMissionWorker")
                .compilationFailureSummary("The code could not be compiled for Mission 02.")
                .executionStoppedSummary("Execution stopped before Mission 02 could be evaluated.")
                .missionInitialStatus(new MissionCoreStatus("CORE-01", "Online", 0, 5, 1, 5, "Connected", "B1",
                        "Control link remains stable from Mission 01. Battery depleted. Structural damage still detected."))
                .missionSupportClasses(List.of(
                        ChargeTheCoreMissionSimulation.class,
                        ChargeTheCoreMissionSimulator.class,
                        ChargeTheCoreMissionValidator.class,
                        ChargeTheCoreMissionWorker.class
                ))
                .build();
    }
}
