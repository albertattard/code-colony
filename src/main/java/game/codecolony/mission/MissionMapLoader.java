package game.codecolony.mission;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MissionMapLoader {

    private static final String MAP_RESOURCE_PATH_TEMPLATE = "content/missions/%s/map.yaml";
    private static final Set<String> CURRENT_MISSIONS = Set.of("mission-01", "mission-02", "mission-03");

    public MissionMap load(final String missionId) {
        final String resourcePath = MAP_RESOURCE_PATH_TEMPLATE.formatted(missionId);
        try (InputStream inputStream = MissionMapLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalStateException("Unable to load mission map: " + resourcePath);
            }

            final String yaml = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return parseYamlForMission(yaml, resourcePath, missionId);
        } catch (IOException ioException) {
            throw new IllegalStateException("Unable to load mission map: " + resourcePath, ioException);
        }
    }

    static MissionMap parseYamlForMission(final String yamlText, final String sourceName, final String missionId) {
        final MissionMap map = parseMap(yamlText, sourceName);
        validateMissionRules(map, missionId, sourceName);
        return map;
    }

    static MissionMap parseYaml(final String yamlText, final String sourceName) {
        return parseMap(yamlText, sourceName);
    }

    private static MissionMap parseMap(final String yamlText, final String sourceName) {
        final Yaml yaml = new Yaml();
        final Object loaded = yaml.load(yamlText);
        if (!(loaded instanceof Map<?, ?> root)) {
            throw new IllegalStateException("Invalid map %s: root must be a mapping".formatted(sourceName));
        }

        final int version = requireInt(root, "version", sourceName);
        final String name = requireString(root, "name", sourceName);

        final Map<?, ?> sizeMap = requireMap(root, "size", sourceName);
        final int rows = requireInt(sizeMap, "rows", sourceName);
        final int cols = requireInt(sizeMap, "cols", sourceName);
        if (rows <= 0 || cols <= 0) {
            throw new IllegalStateException("Invalid map %s: size rows and cols must be positive".formatted(sourceName));
        }
        final MissionMapSize size = new MissionMapSize(rows, cols);

        final Map<?, ?> legendMap = requireMap(root, "legend", sourceName);
        final Map<String, MissionMapLegendEntry> legend = parseLegend(legendMap, sourceName);

        final List<String> base = requireStringList(root, "base", sourceName);
        validateBase(base, size, legend, sourceName);

        final List<MissionMapSpawn> spawns = parseSpawns(requireList(root, "spawns", sourceName), size, sourceName);

        return new MissionMap(version, name, size, Map.copyOf(legend), List.copyOf(base), List.copyOf(spawns));
    }

    private static void validateMissionRules(final MissionMap map, final String missionId, final String sourceName) {
        if (!CURRENT_MISSIONS.contains(missionId)) {
            return;
        }

        final long coreSpawnCount = map.spawns().stream()
                .filter(spawn -> "core_01".equals(spawn.id()))
                .filter(spawn -> "core".equals(spawn.type()))
                .count();
        if (coreSpawnCount != 1) {
            throw new IllegalStateException(
                    "Invalid map %s: mission '%s' requires exactly one core spawn with id 'core_01'"
                            .formatted(sourceName, missionId)
            );
        }

        final Set<String> tileTypes = tileTypesInBase(map);
        requireTileType(tileTypes, "dock", missionId, sourceName);
        requireTileType(tileTypes, "repair", missionId, sourceName);
    }

    private static Set<String> tileTypesInBase(final MissionMap map) {
        final Set<String> tileTypes = new HashSet<>();
        for (final String row : map.base()) {
            for (int index = 0; index < row.length(); index++) {
                final String symbol = String.valueOf(row.charAt(index));
                final MissionMapLegendEntry entry = map.legend().get(symbol);
                if (entry != null) {
                    tileTypes.add(entry.type());
                }
            }
        }
        return tileTypes;
    }

    private static void requireTileType(final Set<String> tileTypes,
                                        final String requiredType,
                                        final String missionId,
                                        final String sourceName) {
        if (!tileTypes.contains(requiredType)) {
            throw new IllegalStateException(
                    "Invalid map %s: mission '%s' requires at least one '%s' tile type"
                            .formatted(sourceName, missionId, requiredType)
            );
        }
    }

    private static Map<String, MissionMapLegendEntry> parseLegend(final Map<?, ?> legendMap,
                                                                  final String sourceName) {
        final Map<String, MissionMapLegendEntry> legend = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : legendMap.entrySet()) {
            if (!(entry.getKey() instanceof String symbol)) {
                throw new IllegalStateException("Invalid map %s: legend keys must be strings".formatted(sourceName));
            }
            if (symbol.length() != 1) {
                throw new IllegalStateException("Invalid map %s: legend symbol '%s' must be one character"
                        .formatted(sourceName, symbol));
            }

            if (!(entry.getValue() instanceof Map<?, ?> valueMap)) {
                throw new IllegalStateException("Invalid map %s: legend entry '%s' must be a mapping"
                        .formatted(sourceName, symbol));
            }

            final String type = requireString(valueMap, "type", sourceName);
            final String label = requireString(valueMap, "label", sourceName);
            legend.put(symbol, new MissionMapLegendEntry(type, label));
        }

        if (legend.isEmpty()) {
            throw new IllegalStateException("Invalid map %s: legend must not be empty".formatted(sourceName));
        }

        return legend;
    }

    private static void validateBase(final List<String> base,
                                     final MissionMapSize size,
                                     final Map<String, MissionMapLegendEntry> legend,
                                     final String sourceName) {
        if (base.size() != size.rows()) {
            throw new IllegalStateException("Invalid map %s: base row count does not match size.rows"
                    .formatted(sourceName));
        }

        for (int row = 0; row < base.size(); row++) {
            final String rowText = base.get(row);
            if (rowText.length() != size.cols()) {
                throw new IllegalStateException("Invalid map %s: base row %d width does not match size.cols"
                        .formatted(sourceName, row + 1));
            }

            for (int col = 0; col < rowText.length(); col++) {
                final String symbol = String.valueOf(rowText.charAt(col));
                if (!legend.containsKey(symbol)) {
                    throw new IllegalStateException("Invalid map %s: base symbol '%s' at row %d col %d is missing from legend"
                            .formatted(sourceName, symbol, row + 1, col + 1));
                }
            }
        }
    }

    private static List<MissionMapSpawn> parseSpawns(final List<?> rawSpawns,
                                                      final MissionMapSize size,
                                                      final String sourceName) {
        final List<MissionMapSpawn> spawns = new ArrayList<>();
        for (int index = 0; index < rawSpawns.size(); index++) {
            final Object rawSpawn = rawSpawns.get(index);
            if (!(rawSpawn instanceof Map<?, ?> spawnMap)) {
                throw new IllegalStateException("Invalid map %s: spawn %d must be a mapping"
                        .formatted(sourceName, index + 1));
            }

            final String id = requireString(spawnMap, "id", sourceName);
            final String type = requireString(spawnMap, "type", sourceName);
            final String state = parseSpawnState(spawnMap, type, sourceName);
            final String at = requireString(spawnMap, "at", sourceName);

            validateCoordinate(at, size, sourceName);

            final MissionMapMeter battery = parseMeter(requireMap(spawnMap, "battery", sourceName), "battery", sourceName);
            final MissionMapMeter health = parseMeter(requireMap(spawnMap, "health", sourceName), "health", sourceName);

            spawns.add(new MissionMapSpawn(id, type, state, at, battery, health));
        }

        return List.copyOf(spawns);
    }

    private static String parseSpawnState(final Map<?, ?> spawnMap,
                                          final String spawnType,
                                          final String sourceName) {
        if (!"core".equals(spawnType)) {
            return "";
        }

        final String state = requireString(spawnMap, "state", sourceName).toLowerCase();
        if (!"offline".equals(state) && !"online".equals(state)) {
            throw new IllegalStateException("Invalid map %s: core spawn state must be 'offline' or 'online'"
                    .formatted(sourceName));
        }
        return state;
    }

    private static MissionMapMeter parseMeter(final Map<?, ?> meterMap,
                                              final String meterName,
                                              final String sourceName) {
        final int level = requireInt(meterMap, "level", sourceName);
        final int capacity = requireInt(meterMap, "capacity", sourceName);

        if (capacity <= 0) {
            throw new IllegalStateException("Invalid map %s: %s capacity must be positive"
                    .formatted(sourceName, meterName));
        }
        if (level < 0 || level > capacity) {
            throw new IllegalStateException("Invalid map %s: %s level must be between 0 and capacity"
                    .formatted(sourceName, meterName));
        }

        return new MissionMapMeter(level, capacity);
    }

    private static void validateCoordinate(final String coordinate,
                                           final MissionMapSize size,
                                           final String sourceName) {
        final String normalized = coordinate.trim();
        if (!normalized.matches("[A-Z][1-9][0-9]*")) {
            throw new IllegalStateException("Invalid map %s: coordinate '%s' must use RowLetter+ColumnNumber format"
                    .formatted(sourceName, coordinate));
        }

        final int rowIndex = normalized.charAt(0) - 'A' + 1;
        final int colIndex = Integer.parseInt(normalized.substring(1));
        if (rowIndex < 1 || rowIndex > size.rows() || colIndex < 1 || colIndex > size.cols()) {
            throw new IllegalStateException("Invalid map %s: coordinate '%s' is out of bounds"
                    .formatted(sourceName, coordinate));
        }
    }

    private static Map<?, ?> requireMap(final Map<?, ?> source, final String key, final String sourceName) {
        final Object value = source.get(key);
        if (!(value instanceof Map<?, ?> mapValue)) {
            throw new IllegalStateException("Invalid map %s: '%s' must be a mapping".formatted(sourceName, key));
        }
        return mapValue;
    }

    private static List<?> requireList(final Map<?, ?> source, final String key, final String sourceName) {
        final Object value = source.get(key);
        if (!(value instanceof List<?> listValue)) {
            throw new IllegalStateException("Invalid map %s: '%s' must be a list".formatted(sourceName, key));
        }
        return listValue;
    }

    private static List<String> requireStringList(final Map<?, ?> source, final String key, final String sourceName) {
        final List<?> listValue = requireList(source, key, sourceName);
        final List<String> values = new ArrayList<>();
        for (final Object value : listValue) {
            if (!(value instanceof String stringValue)) {
                throw new IllegalStateException("Invalid map %s: '%s' list must contain strings".formatted(sourceName, key));
            }
            values.add(stringValue);
        }
        return List.copyOf(values);
    }

    private static String requireString(final Map<?, ?> source, final String key, final String sourceName) {
        final Object value = source.get(key);
        if (!(value instanceof String stringValue) || stringValue.isBlank()) {
            throw new IllegalStateException("Invalid map %s: '%s' must be a non-blank string".formatted(sourceName, key));
        }
        return stringValue;
    }

    private static int requireInt(final Map<?, ?> source, final String key, final String sourceName) {
        final Object value = source.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }

        throw new IllegalStateException("Invalid map %s: '%s' must be an integer".formatted(sourceName, key));
    }
}
