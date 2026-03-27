package game.codecolony.mission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class MissionMapLoaderTest {

    private final MissionMapLoader missionMapLoader = new MissionMapLoader();

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
            assertThat(spawn.at()).isEqualTo("B1");
            assertThat(spawn.battery().level()).isEqualTo(5);
            assertThat(spawn.battery().capacity()).isEqualTo(5);
            assertThat(spawn.health().level()).isEqualTo(1);
            assertThat(spawn.health().capacity()).isEqualTo(5);
        });
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
                    at: A1
                    battery: { level: 6, capacity: 5 }
                    health: { level: 1, capacity: 5 }
                """;

        assertThatThrownBy(() -> MissionMapLoader.parseYaml(yaml, "inline"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("battery level must be between 0 and capacity");
    }
}
