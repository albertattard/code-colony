package game.codecolony.mission;

import game.codecolony.content.NarrativeContentService;
import game.codecolony.content.NarrativeContentService.MissionConsoleContent;
import game.codecolony.content.NarrativeContentService.MissionInitialRunContent;
import game.codecolony.content.NarrativeContentService.MissionNarrativeContent;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public final class MissionPageFacade {

    private static final Map<String, MissionViewConfig> CONFIG_BY_MISSION_ID = Map.of(
            "mission-01", mission01Config(),
            "mission-02", mission02Config(),
            "mission-03", mission03Config()
    );

    private final NarrativeContentService narrativeContentService;
    private final MissionExecutionFacade missionExecutionFacade;

    public MissionPageFacade(final NarrativeContentService narrativeContentService,
                             final MissionExecutionFacade missionExecutionFacade) {
        this.narrativeContentService = narrativeContentService;
        this.missionExecutionFacade = missionExecutionFacade;
    }

    public String defaultCodeForMission(final String missionId) {
        return switch (missionId) {
            case "mission-01" -> "";
            case "mission-02" -> "Core.connect();";
            case "mission-03" -> "var core = Core.connect();";
            default -> throw new IllegalStateException("Unsupported mission content id: " + missionId);
        };
    }

    public MissionPage initialPageForMission(final String missionId, final String startCode) {
        final MissionViewConfig config = requireConfig(missionId);
        final String initialCode = normalizeInitialCode(config, startCode);
        final MissionNarrativeContent missionNarrative = narrativeContentService.loadMissionNarrative(missionId);
        final MissionConsoleContent missionConsole = narrativeContentService.loadMissionConsoleContent(missionId);
        final MissionRunResult runResult = initialRunResult(config);
        return new MissionPage(
                missionNarrative.title(),
                missionNarrative.summary(),
                missionNarrative.objective(),
                missionNarrative.briefingHtml(),
                config.briefingAudioPath(),
                missionConsole.hints(),
                missionConsole.commands(),
                gridForPosition(config, runResult.coreStatus().position()),
                initialCode,
                initialCode,
                config.missionPath(),
                missionPathWithCode(config, initialCode),
                config.nextMissionPath(),
                true,
                runResult
        );
    }

    public MissionPage pageForCode(final String missionId, final String code, final String startCode) {
        final MissionViewConfig config = requireConfig(missionId);
        final String normalizedStartCode = normalizeInitialCode(config, startCode);
        final MissionNarrativeContent missionNarrative = narrativeContentService.loadMissionNarrative(missionId);
        final MissionConsoleContent missionConsole = narrativeContentService.loadMissionConsoleContent(missionId);
        final MissionRunResult runResult = missionExecutionFacade.execute(missionId, code);
        return new MissionPage(
                missionNarrative.title(),
                missionNarrative.summary(),
                missionNarrative.objective(),
                missionNarrative.briefingHtml(),
                config.briefingAudioPath(),
                missionConsole.hints(),
                missionConsole.commands(),
                gridForPosition(config, runResult.coreStatus().position()),
                code,
                normalizedStartCode,
                config.missionPath(),
                missionPathWithCode(config, normalizedStartCode),
                config.nextMissionPath(),
                true,
                runResult
        );
    }

    private MissionRunResult initialRunResult(final MissionViewConfig config) {
        final MissionInitialRunContent initialRunContent = narrativeContentService.loadMissionInitialRunContent(config.missionId());
        final String resolvedHeadline = resolveTemplate(config, initialRunContent.headline());
        final String resolvedSummary = resolveTemplate(config, initialRunContent.summary());
        final List<String> resolvedEvents = initialRunContent.events().stream()
                .map(event -> resolveTemplate(config, event))
                .toList();
        final List<String> resolvedFeedback = initialRunContent.feedback().stream()
                .map(feedbackItem -> resolveTemplate(config, feedbackItem))
                .toList();
        final String resolvedStatusNote = resolveTemplate(config, initialRunContent.statusNote());

        if (config.initiallyOffline()) {
            return new MissionRunResult(
                    resolvedHeadline,
                    resolvedSummary,
                    resolvedEvents,
                    resolvedFeedback,
                    new MissionCoreStatus(
                            "CORE-01",
                            toStatusState(config.coreSpawn().state()),
                            null,
                            null,
                            null,
                            null,
                            "",
                            "",
                            resolvedStatusNote
                    ),
                    "",
                    "",
                    false
            );
        }

        return new MissionRunResult(
                resolvedHeadline,
                resolvedSummary,
                resolvedEvents,
                resolvedFeedback,
                new MissionCoreStatus(
                        "CORE-01",
                        toStatusState(config.coreSpawn().state()),
                        config.coreSpawn().battery().level(),
                        config.coreSpawn().battery().capacity(),
                        config.coreSpawn().health().level(),
                        config.coreSpawn().health().capacity(),
                        "Connected",
                        config.coreSpawn().at(),
                        resolvedStatusNote
                ),
                "",
                "",
                false
        );
    }

    private String resolveTemplate(final MissionViewConfig config, final String value) {
        return value
                .replace("{dockPosition}", config.dockPosition())
                .replace("{repairPosition}", config.repairPosition())
                .replace("{batteryCapacity}", Integer.toString(config.coreSpawn().battery().capacity()))
                .replace("{corePosition}", config.coreSpawn().at());
    }

    private List<GridTile> gridForPosition(final MissionViewConfig config, final String position) {
        final String normalizedPosition = position == null || position.isBlank() ? config.coreSpawn().at() : position;
        return config.baseGrid().stream()
                .map(tile -> coreTileAtPosition(config, tile, normalizedPosition))
                .toList();
    }

    private GridTile coreTileAtPosition(final MissionViewConfig config, final GridTile tile, final String position) {
        final String tilePosition = tile.rowLabel() + tile.columnLabel();
        if (!tilePosition.equals(position)) {
            if (config.restoreDockOnVacate() && config.dockPosition().equals(tilePosition)) {
                return new GridTile(tile.rowLabel(), tile.columnLabel(), "dock", "Docking station");
            }
            if (config.renderEmptyTilesAsFloor() && !config.repairPosition().equals(tilePosition)) {
                return new GridTile(tile.rowLabel(), tile.columnLabel(), "floor", "Walkable floor tile");
            }
            return tile;
        }

        if (config.repairPosition().equals(tilePosition) && config.showRepairOverlayOnCore()) {
            return new GridTile(tile.rowLabel(), tile.columnLabel(), "core-repair", "CORE unit on repair station");
        }

        if (config.dockPosition().equals(tilePosition) || !config.showFloorOverlayOnCore()) {
            return new GridTile(tile.rowLabel(), tile.columnLabel(), "core", "Docked CORE unit");
        }

        return new GridTile(tile.rowLabel(), tile.columnLabel(), "core-floor", "CORE unit");
    }

    private String missionPathWithCode(final MissionViewConfig config, final String initialCode) {
        if (config.includeCodeInResetPath()) {
            return config.missionPath() + "?code=" + URLEncoder.encode(initialCode, StandardCharsets.UTF_8);
        }
        return config.missionPath();
    }

    private String normalizeInitialCode(final MissionViewConfig config, final String initialCode) {
        return initialCode == null || initialCode.isBlank() ? config.defaultCode() : initialCode;
    }

    private MissionViewConfig requireConfig(final String missionId) {
        final MissionViewConfig config = CONFIG_BY_MISSION_ID.get(missionId);
        if (config == null) {
            throw new IllegalStateException("Unsupported mission content id: " + missionId);
        }
        return config;
    }

    private static MissionViewConfig mission01Config() {
        final MissionMap missionMap = new MissionMapLoader().load("mission-01");
        final MissionMapSpawn coreSpawn = missionMap.requireCoreSpawn("core_01");
        return new MissionViewConfig(
                "mission-01",
                "",
                "/missions/wake-the-core",
                "/audio/briefings/mission-01.mp3",
                "/missions/charge-the-core",
                missionMap.requireFirstCoordinateByType("dock"),
                missionMap.requireFirstCoordinateByType("repair"),
                coreSpawn,
                MissionMapAdapter.toGridTiles(missionMap),
                true,
                true,
                true,
                false,
                false,
                false
        );
    }

    private static MissionViewConfig mission02Config() {
        final MissionMap missionMap = new MissionMapLoader().load("mission-02");
        final MissionMapSpawn coreSpawn = missionMap.requireCoreSpawn("core_01");
        return new MissionViewConfig(
                "mission-02",
                "Core.connect();",
                "/missions/charge-the-core",
                "/audio/briefings/mission-02.mp3",
                "",
                missionMap.requireFirstCoordinateByType("dock"),
                missionMap.requireFirstCoordinateByType("repair"),
                coreSpawn,
                MissionMapAdapter.toGridTiles(missionMap),
                false,
                false,
                false,
                false,
                true,
                false
        );
    }

    private static MissionViewConfig mission03Config() {
        final MissionMap missionMap = new MissionMapLoader().load("mission-03");
        final MissionMapSpawn coreSpawn = missionMap.requireCoreSpawn("core_01");
        return new MissionViewConfig(
                "mission-03",
                "var core = Core.connect();",
                "/missions/repair-the-core",
                "",
                "",
                missionMap.requireFirstCoordinateByType("dock"),
                missionMap.requireFirstCoordinateByType("repair"),
                coreSpawn,
                MissionMapAdapter.toGridTiles(missionMap),
                false,
                true,
                true,
                true,
                true,
                true
        );
    }

    private static String toStatusState(final String mapState) {
        return "online".equalsIgnoreCase(mapState) ? "Online" : "Offline";
    }

    private record MissionViewConfig(String missionId,
                                     String defaultCode,
                                     String missionPath,
                                     String briefingAudioPath,
                                     String nextMissionPath,
                                     String dockPosition,
                                     String repairPosition,
                                     MissionMapSpawn coreSpawn,
                                     List<GridTile> baseGrid,
                                     boolean initiallyOffline,
                                     boolean showFloorOverlayOnCore,
                                     boolean showRepairOverlayOnCore,
                                     boolean restoreDockOnVacate,
                                     boolean includeCodeInResetPath,
                                     boolean renderEmptyTilesAsFloor) {
    }
}
