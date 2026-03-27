package game.codecolony.mission.mission03;

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
public final class RepairTheCoreMissionService {

    private static final String DEFAULT_CODE = "var core = Core.connect();";
    private static final String MISSION_PATH = "/missions/repair-the-core";
    private static final String BRIEFING_AUDIO_PATH = "";
    private static final MissionMap MISSION_MAP = new MissionMapLoader().load("mission-03");
    private static final MissionMapSpawn CORE_SPAWN = MissionMapAdapter.requireCoreSpawn(MISSION_MAP, "core_01");
    private static final String DOCK_POSITION = MissionMapAdapter.requireFirstCoordinateByType(MISSION_MAP, "dock");
    private static final String REPAIR_POSITION = MissionMapAdapter.requireFirstCoordinateByType(MISSION_MAP, "repair");
    private static final List<String> HINTS = List.of(
            "Mission 03 expects movement from %s to %s before repair.".formatted(DOCK_POSITION, REPAIR_POSITION),
            "Use core.move(); to reach the repair station.",
            "Call core.repair(); on %s to restore health.".formatted(REPAIR_POSITION)
    );
    private static final List<CommandReference> COMMANDS = List.of(
            new CommandReference("Core.connect()", "Establishes a control link to the next available CORE unit and returns it."),
            new CommandReference("core.move()", "Moves CORE-01 one tile east in this mission room."),
            new CommandReference("core.repair()", "Repairs CORE-01 when it is on the repair station tile.")
    );
    private static final List<GridTile> GRID = MissionMapAdapter.toGridTiles(MISSION_MAP);

    private final RepairTheCoreMissionExecutionService missionExecutionService;
    private final NarrativeContentService narrativeContentService;

    public RepairTheCoreMissionService(final RepairTheCoreMissionExecutionService missionExecutionService,
                                       final NarrativeContentService narrativeContentService) {
        this.missionExecutionService = missionExecutionService;
        this.narrativeContentService = narrativeContentService;
    }

    public MissionPage initialPage(final String carriedCode) {
        final String initialCode = normalizeInitialCode(carriedCode);
        final MissionNarrativeContent missionNarrative = narrativeContentService.loadMissionNarrative("mission-03");
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
                normalizedInitialCode,
                MISSION_PATH,
                missionPathWithCode(normalizedInitialCode),
                "",
                true,
                runResult
        );
    }

    private MissionRunResult initialRunResult() {
        return new MissionRunResult(
                "Awaiting Run",
                "Move CORE-01 to the repair station at B3 and repair it.",
                List.of(
                        "CORE-01 is online, charged, and docked at " + DOCK_POSITION + ".",
                        "Repair station is located at " + REPAIR_POSITION + ".",
                        "Mission success requires reaching " + REPAIR_POSITION + " and calling core.repair()."
                ),
                List.of(
                        "Connect to CORE-01 and keep the returned Core in a variable.",
                        "Use two moves to reach B3, then call core.repair()."
                ),
                new MissionCoreStatus("CORE-01", "Online",
                        CORE_SPAWN.battery().level(),
                        CORE_SPAWN.battery().capacity(),
                        CORE_SPAWN.health().level(),
                        CORE_SPAWN.health().capacity(),
                        "Connected",
                        CORE_SPAWN.at(),
                        "CORE-01 is charged but damaged. Repair station available at %s.".formatted(REPAIR_POSITION)),
                "",
                "",
                false
        );
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
}
