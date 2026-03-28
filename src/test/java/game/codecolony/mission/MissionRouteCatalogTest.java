package game.codecolony.mission;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MissionRouteCatalogTest {

    private final MissionRouteCatalog routeCatalog = new MissionRouteCatalog();

    @Test
    void returnsFirstEnabledMissionFromManifest() {
        assertThat(routeCatalog.firstEnabledMission())
                .isEqualTo(new MissionManifestEntry("wake-the-core", "mission-01", true));
    }

    @Test
    void resolvesPreviousAndNextMissionByManifestOrder() {
        assertThat(routeCatalog.previousEnabledMission("wake-the-core")).isEmpty();
        assertThat(routeCatalog.nextEnabledMission("wake-the-core"))
                .contains(new MissionManifestEntry("charge-the-core", "mission-02", true));

        assertThat(routeCatalog.previousEnabledMission("charge-the-core"))
                .contains(new MissionManifestEntry("wake-the-core", "mission-01", true));
        assertThat(routeCatalog.nextEnabledMission("charge-the-core"))
                .contains(new MissionManifestEntry("repair-the-core", "mission-03", true));

        assertThat(routeCatalog.previousEnabledMission("repair-the-core"))
                .contains(new MissionManifestEntry("charge-the-core", "mission-02", true));
        assertThat(routeCatalog.nextEnabledMission("repair-the-core")).isEmpty();
    }
}
