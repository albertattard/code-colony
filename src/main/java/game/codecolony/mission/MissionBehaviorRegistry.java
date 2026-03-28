package game.codecolony.mission;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MissionBehaviorRegistry {

    private final MissionBehaviorLoader loader = new MissionBehaviorLoader();
    private final Map<String, MissionBehaviorConfig> cache = new ConcurrentHashMap<>();

    public MissionBehaviorConfig get(final String missionId) {
        return cache.computeIfAbsent(missionId, loader::load);
    }
}
