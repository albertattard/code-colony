package game.codecolony.mission;

import java.util.List;
import java.util.Map;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

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
                            resolveExecutionTemplate(context, context.behavior().execution().initialStatusNoteTemplate())),
                    context -> List.of(
                            context.behavior().objective().kind(),
                            encodeValidationPayload(context.behavior().validation()),
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
                            resolveExecutionTemplate(context, context.behavior().execution().initialStatusNoteTemplate())),
                    context -> List.of(
                            context.behavior().objective().kind(),
                            encodeValidationPayload(context.behavior().validation()),
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
                        return MissionInitialStatusFactory.withTelemetry(
                                context.coreSpawn(),
                                "Connected",
                                context.coreSpawn().at(),
                                resolveExecutionTemplate(context, context.behavior().execution().initialStatusNoteTemplate())
                        );
                    },
                    context -> {
                        final String dockPosition = context.missionMap().requireFirstCoordinateByType("dock");
                        final String repairPosition = context.missionMap().requireFirstCoordinateByType("repair");
                        return List.of(
                                context.behavior().objective().kind(),
                                encodeValidationPayload(context.behavior().validation()),
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
                GenericMissionValidator.class,
                GenericMissionValidationCopy.class
        );
    }

    private static String resolveExecutionTemplate(final MissionExecutionConfigFactory.MissionExecutionContext context,
                                                   final String template) {
        final String dockPosition = context.missionMap().requireFirstCoordinateByType("dock");
        final String repairPosition = context.missionMap().requireFirstCoordinateByType("repair");
        return template
                .replace("{dockPosition}", dockPosition)
                .replace("{repairPosition}", repairPosition)
                .replace("{corePosition}", context.coreSpawn().at());
    }

    private static String encodeValidationPayload(final MissionBehaviorConfig.MissionValidationSettings validation) {
        final StringBuilder builder = new StringBuilder();
        builder.append("runtimeExpectation=").append(validation.runtimeExpectation()).append('\n');
        validation.messages().forEach((key, value) -> builder.append(key).append('=').append(value).append('\n'));
        return Base64.getEncoder().encodeToString(builder.toString().getBytes(StandardCharsets.UTF_8));
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
