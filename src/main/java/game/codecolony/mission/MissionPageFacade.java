package game.codecolony.mission;

import game.codecolony.content.NarrativeContentService;
import game.codecolony.content.NarrativeContentService.MissionConsoleContent;
import game.codecolony.content.NarrativeContentService.MissionInitialRunContent;
import game.codecolony.content.NarrativeContentService.MissionNarrativeContent;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public final class MissionPageFacade {

    private final NarrativeContentService narrativeContentService;
    private final MissionExecutionFacade missionExecutionFacade;
    private final MissionBehaviorRegistry missionBehaviorRegistry;
    private final MissionMapLoader missionMapLoader;

    public MissionPageFacade(final NarrativeContentService narrativeContentService,
                             final MissionExecutionFacade missionExecutionFacade) {
        this.narrativeContentService = narrativeContentService;
        this.missionExecutionFacade = missionExecutionFacade;
        this.missionBehaviorRegistry = new MissionBehaviorRegistry();
        this.missionMapLoader = new MissionMapLoader();
    }

    public String defaultCodeForMission(final String missionId) {
        final MissionBehaviorConfig behavior = missionBehaviorRegistry.get(missionId);
        return switch (behavior.objective().kind()) {
            case "connect_once" -> "";
            case "charge_to_full" -> "Core.connect();";
            case "repair_to_full" -> "var core = Core.connect();";
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
                "",
                "",
                "",
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
                "",
                "",
                "",
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
        final String tileType = tile.cellType();
        final String tilePosition = tile.rowLabel() + tile.columnLabel();
        if (!tilePosition.equals(position)) {
            return tile;
        }

        if ("repair".equals(tileType)) {
            return new GridTile(tile.rowLabel(), tile.columnLabel(), "core-repair", "CORE unit on repair station");
        }

        if ("dock".equals(tileType)) {
            return new GridTile(tile.rowLabel(), tile.columnLabel(), "core", "Docked CORE unit");
        }

        return new GridTile(tile.rowLabel(), tile.columnLabel(), "core-floor", "CORE unit");
    }

    private String normalizeInitialCode(final MissionViewConfig config, final String initialCode) {
        return initialCode == null || initialCode.isBlank() ? config.defaultCode() : initialCode;
    }

    private MissionViewConfig requireConfig(final String missionId) {
        final MissionMap missionMap = missionMapLoader.load(missionId);
        final MissionMapSpawn coreSpawn = missionMap.requireCoreSpawn("core_01");
        return new MissionViewConfig(
                missionId,
                defaultCodeForMission(missionId),
                briefingAudioPathForMission(missionId),
                missionMap.requireFirstCoordinateByType("dock"),
                missionMap.requireFirstCoordinateByType("repair"),
                coreSpawn,
                MissionMapAdapter.toGridTiles(missionMap),
                "offline".equalsIgnoreCase(coreSpawn.state())
        );
    }

    private String briefingAudioPathForMission(final String missionId) {
        final Path audioPath = Path.of("src", "main", "resources", "static", "audio", "briefings", missionId + ".mp3");
        if (Files.exists(audioPath)) {
            return "/audio/briefings/" + URLEncoder.encode(missionId, StandardCharsets.UTF_8) + ".mp3";
        }
        return "";
    }

    private static String toStatusState(final String mapState) {
        return "online".equalsIgnoreCase(mapState) ? "Online" : "Offline";
    }

    private record MissionViewConfig(String missionId,
                                     String defaultCode,
                                     String briefingAudioPath,
                                     String dockPosition,
                                     String repairPosition,
                                     MissionMapSpawn coreSpawn,
                                     List<GridTile> baseGrid,
                                     boolean initiallyOffline) {
    }
}
