package game.codecolony.mission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

class MissionGridLayoutTest {

    @Test
    void parseReadsPipeSeparatedTilesAndSkipsComments() {
        final List<GridTile> tiles = MissionGridLayout.parse(List.of(
                "# row|column|cellType|shortLabel|fullLabel",
                "A|1|floor||Walkable floor tile",
                "B|1|core|C|Docked CORE unit"
        ));

        assertThat(tiles).containsExactly(
                new GridTile("A", "1", "floor", "", "Walkable floor tile"),
                new GridTile("B", "1", "core", "C", "Docked CORE unit")
        );
    }

    @Test
    void parseFailsWhenColumnCountIsInvalid() {
        assertThatThrownBy(() -> MissionGridLayout.parse(List.of("A|1|floor")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("expected 5 fields");
    }
}
