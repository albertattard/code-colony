package game.codecolony.mission;

import java.util.List;

public record MissionManifest(int version, List<MissionManifestEntry> missions) {

    public MissionManifest {
        missions = List.copyOf(missions);
    }

    public List<MissionManifestEntry> enabledMissions() {
        return missions.stream()
                .filter(MissionManifestEntry::enabled)
                .toList();
    }
}
