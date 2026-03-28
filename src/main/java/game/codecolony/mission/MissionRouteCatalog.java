package game.codecolony.mission;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public final class MissionRouteCatalog {

    private final MissionManifestLoader manifestLoader = new MissionManifestLoader();

    public MissionManifestEntry firstEnabledMission() {
        return enabledMissions().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No enabled missions available in content/missions/missions.yaml"));
    }

    public MissionManifestEntry requireEnabledMission(final String missionName) {
        return enabledMissions().stream()
                .filter(entry -> entry.name().equals(missionName))
                .findFirst()
                .orElseThrow(() -> new MissionNotFoundException(missionName));
    }

    public Optional<MissionManifestEntry> previousEnabledMission(final String missionName) {
        final List<MissionManifestEntry> missions = enabledMissions();
        for (int index = 0; index < missions.size(); index++) {
            final MissionManifestEntry entry = missions.get(index);
            if (entry.name().equals(missionName)) {
                return index == 0 ? Optional.empty() : Optional.of(missions.get(index - 1));
            }
        }
        throw new MissionNotFoundException(missionName);
    }

    public Optional<MissionManifestEntry> nextEnabledMission(final String missionName) {
        final List<MissionManifestEntry> missions = enabledMissions();
        for (int index = 0; index < missions.size(); index++) {
            final MissionManifestEntry entry = missions.get(index);
            if (entry.name().equals(missionName)) {
                return index == missions.size() - 1 ? Optional.empty() : Optional.of(missions.get(index + 1));
            }
        }
        throw new MissionNotFoundException(missionName);
    }

    private List<MissionManifestEntry> enabledMissions() {
        return manifestLoader.load().enabledMissions();
    }
}
