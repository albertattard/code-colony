package game.codecolony.mission;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public final class MissionExecutionConfigFactory {

    private final MissionBehaviorRegistry missionBehaviorRegistry = new MissionBehaviorRegistry();
    private final MissionMapLoader missionMapLoader = new MissionMapLoader();

    public MissionExecutionContext contextFor(final String missionId) {
        final MissionBehaviorConfig behavior = missionBehaviorRegistry.get(missionId);
        final MissionMap missionMap = missionMapLoader.load(missionId);
        final MissionMapSpawn coreSpawn = missionMap.requireCoreSpawn("core_01");
        return new MissionExecutionContext(missionId, behavior, missionMap, coreSpawn);
    }

    public MissionExecutionConfig create(final MissionExecutionContext context,
                                         final Class<?> workerClass,
                                         final MissionCoreStatus initialStatus,
                                         final List<Class<?>> missionSupportClasses,
                                         final List<String> workerArguments) {
        return MissionExecutionConfig.builder()
                .temporaryDirectoryPrefix(context.behavior().execution().temporaryDirectoryPrefix())
                .resultFileName(context.behavior().execution().resultFileName())
                .workerClass(workerClass)
                .compilationFailureSummary(context.behavior().execution().compilationFailureSummary())
                .executionStoppedSummary(context.behavior().execution().executionStoppedSummary())
                .missionInitialStatus(initialStatus)
                .missionSupportClasses(missionSupportClasses)
                .workerArguments(workerArguments)
                .build();
    }

    public record MissionExecutionContext(String missionId,
                                          MissionBehaviorConfig behavior,
                                          MissionMap missionMap,
                                          MissionMapSpawn coreSpawn) {
    }
}
