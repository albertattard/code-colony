package game.codecolony.mission;

import java.util.List;
import java.util.Map;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public final class MissionExecutionFacade {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^{}]+)}");

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

    MissionExecutionConfig configForContext(final MissionExecutionConfigFactory.MissionExecutionContext context) {
        if (context.behavior().runtime() != null) {
            return configForRuntime(context, context.behavior().runtime());
        }

        return configForObjectiveFallback(context);
    }

    private MissionExecutionConfig configForObjectiveFallback(final MissionExecutionConfigFactory.MissionExecutionContext context) {
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

    private MissionExecutionConfig configForRuntime(final MissionExecutionConfigFactory.MissionExecutionContext context,
                                                    final MissionBehaviorConfig.MissionRuntimeSettings runtime) {
        final Class<?> workerClass = workerClassForRuntime(runtime.worker());
        final List<Class<?>> supportClasses = supportClassesForRuntime(runtime.worker(), runtime.simulator());
        final MissionCoreStatus initialStatus = initialStatusForRuntime(context, runtime.initialStatus());
        final List<String> workerArguments = runtime.args().stream()
                .map(argument -> resolveRuntimeTemplate(context, argument.value()))
                .toList();

        return configFactory.create(context, workerClass, initialStatus, supportClasses, workerArguments);
    }

    private static Class<?> workerClassForRuntime(final String worker) {
        return switch (worker) {
            case "generic-mission-worker" -> GenericMissionWorker.class;
            default -> throw new IllegalStateException("Unsupported runtime worker: " + worker);
        };
    }

    private static List<Class<?>> supportClassesForRuntime(final String worker, final String simulator) {
        if (!"generic-mission-worker".equals(worker)) {
            throw new IllegalStateException("Unsupported runtime worker for support classes: " + worker);
        }
        if (!"generic-mission-simulator".equals(simulator)) {
            throw new IllegalStateException("Unsupported runtime simulator: " + simulator);
        }

        return genericSupportClasses();
    }

    private static MissionCoreStatus initialStatusForRuntime(final MissionExecutionConfigFactory.MissionExecutionContext context,
                                                             final MissionBehaviorConfig.MissionRuntimeInitialStatusSettings initialStatus) {
        final String note = resolveRuntimeTemplate(context, initialStatus.noteTemplate());
        return switch (initialStatus.mode()) {
            case "withoutTelemetry" -> MissionInitialStatusFactory.withoutTelemetry(context.coreSpawn(), note);
            case "withTelemetry" -> MissionInitialStatusFactory.withTelemetry(
                    context.coreSpawn(),
                    initialStatus.state() == null ? "Connected" : resolveRuntimeTemplate(context, initialStatus.state()),
                    initialStatus.position() == null ? context.coreSpawn().at() : resolveRuntimeTemplate(context, initialStatus.position()),
                    note
            );
            default -> throw new IllegalStateException("Unsupported runtime initial status mode: " + initialStatus.mode());
        };
    }

    private static String resolveRuntimeTemplate(final MissionExecutionConfigFactory.MissionExecutionContext context,
                                                 final String template) {
        final Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        final StringBuilder resolved = new StringBuilder();
        int last = 0;
        while (matcher.find()) {
            resolved.append(template, last, matcher.start());
            resolved.append(resolveRuntimeToken(context, matcher.group(1)));
            last = matcher.end();
        }
        resolved.append(template.substring(last));
        return resolved.toString();
    }

    private static String resolveRuntimeToken(final MissionExecutionConfigFactory.MissionExecutionContext context,
                                              final String token) {
        if (token.startsWith("map.firstByType:")) {
            return context.missionMap().requireFirstCoordinateByType(token.substring("map.firstByType:".length()));
        }

        return switch (token) {
            case "objective.kind" -> context.behavior().objective().kind();
            case "validationPayload.base64" -> encodeValidationPayload(context.behavior().validation());
            case "allowedRuntimeCommands.csv" -> encodeAllowedRuntimeCommands(context.behavior().allowedRuntimeCommands());
            case "coreSpawn.at", "corePosition" -> context.coreSpawn().at();
            case "coreSpawn.battery.level" -> Integer.toString(context.coreSpawn().battery().level());
            case "coreSpawn.battery.capacity" -> Integer.toString(context.coreSpawn().battery().capacity());
            case "coreSpawn.health.level" -> Integer.toString(context.coreSpawn().health().level());
            case "coreSpawn.health.capacity" -> Integer.toString(context.coreSpawn().health().capacity());
            case "map.size.cols" -> Integer.toString(context.missionMap().size().cols());
            case "dockPosition" -> context.missionMap().requireFirstCoordinateByType("dock");
            case "repairPosition" -> context.missionMap().requireFirstCoordinateByType("repair");
            default -> throw new IllegalStateException(
                    "Unknown runtime placeholder token '%s' for mission '%s'"
                            .formatted(token, context.missionId())
            );
        };
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
                            encodeAllowedRuntimeCommands(context.behavior().allowedRuntimeCommands()),
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
                            encodeAllowedRuntimeCommands(context.behavior().allowedRuntimeCommands()),
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
                                encodeAllowedRuntimeCommands(context.behavior().allowedRuntimeCommands()),
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
        builder.append("runtimeRetryHint=").append(validation.runtimeRetryHint()).append('\n');
        validation.messages().forEach((key, value) -> builder.append(key).append('=').append(value).append('\n'));
        return Base64.getEncoder().encodeToString(builder.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static String encodeAllowedRuntimeCommands(final List<String> allowedRuntimeCommands) {
        if (allowedRuntimeCommands.isEmpty()) {
            return "";
        }

        return String.join(",", allowedRuntimeCommands);
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
