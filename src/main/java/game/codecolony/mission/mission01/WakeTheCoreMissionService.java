package game.codecolony.mission.mission01;

import game.codecolony.content.NarrativeContentService;
import game.codecolony.content.NarrativeContentService.MissionNarrativeContent;
import game.codecolony.mission.CommandReference;
import game.codecolony.mission.GridTile;
import game.codecolony.mission.MissionCoreStatus;
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
    private static final List<String> HINTS = List.of(
            "Mission 01 expects a single method call.",
            "You do not need a variable yet.",
            "When Core.connect(); works, the status panel should change from Offline to Online and reveal the CORE's condition."
    );
    private static final List<CommandReference> COMMANDS = List.of(
            new CommandReference("Core.connect()", "Establishes a control link to the next available CORE unit.")
    );
    private static final List<GridTile> GRID = MissionMapAdapter.toGridTiles(MISSION_MAP);

    private final WakeTheCoreMissionExecutionService missionExecutionService;
    private final NarrativeContentService narrativeContentService;

    public WakeTheCoreMissionService(final WakeTheCoreMissionExecutionService missionExecutionService,
                                     final NarrativeContentService narrativeContentService) {
        this.missionExecutionService = missionExecutionService;
        this.narrativeContentService = narrativeContentService;
    }

    public MissionPage initialPage() {
        final MissionNarrativeContent missionNarrative = narrativeContentService.loadMissionNarrative("mission-01");
        final MissionRunResult runResult = initialRunResult();
        return new MissionPage(
                missionNarrative.title(),
                missionNarrative.summary(),
                missionNarrative.objective(),
                missionNarrative.briefingHtml(),
                BRIEFING_AUDIO_PATH,
                HINTS,
                COMMANDS,
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
        final MissionRunResult runResult = missionExecutionService.execute(code);
        return new MissionPage(
                missionNarrative.title(),
                missionNarrative.summary(),
                missionNarrative.objective(),
                missionNarrative.briefingHtml(),
                BRIEFING_AUDIO_PATH,
                HINTS,
                COMMANDS,
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
        return new MissionRunResult(
                "Awaiting Run",
                "Enter Core.connect(); and click Run to bring CORE-01 online.",
                List.of(
                        "CORE-01 is docked in Maintenance Room B-1049.",
                        "The control link is offline.",
                        "Docking station is located at " + DOCK_POSITION + ".",
                        "Repair station is located at " + REPAIR_POSITION + ".",
                        "Running code will update the CORE status and feedback panels."
                ),
                List.of(
                        "Mission 01 expects a single method call: Core.connect();",
                        "The first successful run should bring CORE-01 online."
                ),
                new MissionCoreStatus("CORE-01", CORE_STATE, null, null, null, null, "", "", "No telemetry available while offline."),
                "",
                "",
                false
        );
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
