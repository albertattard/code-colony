package game.codecolony.mission.mission02;

import game.codecolony.mission.GenericMissionExecutionService;
import game.codecolony.mission.MissionExecutionConfig;
import game.codecolony.mission.MissionExecutionConfigFactory;
import game.codecolony.mission.MissionInitialStatusFactory;
import game.codecolony.mission.MissionRunResult;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public final class ChargeTheCoreMissionExecutionService {

    private static final GenericMissionExecutionService EXECUTION_SERVICE = new GenericMissionExecutionService();
    private static final MissionExecutionConfigFactory CONFIG_FACTORY = new MissionExecutionConfigFactory();
    private static final MissionExecutionConfigFactory.MissionExecutionContext CONTEXT =
            CONFIG_FACTORY.contextFor("mission-02");
    private static final MissionExecutionConfig CONFIG = CONFIG_FACTORY.create(
            CONTEXT,
            ChargeTheCoreMissionWorker.class,
            MissionInitialStatusFactory.withTelemetry(
                    CONTEXT.coreSpawn(),
                    "Connected",
                    CONTEXT.coreSpawn().at(),
                    "CORE-01 remains online from Mission 01. Re-establish control for this run to operate the unit."),
            List.of(
                    ChargeTheCoreMissionSimulation.class,
                    ChargeTheCoreMissionSimulator.class,
                    ChargeTheCoreMissionValidator.class,
                    ChargeTheCoreMissionWorker.class),
            List.of(
                    CONTEXT.coreSpawn().at(),
                    Integer.toString(CONTEXT.coreSpawn().battery().level()),
                    Integer.toString(CONTEXT.coreSpawn().battery().capacity()),
                    Integer.toString(CONTEXT.coreSpawn().health().level()),
                    Integer.toString(CONTEXT.coreSpawn().health().capacity()))
    );

    public MissionRunResult execute(final String code) {
        return EXECUTION_SERVICE.execute(code, CONFIG);
    }
}
