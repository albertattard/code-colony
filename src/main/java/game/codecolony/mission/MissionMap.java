package game.codecolony.mission;

import java.util.List;
import java.util.Map;

public record MissionMap(int version,
                         String name,
                         MissionMapSize size,
                         Map<String, MissionMapLegendEntry> legend,
                         List<String> base,
                         List<MissionMapSpawn> spawns) {

    public MissionMapSpawn requireCoreSpawn(final String spawnId) {
        return spawns.stream()
                .filter(spawn -> spawnId.equals(spawn.id()))
                .filter(spawn -> "core".equals(spawn.type()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Invalid map %s: required core spawn '%s' is missing".formatted(name, spawnId)
                ));
    }

    public String requireFirstCoordinateByType(final String tileType) {
        for (int rowIndex = 0; rowIndex < base.size(); rowIndex++) {
            final String rowText = base.get(rowIndex);
            for (int colIndex = 0; colIndex < rowText.length(); colIndex++) {
                final String symbol = String.valueOf(rowText.charAt(colIndex));
                final MissionMapLegendEntry entry = legend.get(symbol);
                if (tileType.equals(entry.type())) {
                    return rowLabel(rowIndex) + (colIndex + 1);
                }
            }
        }

        throw new IllegalStateException("Invalid map %s: no tile with type '%s'".formatted(name, tileType));
    }

    private static String rowLabel(final int rowIndex) {
        return String.valueOf((char) ('A' + rowIndex));
    }
}
