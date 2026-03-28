package game.codecolony.mission.mission03;

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
public final class RepairTheCoreMissionService {

    private static final String DEFAULT_CODE = "var core = Core.connect();";
    private static final String MISSION_PATH = "/missions/repair-the-core";
    private static final String BRIEFING_AUDIO_PATH = "";
    private static final MissionMap MISSION_MAP = new MissionMapLoader().load("mission-03");
    private static final MissionMapSpawn CORE_SPAWN = MISSION_MAP.requireCoreSpawn("core_01");
    private static final String CORE_STATE = toStatusState(CORE_SPAWN.state());
    private static final String DOCK_POSITION = MISSION_MAP.requireFirstCoordinateByType("dock");
    private static final String REPAIR_POSITION = MISSION_MAP.requireFirstCoordinateByType("repair");
    private static final List<GridTile> GRID = MissionMapAdapter.toGridTiles(MISSION_MAP);

    private final MissionExecutionFacade missionExecutionFacade;
    private final NarrativeContentService narrativeContentService;

    public RepairTheCoreMissionService(final MissionExecutionFacade missionExecutionFacade,
                                       final NarrativeContentService narrativeContentService) {
        this.missionExecutionFacade = missionExecutionFacade;
        this.narrativeContentService = narrativeContentService;
    }

    public MissionPage initialPage(final String carriedCode) {
        final String initialCode = normalizeInitialCode(carriedCode);
        final MissionNarrativeContent missionNarrative = narrativeContentService.loadMissionNarrative("mission-03");
        final MissionConsoleContent missionConsole = narrativeContentService.loadMissionConsoleContent("mission-03");
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
                initialCode,
                initialCode,
                MISSION_PATH,
                missionPathWithCode(initialCode),
                "",
                true,
                runResult
        );
    }

    public MissionPage pageForCode(final String code, final String initialCode) {
        final String normalizedInitialCode = normalizeInitialCode(initialCode);
        final MissionNarrativeContent missionNarrative = narrativeContentService.loadMissionNarrative("mission-03");
        final MissionConsoleContent missionConsole = narrativeContentService.loadMissionConsoleContent("mission-03");
        final MissionRunResult runResult = missionExecutionFacade.execute("mission-03", code);
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
                normalizedInitialCode,
                MISSION_PATH,
                missionPathWithCode(normalizedInitialCode),
                "",
                true,
                runResult
        );
    }

    private MissionRunResult initialRunResult() {
        final MissionInitialRunContent initialRunContent = narrativeContentService.loadMissionInitialRunContent("mission-03");
        return new MissionRunResult(
                resolveTemplate(initialRunContent.headline()),
                resolveTemplate(initialRunContent.summary()),
                initialRunContent.events().stream()
                        .map(this::resolveTemplate)
                        .toList(),
                initialRunContent.feedback().stream()
                        .map(this::resolveTemplate)
                        .toList(),
                new MissionCoreStatus("CORE-01", CORE_STATE,
                        CORE_SPAWN.battery().level(),
                        CORE_SPAWN.battery().capacity(),
                        CORE_SPAWN.health().level(),
                        CORE_SPAWN.health().capacity(),
                        "Connected",
                        CORE_SPAWN.at(),
                        resolveTemplate(initialRunContent.statusNote())),
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

    private String normalizeInitialCode(final String initialCode) {
        return initialCode == null || initialCode.isBlank() ? DEFAULT_CODE : initialCode;
    }

    private String missionPathWithCode(final String initialCode) {
        return MISSION_PATH + "?code=" + URLEncoder.encode(initialCode, StandardCharsets.UTF_8);
    }

    private List<GridTile> gridForPosition(final String position) {
        final String normalizedPosition = position == null || position.isBlank() ? CORE_SPAWN.at() : position;
        return GRID.stream()
                .map(tile -> coreTileAtPosition(tile, normalizedPosition))
                .toList();
    }

    private GridTile coreTileAtPosition(final GridTile tile, final String position) {
        final String tilePosition = tile.rowLabel() + tile.columnLabel();
        if (tilePosition.equals(position)) {
            if (DOCK_POSITION.equals(tilePosition)) {
                return new GridTile(tile.rowLabel(), tile.columnLabel(), "core", "Docked CORE unit");
            }
            if (REPAIR_POSITION.equals(tilePosition)) {
                return new GridTile(tile.rowLabel(), tile.columnLabel(), "core-repair", "CORE unit on repair station");
            }
            return new GridTile(tile.rowLabel(), tile.columnLabel(), "core-floor", "CORE unit");
        }
        if (DOCK_POSITION.equals(tilePosition)) {
            return new GridTile(tile.rowLabel(), tile.columnLabel(), "dock", "Docking station");
        }
        if (REPAIR_POSITION.equals(tilePosition)) {
            return tile;
        }
        return new GridTile(tile.rowLabel(), tile.columnLabel(), "floor", "Walkable floor tile");
    }

    private static String toStatusState(final String mapState) {
        return "online".equalsIgnoreCase(mapState) ? "Online" : "Offline";
    }
}
