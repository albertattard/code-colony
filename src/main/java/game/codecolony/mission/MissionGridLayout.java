package game.codecolony.mission;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class MissionGridLayout {

    private static final String RESOURCE_PATH = "content/mission-grid-layout.txt";
    private static final List<GridTile> DEFAULT_GRID = loadDefaultGrid();

    private MissionGridLayout() {
    }

    public static List<GridTile> defaultGrid() {
        return DEFAULT_GRID;
    }

    static List<GridTile> parse(final List<String> lines) {
        final List<GridTile> tiles = new ArrayList<>();
        for (int index = 0; index < lines.size(); index++) {
            final String line = lines.get(index).trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            final String[] tokens = line.split("\\|", -1);
            if (tokens.length != 4) {
                throw new IllegalStateException("Invalid grid line %d: expected 4 fields".formatted(index + 1));
            }

            tiles.add(new GridTile(
                    tokens[0].trim(),
                    tokens[1].trim(),
                    tokens[2].trim(),
                    tokens[3].trim()
            ));
        }
        return List.copyOf(tiles);
    }

    private static List<GridTile> loadDefaultGrid() {
        try (InputStream inputStream = MissionGridLayout.class.getClassLoader().getResourceAsStream(RESOURCE_PATH)) {
            if (inputStream == null) {
                throw new IllegalStateException("Unable to load grid layout resource: " + RESOURCE_PATH);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return parse(reader.lines().toList());
            }
        } catch (IOException ioException) {
            throw new IllegalStateException("Unable to load grid layout resource: " + RESOURCE_PATH, ioException);
        }
    }
}
