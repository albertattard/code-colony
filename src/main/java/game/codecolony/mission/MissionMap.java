package game.codecolony.mission;

import java.util.List;
import java.util.Map;

public record MissionMap(int version,
                         String name,
                         MissionMapSize size,
                         Map<String, MissionMapLegendEntry> legend,
                         List<String> base,
                         List<MissionMapSpawn> spawns) {
}
