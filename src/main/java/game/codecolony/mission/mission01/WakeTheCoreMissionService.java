package game.codecolony.mission.mission01;

import game.codecolony.content.NarrativeContentService;
import game.codecolony.content.NarrativeContentService.MissionConsoleContent;
import game.codecolony.content.NarrativeContentService.MissionInitialRunContent;
import game.codecolony.content.NarrativeContentService.MissionNarrativeContent;
import game.codecolony.mission.GridTile;
import game.codecolony.mission.MissionCoreStatus;
import game.codecolony.mission.MissionExecutionFacade;
import game.codecolony.mission.MissionMap;
import game.codecolony.mission.MissionMapAdapter;
import game.codecolony.mission.MissionMapLoader;
import game.codecolony.mission.MissionMapSpawn;
import game.codecolony.mission.MissionPage;
import game.codecolony.mission.MissionRunResult;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public final class WakeTheCoreMissionService {

    private static final String DEFAULT_CODE = "";
    private static final String MISSION_PATH = "/missions/wake-the-core";
    private static final String BRIEFING_AUDIO_PATH = "/audio/briefings/mission-01.mp3";
    private static final String NEXT_MISSION_PATH = "/missions/charge-the-core";
    private static final MissionMap MISSION_MAP = new MissionMapLoader().load("mission-01");
    private static final MissionMapSpawn CORE_SPAWN = MISSION_MAP.requireCoreSpawn("core_01");
    private static final String CORE_STATE = toStatusState(CORE_SPAWN.state());
    private static final String DOCK_POSITION = MISSION_MAP.requireFirstCoordinateByType("dock");
    private static final String REPAIR_POSITION = MISSION_MAP.requireFirstCoordinateByType("repair");
    private static final List<GridTile> GRID = MissionMapAdapter.toGridTiles(MISSION_MAP);

    private final MissionExecutionFacade missionExecutionFacade;
    private final NarrativeContentService narrativeContentService;

    public WakeTheCoreMissionService(final MissionExecutionFacade missionExecutionFacade,
                                     final NarrativeContentService narrativeContentService) {
        this.missionExecutionFacade = missionExecutionFacade;
        this.narrativeContentService = narrativeContentService;
    }

    public MissionPage initialPage() {
        final MissionNarrativeContent missionNarrative = narrativeContentService.loadMissionNarrative("mission-01");
        final MissionConsoleContent missionConsole = narrativeContentService.loadMissionConsoleContent("mission-01");
        final MissionRunResult runResult = initialRunResult();
        return new MissionPage(
                missionNarrative.title(),
                missionNarrative.summary(),
                missionNarrative.objective(),
                missionNarrative.briefingHtml(),
                BRIEFING_AUDIO_PATH,
                missionConsole.hints(),
                missionConsole.commands(),
                gridForPosition(runResult.coreStatus().position()),
                DEFAULT_CODE,
                DEFAULT_CODE,
                MISSION_PATH,
                MISSION_PATH,
                NEXT_MISSION_PATH,
                true,
                runResult
        );
    }

    public MissionPage pageForCode(final String code) {
        final MissionNarrativeContent missionNarrative = narrativeContentService.loadMissionNarrative("mission-01");
        final MissionConsoleContent missionConsole = narrativeContentService.loadMissionConsoleContent("mission-01");
        final MissionRunResult runResult = missionExecutionFacade.execute("mission-01", code);
        return new MissionPage(
                missionNarrative.title(),
                missionNarrative.summary(),
                missionNarrative.objective(),
                missionNarrative.briefingHtml(),
                BRIEFING_AUDIO_PATH,
                missionConsole.hints(),
                missionConsole.commands(),
                gridForPosition(runResult.coreStatus().position()),
                code,
                DEFAULT_CODE,
                MISSION_PATH,
                MISSION_PATH,
                nextMissionPath(code),
                true,
                runResult
        );
    }

    private MissionRunResult initialRunResult() {
        final MissionInitialRunContent initialRunContent = narrativeContentService.loadMissionInitialRunContent("mission-01");
        return new MissionRunResult(
                resolveTemplate(initialRunContent.headline()),
                resolveTemplate(initialRunContent.summary()),
                initialRunContent.events().stream()
                        .map(this::resolveTemplate)
                        .toList(),
                initialRunContent.feedback().stream()
                        .map(this::resolveTemplate)
                        .toList(),
                new MissionCoreStatus(
                        "CORE-01",
                        CORE_STATE,
                        null,
                        null,
                        null,
                        null,
                        "",
                        "",
                        resolveTemplate(initialRunContent.statusNote())
                ),
                "",
                "",
                false
        );
    }

    private String resolveTemplate(final String value) {
        return value
                .replace("{dockPosition}", DOCK_POSITION)
                .replace("{repairPosition}", REPAIR_POSITION);
    }

    private String nextMissionPath(final String code) {
        return NEXT_MISSION_PATH + "?code=" + URLEncoder.encode(code, StandardCharsets.UTF_8);
    }

    private List<GridTile> gridForPosition(final String position) {
        final String normalizedPosition = position == null || position.isBlank() ? CORE_SPAWN.at() : position;
        return GRID.stream()
                .map(tile -> coreTileAtPosition(tile, normalizedPosition))
                .toList();
    }

    private GridTile coreTileAtPosition(final GridTile tile, final String position) {
        final String tilePosition = tile.rowLabel() + tile.columnLabel();
        if (tilePosition.equals(position) && DOCK_POSITION.equals(tilePosition)) {
            return new GridTile(tile.rowLabel(), tile.columnLabel(), "core", "Docked CORE unit");
        }
        if (tilePosition.equals(position) && REPAIR_POSITION.equals(tilePosition)) {
            return new GridTile(tile.rowLabel(), tile.columnLabel(), "core-repair", "CORE unit on repair station");
        }
        if (tilePosition.equals(position)) {
            return new GridTile(tile.rowLabel(), tile.columnLabel(), "core-floor", "CORE unit");
        }
        return tile;
    }

    private static String toStatusState(final String mapState) {
        return "online".equalsIgnoreCase(mapState) ? "Online" : "Offline";
    }
}
