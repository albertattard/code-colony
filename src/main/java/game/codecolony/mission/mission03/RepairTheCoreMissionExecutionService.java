package game.codecolony.mission.mission03;

import game.codecolony.mission.GenericMissionExecutionService;
import game.codecolony.mission.MissionExecutionConfig;
import game.codecolony.mission.MissionExecutionConfigFactory;
import game.codecolony.mission.MissionInitialStatusFactory;
import game.codecolony.mission.MissionRunResult;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public final class RepairTheCoreMissionExecutionService {

    private static final GenericMissionExecutionService EXECUTION_SERVICE = new GenericMissionExecutionService();
    private static final MissionExecutionConfigFactory CONFIG_FACTORY = new MissionExecutionConfigFactory();
    private static final MissionExecutionConfigFactory.MissionExecutionContext CONTEXT =
            CONFIG_FACTORY.contextFor("mission-03");
    private static final String DOCK_POSITION = CONTEXT.missionMap().requireFirstCoordinateByType("dock");
    private static final String REPAIR_POSITION = CONTEXT.missionMap().requireFirstCoordinateByType("repair");
    private static final MissionExecutionConfig CONFIG = CONFIG_FACTORY.create(
            CONTEXT,
            RepairTheCoreMissionWorker.class,
            MissionInitialStatusFactory.withTelemetry(
                    CONTEXT.coreSpawn(),
                    "Connected",
                    CONTEXT.coreSpawn().at(),
                    "CORE-01 is stable and charged. Move to %s and repair structural damage.".formatted(REPAIR_POSITION)),
            List.of(
                    RepairTheCoreMissionSimulation.class,
                    RepairTheCoreMissionSimulator.class,
                    RepairTheCoreMissionValidator.class,
                    RepairTheCoreMissionWorker.class),
            List.of(
                    CONTEXT.coreSpawn().at(),
                    DOCK_POSITION,
                    REPAIR_POSITION,
                    Integer.toString(CONTEXT.missionMap().size().cols()),
                    Integer.toString(CONTEXT.coreSpawn().battery().level()),
                    Integer.toString(CONTEXT.coreSpawn().battery().capacity()),
                    Integer.toString(CONTEXT.coreSpawn().health().level()),
                    Integer.toString(CONTEXT.coreSpawn().health().capacity()))
    );

    public MissionRunResult execute(final String code) {
        return EXECUTION_SERVICE.execute(code, CONFIG);
    }
}
