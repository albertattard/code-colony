package game.codecolony.mission;

import java.util.Set;

public final class MissionCatalog {

    private static final Set<String> CURRENT_MISSIONS = Set.of("mission-01", "mission-02", "mission-03");

    private MissionCatalog() {
    }

    public static boolean isCurrentMission(final String missionId) {
        return CURRENT_MISSIONS.contains(missionId);
    }
}
