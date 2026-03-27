package game.codecolony.mission;

import java.util.List;

public final class MissionMapAdapter {

    private MissionMapAdapter() {
    }

    public static List<GridTile> toGridTiles(final MissionMap map) {
        final List<String> base = map.base();
        return java.util.stream.IntStream.range(0, base.size())
                .boxed()
                .flatMap(rowIndex -> {
                    final String rowText = base.get(rowIndex);
                    return java.util.stream.IntStream.range(0, rowText.length())
                            .mapToObj(colIndex -> {
                                final String symbol = String.valueOf(rowText.charAt(colIndex));
                                final MissionMapLegendEntry entry = map.legend().get(symbol);
                                return new GridTile(
                                        rowLabel(rowIndex),
                                        Integer.toString(colIndex + 1),
                                        entry.type(),
                                        entry.label()
                                );
                            });
                })
                .toList();
    }

    public static MissionMapSpawn requireCoreSpawn(final MissionMap map, final String spawnId) {
        return map.spawns().stream()
                .filter(spawn -> spawnId.equals(spawn.id()))
                .filter(spawn -> "core".equals(spawn.type()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Invalid map %s: required core spawn '%s' is missing".formatted(map.name(), spawnId)
                ));
    }

    public static String requireFirstCoordinateByType(final MissionMap map, final String tileType) {
        final List<String> base = map.base();
        for (int rowIndex = 0; rowIndex < base.size(); rowIndex++) {
            final String rowText = base.get(rowIndex);
            for (int colIndex = 0; colIndex < rowText.length(); colIndex++) {
                final String symbol = String.valueOf(rowText.charAt(colIndex));
                final MissionMapLegendEntry entry = map.legend().get(symbol);
                if (tileType.equals(entry.type())) {
                    return rowLabel(rowIndex) + (colIndex + 1);
                }
            }
        }

        throw new IllegalStateException("Invalid map %s: no tile with type '%s'".formatted(map.name(), tileType));
    }

    private static String rowLabel(final int rowIndex) {
        return String.valueOf((char) ('A' + rowIndex));
    }
}
