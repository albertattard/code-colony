package game.codecolony.mission.mission02;

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
public final class ChargeTheCoreMissionExecutionService {

    private static final MissionExecutionRunner RUNNER = new MissionExecutionRunner();
    private static final MissionMap MISSION_MAP = new MissionMapLoader().load("mission-02");
    private static final MissionMapSpawn CORE_SPAWN = MissionMapAdapter.requireCoreSpawn(MISSION_MAP, "core_01");
    private static final MissionExecutionConfig CONFIG = MissionExecutionConfig.builder()
            .temporaryDirectoryPrefix("charge-the-core-")
            .resultFileName("charge-the-core-result.properties")
            .workerClassName("game.codecolony.mission.mission02.ChargeTheCoreMissionWorker")
            .compilationFailureSummary("The code could not be compiled for Mission 02.")
            .executionStoppedSummary("Execution stopped before Mission 02 could be evaluated.")
            .missionInitialStatus(new MissionCoreStatus("CORE-01", "Online",
                    CORE_SPAWN.battery().level(),
                    CORE_SPAWN.battery().capacity(),
                    CORE_SPAWN.health().level(),
                    CORE_SPAWN.health().capacity(),
                    "Connected",
                    CORE_SPAWN.at(),
                    "Control link remains stable from Mission 01. Battery depleted. Structural damage still detected."))
            .missionSupportClasses(List.of(
                    ChargeTheCoreMissionSimulation.class,
                    ChargeTheCoreMissionSimulator.class,
                    ChargeTheCoreMissionValidator.class,
                    ChargeTheCoreMissionWorker.class
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
