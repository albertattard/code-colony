package game.codecolony.mission.mission01;

import game.codecolony.mission.MissionExecutionConfig;
import game.codecolony.mission.MissionInitialStatusFactory;
import game.codecolony.mission.MissionExecutionRunner;
import game.codecolony.mission.MissionBehaviorConfig;
import game.codecolony.mission.MissionBehaviorRegistry;
import game.codecolony.mission.MissionMap;
import game.codecolony.mission.MissionMapLoader;
import game.codecolony.mission.MissionMapSpawn;
import game.codecolony.mission.MissionRunResult;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public final class WakeTheCoreMissionExecutionService {

    private static final MissionExecutionRunner RUNNER = new MissionExecutionRunner();
    private static final MissionBehaviorConfig BEHAVIOR = new MissionBehaviorRegistry().get("mission-01");
    private static final MissionMap MISSION_MAP = new MissionMapLoader().load("mission-01");
    private static final MissionMapSpawn CORE_SPAWN = MISSION_MAP.requireCoreSpawn("core_01");
    private static final MissionExecutionConfig CONFIG = MissionExecutionConfig.builder()
            .temporaryDirectoryPrefix(BEHAVIOR.execution().temporaryDirectoryPrefix())
            .resultFileName(BEHAVIOR.execution().resultFileName())
            .workerClass(WakeTheCoreMissionWorker.class)
            .compilationFailureSummary(BEHAVIOR.execution().compilationFailureSummary())
            .executionStoppedSummary(BEHAVIOR.execution().executionStoppedSummary())
            .missionInitialStatus(MissionInitialStatusFactory.withoutTelemetry(
                    CORE_SPAWN,
                    "No telemetry available while offline."))
            .missionSupportClasses(List.of(
                    WakeTheCoreMissionSimulation.class,
                    WakeTheCoreMissionSimulator.class,
                    WakeTheCoreMissionValidator.class,
                    WakeTheCoreMissionWorker.class))
            .workerArguments(List.of(
                    CORE_SPAWN.at(),
                    Integer.toString(CORE_SPAWN.battery().level()),
                    Integer.toString(CORE_SPAWN.battery().capacity()),
                    Integer.toString(CORE_SPAWN.health().level()),
                    Integer.toString(CORE_SPAWN.health().capacity())))
            .build();

    public MissionRunResult execute(final String code) {
        return RUNNER.execute(code, CONFIG);
    }
}
