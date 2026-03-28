package game.codecolony.mission;

import game.codecolony.mission.mission01.WakeTheCoreMissionWorker;
import game.codecolony.mission.mission02.ChargeTheCoreMissionWorker;
import game.codecolony.mission.mission03.RepairTheCoreMissionWorker;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public final class MissionExecutionFacade {

    private final GenericMissionExecutionService executionService;
    private final Map<String, MissionExecutionConfig> configByMissionId;

    public MissionExecutionFacade() {
        this(new GenericMissionExecutionService(), new MissionExecutionConfigFactory());
    }

    MissionExecutionFacade(final GenericMissionExecutionService executionService,
                           final MissionExecutionConfigFactory configFactory) {
        this.executionService = executionService;
        this.configByMissionId = Map.of(
                "mission-01", mission01Config(configFactory),
                "mission-02", mission02Config(configFactory),
                "mission-03", mission03Config(configFactory)
        );
    }

    public MissionRunResult execute(final String missionId, final String code) {
        final MissionExecutionConfig config = configByMissionId.get(missionId);
        if (config == null) {
            throw new IllegalStateException("Unsupported mission id for execution: " + missionId);
        }
        return executionService.execute(code, config);
    }

    private static MissionExecutionConfig mission01Config(final MissionExecutionConfigFactory configFactory) {
        final MissionExecutionConfigFactory.MissionExecutionContext context = configFactory.contextFor("mission-01");
        return configFactory.create(
                context,
                WakeTheCoreMissionWorker.class,
                MissionInitialStatusFactory.withoutTelemetry(
                        context.coreSpawn(),
                        "No telemetry available while offline."),
                List.of(
                        classForName("game.codecolony.mission.mission01.WakeTheCoreMissionSimulation"),
                        classForName("game.codecolony.mission.mission01.WakeTheCoreMissionSimulator"),
                        classForName("game.codecolony.mission.mission01.WakeTheCoreMissionValidator"),
                        WakeTheCoreMissionWorker.class),
                List.of(
                        context.coreSpawn().at(),
                        Integer.toString(context.coreSpawn().battery().level()),
                        Integer.toString(context.coreSpawn().battery().capacity()),
                        Integer.toString(context.coreSpawn().health().level()),
                        Integer.toString(context.coreSpawn().health().capacity()))
        );
    }

    private static MissionExecutionConfig mission02Config(final MissionExecutionConfigFactory configFactory) {
        final MissionExecutionConfigFactory.MissionExecutionContext context = configFactory.contextFor("mission-02");
        return configFactory.create(
                context,
                ChargeTheCoreMissionWorker.class,
                MissionInitialStatusFactory.withTelemetry(
                        context.coreSpawn(),
                        "Connected",
                        context.coreSpawn().at(),
                        "CORE-01 remains online from Mission 01. Re-establish control for this run to operate the unit."),
                List.of(
                        classForName("game.codecolony.mission.mission02.ChargeTheCoreMissionSimulation"),
                        classForName("game.codecolony.mission.mission02.ChargeTheCoreMissionSimulator"),
                        classForName("game.codecolony.mission.mission02.ChargeTheCoreMissionValidator"),
                        ChargeTheCoreMissionWorker.class),
                List.of(
                        context.coreSpawn().at(),
                        Integer.toString(context.coreSpawn().battery().level()),
                        Integer.toString(context.coreSpawn().battery().capacity()),
                        Integer.toString(context.coreSpawn().health().level()),
                        Integer.toString(context.coreSpawn().health().capacity()))
        );
    }

    private static MissionExecutionConfig mission03Config(final MissionExecutionConfigFactory configFactory) {
        final MissionExecutionConfigFactory.MissionExecutionContext context = configFactory.contextFor("mission-03");
        final String dockPosition = context.missionMap().requireFirstCoordinateByType("dock");
        final String repairPosition = context.missionMap().requireFirstCoordinateByType("repair");

        return configFactory.create(
                context,
                RepairTheCoreMissionWorker.class,
                MissionInitialStatusFactory.withTelemetry(
                        context.coreSpawn(),
                        "Connected",
                        context.coreSpawn().at(),
                        "CORE-01 is stable and charged. Move to %s and repair structural damage.".formatted(repairPosition)),
                List.of(
                        classForName("game.codecolony.mission.mission03.RepairTheCoreMissionSimulation"),
                        classForName("game.codecolony.mission.mission03.RepairTheCoreMissionSimulator"),
                        classForName("game.codecolony.mission.mission03.RepairTheCoreMissionValidator"),
                        RepairTheCoreMissionWorker.class),
                List.of(
                        context.coreSpawn().at(),
                        dockPosition,
                        repairPosition,
                        Integer.toString(context.missionMap().size().cols()),
                        Integer.toString(context.coreSpawn().battery().level()),
                        Integer.toString(context.coreSpawn().battery().capacity()),
                        Integer.toString(context.coreSpawn().health().level()),
                        Integer.toString(context.coreSpawn().health().capacity()))
        );
    }

    private static Class<?> classForName(final String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Unable to load mission execution class: " + className, e);
        }
    }
}
