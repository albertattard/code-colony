package game.codecolony.mission.mission02;

import game.codecolony.content.NarrativeContentService;
import game.codecolony.content.NarrativeContentService.MissionNarrativeContent;
import game.codecolony.mission.CommandReference;
import game.codecolony.mission.GridTile;
import game.codecolony.mission.MissionCoreStatus;
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
    private static final List<String> HINTS = List.of(
            "Mission 02 starts from the connected CORE state you reached at the end of Mission 01.",
            "Call Core.connect() to obtain a CORE reference you can reuse in code.",
            "Each successful core.charge(); call fills one power segment. Mission 02 needs 5 / 5."
    );
    private static final List<CommandReference> COMMANDS = List.of(
            new CommandReference("Core.connect()", "Establishes a control link to the next available CORE unit and returns it."),
            new CommandReference("core.charge()", "Restores one battery segment while the CORE is on the docking station.")
    );
    private static final List<GridTile> GRID = List.of(
            new GridTile("A", "1", "floor", "", "Walkable floor tile"),
            new GridTile("A", "2", "floor", "", "Walkable floor tile"),
            new GridTile("A", "3", "floor", "", "Walkable floor tile"),
            new GridTile("B", "1", "core", "C", "Docked CORE unit"),
            new GridTile("B", "2", "floor", "", "Walkable floor tile"),
            new GridTile("B", "3", "repair", "S", "Repair station"),
            new GridTile("C", "1", "floor", "", "Walkable floor tile"),
            new GridTile("C", "2", "floor", "", "Walkable floor tile"),
            new GridTile("C", "3", "floor", "", "Walkable floor tile")
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
        return new MissionPage(
                missionNarrative.title(),
                missionNarrative.summary(),
                missionNarrative.objective(),
                missionNarrative.briefingHtml(),
                BRIEFING_AUDIO_PATH,
                HINTS,
                COMMANDS,
                GRID,
                initialCode,
                initialCode,
                MISSION_PATH,
                missionPathWithCode(initialCode),
                "",
                true,
                initialRunResult()
        );
    }

    public MissionPage pageForCode(final String code, final String initialCode) {
        final String normalizedInitialCode = normalizeInitialCode(initialCode);
        final MissionNarrativeContent missionNarrative = narrativeContentService.loadMissionNarrative("mission-02");
        return new MissionPage(
                missionNarrative.title(),
                missionNarrative.summary(),
                missionNarrative.objective(),
                missionNarrative.briefingHtml(),
                BRIEFING_AUDIO_PATH,
                HINTS,
                COMMANDS,
                GRID,
                code,
                normalizedInitialCode,
                MISSION_PATH,
                missionPathWithCode(normalizedInitialCode),
                "",
                true,
                missionExecutionService.execute(code)
        );
    }

    private MissionRunResult initialRunResult() {
        return new MissionRunResult(
                "Awaiting Run",
                "CORE-01 remains online. Obtain a CORE reference with Core.connect(), then charge it to full power.",
                List.of(
                        "CORE-01 is still docked in Maintenance Room B-1049 and remains online from the previous recovery step.",
                        "The docking station can restore one power segment per successful charge command.",
                        "Mission 02 is complete when the battery reaches 5 / 5."
                ),
                List.of(
                        "Rewrite the carried code so you keep the returned Core in a variable.",
                        "Call core.charge(); enough times to fill all five battery segments."
                ),
                new MissionCoreStatus("CORE-01", "Online", 0, 5, 1, 5, "Connected", "B1",
                        "Control link remains stable from Mission 01. Battery depleted. Structural damage still detected."),
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
}
