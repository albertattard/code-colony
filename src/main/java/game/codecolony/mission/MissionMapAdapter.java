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

    private static String rowLabel(final int rowIndex) {
        return String.valueOf((char) ('A' + rowIndex));
    }
}
