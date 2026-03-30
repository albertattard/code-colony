package game.codecolony.mission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class MissionMapLoaderTest {

    private final MissionMapLoader missionMapLoader = new MissionMapLoader();

    @Test
    void allCurrentMissionMapsLoad() {
        assertThat(missionMapLoader.load("mission-01")).isNotNull();
        assertThat(missionMapLoader.load("mission-02")).isNotNull();
        assertThat(missionMapLoader.load("mission-03")).isNotNull();
        assertThat(missionMapLoader.load("mission-04")).isNotNull();
    }

    @Test
    void loadMission01MapFromResources() {
        final MissionMap map = missionMapLoader.load("mission-01");

        assertThat(map.version()).isEqualTo(1);
        assertThat(map.name()).isEqualTo("maintenance-room-b-1049");
        assertThat(map.size().rows()).isEqualTo(3);
        assertThat(map.size().cols()).isEqualTo(3);
        assertThat(map.base()).containsExactly("...", "D.R", "...");
        assertThat(map.legend().get("D").type()).isEqualTo("dock");
        assertThat(map.legend().get("R").type()).isEqualTo("repair");

        assertThat(map.spawns()).singleElement().satisfies(spawn -> {
            assertThat(spawn.id()).isEqualTo("core_01");
            assertThat(spawn.type()).isEqualTo("core");
            assertThat(spawn.state()).isEqualTo("offline");
            assertThat(spawn.at()).isEqualTo("B1");
            assertThat(spawn.battery().level()).isEqualTo(0);
            assertThat(spawn.battery().capacity()).isEqualTo(5);
            assertThat(spawn.health().level()).isEqualTo(1);
            assertThat(spawn.health().capacity()).isEqualTo(5);
        });
    }

    @Test
    void loadMission02MapFromResources() {
        final MissionMap map = missionMapLoader.load("mission-02");

        assertThat(map.version()).isEqualTo(1);
        assertThat(map.name()).isEqualTo("maintenance-room-b-1049");
        assertThat(map.size().rows()).isEqualTo(3);
        assertThat(map.size().cols()).isEqualTo(3);
        assertThat(map.base()).containsExactly("...", "D.R", "...");
        assertThat(map.legend().get("D").type()).isEqualTo("dock");
        assertThat(map.legend().get("R").type()).isEqualTo("repair");

        assertThat(map.spawns()).singleElement().satisfies(spawn -> {
            assertThat(spawn.id()).isEqualTo("core_01");
            assertThat(spawn.type()).isEqualTo("core");
            assertThat(spawn.state()).isEqualTo("online");
            assertThat(spawn.at()).isEqualTo("B1");
            assertThat(spawn.battery().level()).isEqualTo(0);
            assertThat(spawn.battery().capacity()).isEqualTo(5);
            assertThat(spawn.health().level()).isEqualTo(1);
            assertThat(spawn.health().capacity()).isEqualTo(5);
        });
    }

    @Test
    void loadMission03MapFromResources() {
        final MissionMap map = missionMapLoader.load("mission-03");

        assertThat(map.version()).isEqualTo(1);
        assertThat(map.name()).isEqualTo("maintenance-room-b-1049");
        assertThat(map.size().rows()).isEqualTo(3);
        assertThat(map.size().cols()).isEqualTo(3);
        assertThat(map.base()).containsExactly("...", "D.R", "...");
        assertThat(map.legend().get("D").type()).isEqualTo("dock");
        assertThat(map.legend().get("R").type()).isEqualTo("repair");

        assertThat(map.spawns()).singleElement().satisfies(spawn -> {
            assertThat(spawn.id()).isEqualTo("core_01");
            assertThat(spawn.type()).isEqualTo("core");
            assertThat(spawn.state()).isEqualTo("online");
            assertThat(spawn.at()).isEqualTo("B1");
            assertThat(spawn.battery().level()).isEqualTo(5);
            assertThat(spawn.battery().capacity()).isEqualTo(5);
            assertThat(spawn.health().level()).isEqualTo(1);
            assertThat(spawn.health().capacity()).isEqualTo(5);
        });
    }

    @Test
    void loadFailsFastWhenMissionDirectoryIsMissing() {
        assertThatThrownBy(() -> missionMapLoader.load("mission-does-not-exist"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Missing mission directory");
    }

    @Test
    void parseYamlFailsWhenBaseContainsUnknownSymbol() {
        final String yaml = """
                version: 1
                name: bad-map
                size:
                  rows: 1
                  cols: 1
                legend:
                  ".":
                    type: floor
                    label: Walkable floor tile
                base:
                  - "X"
                spawns:
                  - id: core_01
                    type: core
                    state: online
                    at: A1
                    battery: { level: 0, capacity: 5 }
                    health: { level: 1, capacity: 5 }
                """;

        assertThatThrownBy(() -> MissionMapLoader.parseYaml(yaml, "inline"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("missing from legend");
    }

    @Test
    void parseYamlFailsWhenSpawnCoordinateIsOutOfBounds() {
        final String yaml = """
                version: 1
                name: bad-map
                size:
                  rows: 1
                  cols: 1
                legend:
                  ".":
                    type: floor
                    label: Walkable floor tile
                base:
                  - "."
                spawns:
                  - id: core_01
                    type: core
                    state: online
                    at: B1
                    battery: { level: 0, capacity: 5 }
                    health: { level: 1, capacity: 5 }
                """;

        assertThatThrownBy(() -> MissionMapLoader.parseYaml(yaml, "inline"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("out of bounds");
    }

    @Test
    void parseYamlFailsWhenMeterLevelIsInvalid() {
        final String yaml = """
                version: 1
                name: bad-map
                size:
                  rows: 1
                  cols: 1
                legend:
                  ".":
                    type: floor
                    label: Walkable floor tile
                base:
                  - "."
                spawns:
                  - id: core_01
                    type: core
                    state: online
                    at: A1
                    battery: { level: 6, capacity: 5 }
                    health: { level: 1, capacity: 5 }
                """;

        assertThatThrownBy(() -> MissionMapLoader.parseYaml(yaml, "inline"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("battery level must be between 0 and capacity");
    }

    @Test
    void parseYamlForMissionFailsWhenCoreSpawnIsMissing() {
        final String yaml = """
                version: 1
                name: bad-map
                size:
                  rows: 3
                  cols: 3
                legend:
                  ".":
                    type: floor
                    label: Walkable floor tile
                  "D":
                    type: dock
                    label: Docking station
                  "R":
                    type: repair
                    label: Repair station
                base:
                  - "..."
                  - "D.R"
                  - "..."
                spawns:
                  - id: helper_01
                    type: core
                    state: online
                    at: B1
                    battery: { level: 0, capacity: 5 }
                    health: { level: 1, capacity: 5 }
                """;

        assertThatThrownBy(() -> MissionMapLoader.parseYamlForMission(yaml, "inline", "mission-01"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("requires exactly one core spawn with id 'core_01'");
    }

    @Test
    void parseYamlForMissionFailsWhenCoreSpawnIsDuplicated() {
        final String yaml = """
                version: 1
                name: bad-map
                size:
                  rows: 3
                  cols: 3
                legend:
                  ".":
                    type: floor
                    label: Walkable floor tile
                  "D":
                    type: dock
                    label: Docking station
                  "R":
                    type: repair
                    label: Repair station
                base:
                  - "..."
                  - "D.R"
                  - "..."
                spawns:
                  - id: core_01
                    type: core
                    state: online
                    at: B1
                    battery: { level: 0, capacity: 5 }
                    health: { level: 1, capacity: 5 }
                  - id: core_01
                    type: core
                    state: online
                    at: B2
                    battery: { level: 0, capacity: 5 }
                    health: { level: 1, capacity: 5 }
                """;

        assertThatThrownBy(() -> MissionMapLoader.parseYamlForMission(yaml, "inline", "mission-02"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("requires exactly one core spawn with id 'core_01'");
    }

    @Test
    void parseYamlForMissionFailsWhenDockTileTypeIsMissing() {
        final String yaml = """
                version: 1
                name: bad-map
                size:
                  rows: 3
                  cols: 3
                legend:
                  ".":
                    type: floor
                    label: Walkable floor tile
                  "R":
                    type: repair
                    label: Repair station
                base:
                  - "..."
                  - "..R"
                  - "..."
                spawns:
                  - id: core_01
                    type: core
                    state: online
                    at: B1
                    battery: { level: 0, capacity: 5 }
                    health: { level: 1, capacity: 5 }
                """;

        assertThatThrownBy(() -> MissionMapLoader.parseYamlForMission(yaml, "inline", "mission-03"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("requires at least one 'dock' tile type");
    }

    @Test
    void parseYamlForMissionFailsWhenRepairTileTypeIsMissing() {
        final String yaml = """
                version: 1
                name: bad-map
                size:
                  rows: 3
                  cols: 3
                legend:
                  ".":
                    type: floor
                    label: Walkable floor tile
                  "D":
                    type: dock
                    label: Docking station
                base:
                  - "..."
                  - "D.."
                  - "..."
                spawns:
                  - id: core_01
                    type: core
                    state: online
                    at: B1
                    battery: { level: 0, capacity: 5 }
                    health: { level: 1, capacity: 5 }
                """;

        assertThatThrownBy(() -> MissionMapLoader.parseYamlForMission(yaml, "inline", "mission-01"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("requires at least one 'repair' tile type");
    }

    @Test
    void parseYamlFailsWhenCoreSpawnStateIsMissing() {
        final String yaml = """
                version: 1
                name: bad-map
                size:
                  rows: 1
                  cols: 1
                legend:
                  ".":
                    type: floor
                    label: Walkable floor tile
                base:
                  - "."
                spawns:
                  - id: core_01
                    type: core
                    at: A1
                    battery: { level: 0, capacity: 5 }
                    health: { level: 1, capacity: 5 }
                """;

        assertThatThrownBy(() -> MissionMapLoader.parseYaml(yaml, "inline"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("'state' must be a non-blank string");
    }

    @Test
    void parseYamlFailsWhenCoreSpawnStateIsInvalid() {
        final String yaml = """
                version: 1
                name: bad-map
                size:
                  rows: 1
                  cols: 1
                legend:
                  ".":
                    type: floor
                    label: Walkable floor tile
                base:
                  - "."
                spawns:
                  - id: core_01
                    type: core
                    state: standby
                    at: A1
                    battery: { level: 0, capacity: 5 }
                    health: { level: 1, capacity: 5 }
                """;

        assertThatThrownBy(() -> MissionMapLoader.parseYaml(yaml, "inline"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("core spawn state must be 'offline' or 'online'");
    }
}
