package game.codecolony.mission.mission03;

import game.codecolony.mission.MissionExecutionConfig;
import game.codecolony.mission.MissionExecutionRunner;
import game.codecolony.mission.MissionInitialStatusFactory;
import game.codecolony.mission.MissionBehaviorConfig;
import game.codecolony.mission.MissionBehaviorRegistry;
import game.codecolony.mission.MissionMap;
import game.codecolony.mission.MissionMapLoader;
import game.codecolony.mission.MissionMapSpawn;
import game.codecolony.mission.MissionRunResult;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public final class RepairTheCoreMissionExecutionService {

    private static final MissionExecutionRunner RUNNER = new MissionExecutionRunner();
    private static final MissionBehaviorConfig BEHAVIOR = new MissionBehaviorRegistry().get("mission-03");
    private static final MissionMap MISSION_MAP = new MissionMapLoader().load("mission-03");
    private static final MissionMapSpawn CORE_SPAWN = MISSION_MAP.requireCoreSpawn("core_01");
    private static final String DOCK_POSITION = MISSION_MAP.requireFirstCoordinateByType("dock");
    private static final String REPAIR_POSITION = MISSION_MAP.requireFirstCoordinateByType("repair");
    private static final MissionExecutionConfig CONFIG = MissionExecutionConfig.builder()
            .temporaryDirectoryPrefix(BEHAVIOR.execution().temporaryDirectoryPrefix())
            .resultFileName(BEHAVIOR.execution().resultFileName())
            .workerClass(RepairTheCoreMissionWorker.class)
            .compilationFailureSummary(BEHAVIOR.execution().compilationFailureSummary())
            .executionStoppedSummary(BEHAVIOR.execution().executionStoppedSummary())
            .missionInitialStatus(MissionInitialStatusFactory.withTelemetry(
                    CORE_SPAWN,
                    "Connected",
                    CORE_SPAWN.at(),
                    "CORE-01 is stable and charged. Move to %s and repair structural damage.".formatted(REPAIR_POSITION)))
            .missionSupportClasses(List.of(
                    RepairTheCoreMissionSimulation.class,
                    RepairTheCoreMissionSimulator.class,
                    RepairTheCoreMissionValidator.class,
                    RepairTheCoreMissionWorker.class))
            .workerArguments(List.of(
                    CORE_SPAWN.at(),
                    DOCK_POSITION,
                    REPAIR_POSITION,
                    Integer.toString(MISSION_MAP.size().cols()),
                    Integer.toString(CORE_SPAWN.battery().level()),
                    Integer.toString(CORE_SPAWN.battery().capacity()),
                    Integer.toString(CORE_SPAWN.health().level()),
                    Integer.toString(CORE_SPAWN.health().capacity())))
            .build();

    public MissionRunResult execute(final String code) {
        return RUNNER.execute(code, CONFIG);
    }
}
