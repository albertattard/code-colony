package game.codecolony.mission.mission03;

import game.codecolony.mission.MissionExecutionConfig;
import game.codecolony.mission.MissionCoreStatus;
import game.codecolony.mission.MissionExecutionRunner;
import game.codecolony.mission.MissionRunResult;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public final class RepairTheCoreMissionExecutionService {

    private static final MissionExecutionRunner RUNNER = new MissionExecutionRunner();
    private static final MissionExecutionConfig CONFIG = MissionExecutionConfig.builder()
            .temporaryDirectoryPrefix("repair-the-core-")
            .resultFileName("repair-the-core-result.properties")
            .workerClassName("game.codecolony.mission.mission03.RepairTheCoreMissionWorker")
            .compilationFailureSummary("The code could not be compiled for Mission 03.")
            .executionStoppedSummary("Execution stopped before Mission 03 could be evaluated.")
            .missionInitialStatus(new MissionCoreStatus("CORE-01", "Online", 5, 5, 1, 5, "Connected", "B1",
                    "CORE-01 is stable and charged. Move to B3 and repair structural damage."))
            .missionSupportClasses(List.of(
                    RepairTheCoreMissionSimulation.class,
                    RepairTheCoreMissionSimulator.class,
                    RepairTheCoreMissionValidator.class,
                    RepairTheCoreMissionWorker.class
            ))
            .build();

    public MissionRunResult execute(final String code) {
        return RUNNER.execute(code, CONFIG);
    }
}
