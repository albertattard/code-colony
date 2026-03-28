package game.codecolony.mission.mission02;

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
public final class ChargeTheCoreMissionService {

    private static final String DEFAULT_CODE = "Core.connect();";
    private static final String MISSION_PATH = "/missions/charge-the-core";
    private static final String BRIEFING_AUDIO_PATH = "/audio/briefings/mission-02.mp3";
    private static final MissionMap MISSION_MAP = new MissionMapLoader().load("mission-02");
    private static final MissionMapSpawn CORE_SPAWN = MISSION_MAP.requireCoreSpawn("core_01");
    private static final String CORE_STATE = toStatusState(CORE_SPAWN.state());
    private static final List<GridTile> GRID = MissionMapAdapter.toGridTiles(MISSION_MAP);
    private static final List<String> HINTS = List.of(
            "CORE-01 remains online from Mission 01.",
            "At the start of each run, call Core.connect() to re-establish control and get a CORE reference.",
            "Each successful core.charge(); call fills one power segment. Mission 02 needs 5 / 5."
    );
    private static final List<CommandReference> COMMANDS = List.of(
            new CommandReference("Core.connect()", "Re-establishes control for this run and returns the available CORE unit."),
            new CommandReference("core.charge()", "Restores one battery segment while the CORE is on the docking station.")
    );

    private final ChargeTheCoreMissionExecutionService missionExecutionService;
    private final NarrativeContentService narrativeContentService;

    public ChargeTheCoreMissionService(final ChargeTheCoreMissionExecutionService missionExecutionService,
                                       final NarrativeContentService narrativeContentService) {
        this.missionExecutionService = missionExecutionService;
        this.narrativeContentService = narrativeContentService;
    }

    public MissionPage initialPage(final String carriedCode) {
        final String initialCode = normalizeInitialCode(carriedCode);
        final MissionNarrativeContent missionNarrative = narrativeContentService.loadMissionNarrative("mission-02");
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
        final MissionNarrativeContent missionNarrative = narrativeContentService.loadMissionNarrative("mission-02");
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
                "CORE-01 remains online. Re-establish control for this run with Core.connect(), then charge it to full power.",
                List.of(
                        "CORE-01 is still docked in Maintenance Room B-1049 and remains online from the previous recovery step.",
                        "The docking station can restore one power segment per successful charge command.",
                        "Mission 02 is complete when the battery reaches %d / %d."
                                .formatted(CORE_SPAWN.battery().capacity(), CORE_SPAWN.battery().capacity())
                ),
                List.of(
                        "Start this run with Core.connect() so you can control CORE-01 in code.",
                        "Rewrite the carried code so you keep the returned Core in a variable.",
                        "Call core.charge(); enough times to fill all five battery segments."
                ),
                new MissionCoreStatus("CORE-01", CORE_STATE,
                        CORE_SPAWN.battery().level(),
                        CORE_SPAWN.battery().capacity(),
                        CORE_SPAWN.health().level(),
                        CORE_SPAWN.health().capacity(),
                        "Connected",
                        CORE_SPAWN.at(),
                        "CORE-01 remains online from Mission 01. Re-establish control for this run to operate the unit."),
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
            return new GridTile(tile.rowLabel(), tile.columnLabel(), "core", "Docked CORE unit");
        }
        return tile;
    }

    private static String toStatusState(final String mapState) {
        return "online".equalsIgnoreCase(mapState) ? "Online" : "Offline";
    }
}
