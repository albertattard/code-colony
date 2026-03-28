package game.codecolony.mission.mission03;

import game.codecolony.mission.MissionExecutionConfig;
import game.codecolony.mission.MissionExecutionRunner;
import game.codecolony.mission.MissionMap;
import game.codecolony.mission.MissionMapAdapter;
import game.codecolony.mission.MissionMapLoader;
import game.codecolony.mission.MissionMapSpawn;
import game.codecolony.mission.MissionCoreStatus;
import game.codecolony.mission.MissionRunResult;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public final class RepairTheCoreMissionExecutionService {

    private static final MissionExecutionRunner RUNNER = new MissionExecutionRunner();
    private static final MissionMap MISSION_MAP = new MissionMapLoader().load("mission-03");
    private static final MissionMapSpawn CORE_SPAWN = MissionMapAdapter.requireCoreSpawn(MISSION_MAP, "core_01");
    private static final String DOCK_POSITION = MissionMapAdapter.requireFirstCoordinateByType(MISSION_MAP, "dock");
    private static final String REPAIR_POSITION = MissionMapAdapter.requireFirstCoordinateByType(MISSION_MAP, "repair");
    private static final MissionExecutionConfig CONFIG = MissionExecutionConfig.builder()
            .temporaryDirectoryPrefix("repair-the-core-")
            .resultFileName("repair-the-core-result.properties")
            .workerClass(RepairTheCoreMissionWorker.class)
            .compilationFailureSummary("The code could not be compiled for Mission 03.")
            .executionStoppedSummary("Execution stopped before Mission 03 could be evaluated.")
            .missionInitialStatus(new MissionCoreStatus("CORE-01", "Online",
                    CORE_SPAWN.battery().level(),
                    CORE_SPAWN.battery().capacity(),
                    CORE_SPAWN.health().level(),
                    CORE_SPAWN.health().capacity(),
                    "Connected",
                    CORE_SPAWN.at(),
                    "CORE-01 is stable and charged. Move to %s and repair structural damage.".formatted(REPAIR_POSITION)))
            .missionSupportClasses(List.of(
                    RepairTheCoreMissionSimulation.class,
                    RepairTheCoreMissionSimulator.class,
                    RepairTheCoreMissionValidator.class,
                    RepairTheCoreMissionWorker.class
            ))
            .workerArguments(List.of(
                    CORE_SPAWN.at(),
                    DOCK_POSITION,
                    REPAIR_POSITION,
                    Integer.toString(MISSION_MAP.size().cols()),
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
