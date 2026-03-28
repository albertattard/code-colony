package game.codecolony.mission.mission01;

import game.codecolony.mission.MissionExecutionConfig;
import game.codecolony.mission.MissionCoreStatus;
import game.codecolony.mission.MissionExecutionRunner;
import game.codecolony.mission.MissionMap;
import game.codecolony.mission.MissionMapAdapter;
import game.codecolony.mission.MissionMapLoader;
import game.codecolony.mission.MissionMapSpawn;
import game.codecolony.mission.MissionRunResult;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public final class WakeTheCoreMissionExecutionService {

    private static final MissionExecutionRunner RUNNER = new MissionExecutionRunner();
    private static final MissionMap MISSION_MAP = new MissionMapLoader().load("mission-01");
    private static final MissionMapSpawn CORE_SPAWN = MissionMapAdapter.requireCoreSpawn(MISSION_MAP, "core_01");
    private static final MissionExecutionConfig CONFIG = MissionExecutionConfig.builder()
            .temporaryDirectoryPrefix("wake-the-core-")
            .resultFileName("wake-the-core-result.properties")
            .workerClass(WakeTheCoreMissionWorker.class)
            .compilationFailureSummary("The code could not be compiled for Mission 01.")
            .executionStoppedSummary("Execution stopped before Mission 01 could be evaluated.")
            .missionInitialStatus(new MissionCoreStatus("CORE-01", "Offline", null, null, null, null, "", "",
                    "No telemetry available while offline."))
            .missionSupportClasses(List.of(
                    WakeTheCoreMissionSimulation.class,
                    WakeTheCoreMissionSimulator.class,
                    WakeTheCoreMissionValidator.class,
                    WakeTheCoreMissionWorker.class
            ))
            .workerArguments(List.of(
                    CORE_SPAWN.at(),
                    Integer.toString(CORE_SPAWN.battery().level()),
                    Integer.toString(CORE_SPAWN.battery().capacity()),
                    Integer.toString(CORE_SPAWN.health().level()),
                    Integer.toString(CORE_SPAWN.health().capacity())
            ))
            .build();

    public MissionRunResult execute(final String code) {
        return RUNNER.execute(code, CONFIG);
    }
}
