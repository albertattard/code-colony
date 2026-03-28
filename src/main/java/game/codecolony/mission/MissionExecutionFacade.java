package game.codecolony.mission;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public final class MissionExecutionFacade {

    private final GenericMissionExecutionService executionService;
    private final MissionExecutionConfigFactory configFactory;

    public MissionExecutionFacade() {
        this(new GenericMissionExecutionService(), new MissionExecutionConfigFactory());
    }

    MissionExecutionFacade(final GenericMissionExecutionService executionService,
                           final MissionExecutionConfigFactory configFactory) {
        this.executionService = executionService;
        this.configFactory = configFactory;
    }

    public MissionRunResult execute(final String missionId, final String code) {
        final MissionExecutionConfigFactory.MissionExecutionContext context = configFactory.contextFor(missionId);
        final MissionExecutionConfig config = configForContext(context);
        return executionService.execute(code, config);
    }

    private MissionExecutionConfig configForContext(final MissionExecutionConfigFactory.MissionExecutionContext context) {
        final ObjectiveExecutionProfile profile = PROFILE_BY_OBJECTIVE_KIND.get(context.behavior().objective().kind());
        if (profile == null) {
            throw new IllegalStateException(
                    "Unsupported objective kind for mission execution: %s (%s)"
                            .formatted(context.behavior().objective().kind(), context.missionId())
            );
        }

        return configFactory.create(
                context,
                profile.workerClass(),
                profile.initialStatusBuilder().build(context),
                profile.supportClasses(),
                profile.workerArgumentsBuilder().build(context)
        );
    }

    private static final Map<String, ObjectiveExecutionProfile> PROFILE_BY_OBJECTIVE_KIND = Map.of(
            "connect_once", new ObjectiveExecutionProfile(
                    GenericMissionWorker.class,
                    genericSupportClasses(),
                    context -> MissionInitialStatusFactory.withoutTelemetry(
                            context.coreSpawn(),
                            "No telemetry available while offline."),
                    context -> List.of(
                            context.behavior().objective().kind(),
                            context.coreSpawn().at(),
                            Integer.toString(context.coreSpawn().battery().level()),
                            Integer.toString(context.coreSpawn().battery().capacity()),
                            Integer.toString(context.coreSpawn().health().level()),
                            Integer.toString(context.coreSpawn().health().capacity()))
            ),
            "charge_to_full", new ObjectiveExecutionProfile(
                    GenericMissionWorker.class,
                    genericSupportClasses(),
                    context -> MissionInitialStatusFactory.withTelemetry(
                            context.coreSpawn(),
                            "Connected",
                            context.coreSpawn().at(),
                            "CORE-01 remains online from Mission 01. Re-establish control for this run to operate the unit."),
                    context -> List.of(
                            context.behavior().objective().kind(),
                            context.coreSpawn().at(),
                            Integer.toString(context.coreSpawn().battery().level()),
                            Integer.toString(context.coreSpawn().battery().capacity()),
                            Integer.toString(context.coreSpawn().health().level()),
                            Integer.toString(context.coreSpawn().health().capacity()))
            ),
            "repair_to_full", new ObjectiveExecutionProfile(
                    GenericMissionWorker.class,
                    genericSupportClasses(),
                    context -> {
                        final String repairPosition = context.missionMap().requireFirstCoordinateByType("repair");
                        return MissionInitialStatusFactory.withTelemetry(
                                context.coreSpawn(),
                                "Connected",
                                context.coreSpawn().at(),
                                "CORE-01 is stable and charged. Move to %s and repair structural damage."
                                        .formatted(repairPosition)
                        );
                    },
                    context -> {
                        final String dockPosition = context.missionMap().requireFirstCoordinateByType("dock");
                        final String repairPosition = context.missionMap().requireFirstCoordinateByType("repair");
                        return List.of(
                                context.behavior().objective().kind(),
                                context.coreSpawn().at(),
                                dockPosition,
                                repairPosition,
                                Integer.toString(context.missionMap().size().cols()),
                                Integer.toString(context.coreSpawn().battery().level()),
                                Integer.toString(context.coreSpawn().battery().capacity()),
                                Integer.toString(context.coreSpawn().health().level()),
                                Integer.toString(context.coreSpawn().health().capacity())
                        );
                    }
            )
    );

    private static List<Class<?>> genericSupportClasses() {
        return List.of(
                GenericMissionWorker.class,
                GenericMissionSimulator.class,
                GenericMissionSimulation.class,
                GenericMissionValidator.class
        );
    }

    private record ObjectiveExecutionProfile(Class<?> workerClass,
                                             List<Class<?>> supportClasses,
                                             InitialStatusBuilder initialStatusBuilder,
                                             WorkerArgumentsBuilder workerArgumentsBuilder) {
    }

    @FunctionalInterface
    private interface InitialStatusBuilder {
        MissionCoreStatus build(MissionExecutionConfigFactory.MissionExecutionContext context);
    }

    @FunctionalInterface
    private interface WorkerArgumentsBuilder {
        List<String> build(MissionExecutionConfigFactory.MissionExecutionContext context);
    }
}
