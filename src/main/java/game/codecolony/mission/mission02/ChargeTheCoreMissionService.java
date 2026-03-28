package game.codecolony.mission.mission02;

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
public final class ChargeTheCoreMissionService {

    private static final String DEFAULT_CODE = "Core.connect();";
    private static final String MISSION_PATH = "/missions/charge-the-core";
    private static final String BRIEFING_AUDIO_PATH = "/audio/briefings/mission-02.mp3";
    private static final MissionMap MISSION_MAP = new MissionMapLoader().load("mission-02");
    private static final MissionMapSpawn CORE_SPAWN = MISSION_MAP.requireCoreSpawn("core_01");
    private static final String CORE_STATE = toStatusState(CORE_SPAWN.state());
    private static final List<GridTile> GRID = MissionMapAdapter.toGridTiles(MISSION_MAP);

    private final MissionExecutionFacade missionExecutionFacade;
    private final NarrativeContentService narrativeContentService;

    public ChargeTheCoreMissionService(final MissionExecutionFacade missionExecutionFacade,
                                       final NarrativeContentService narrativeContentService) {
        this.missionExecutionFacade = missionExecutionFacade;
        this.narrativeContentService = narrativeContentService;
    }

    public MissionPage initialPage(final String carriedCode) {
        final String initialCode = normalizeInitialCode(carriedCode);
        final MissionNarrativeContent missionNarrative = narrativeContentService.loadMissionNarrative("mission-02");
        final MissionConsoleContent missionConsole = narrativeContentService.loadMissionConsoleContent("mission-02");
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
        final MissionNarrativeContent missionNarrative = narrativeContentService.loadMissionNarrative("mission-02");
        final MissionConsoleContent missionConsole = narrativeContentService.loadMissionConsoleContent("mission-02");
        final MissionRunResult runResult = missionExecutionFacade.execute("mission-02", code);
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
        final MissionInitialRunContent initialRunContent = narrativeContentService.loadMissionInitialRunContent("mission-02");
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
                .replace("{batteryCapacity}", String.valueOf(CORE_SPAWN.battery().capacity()))
                .replace("{corePosition}", CORE_SPAWN.at());
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
