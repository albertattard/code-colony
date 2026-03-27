package game.codecolony.mission.mission01;

import game.codecolony.content.NarrativeContentService;
import game.codecolony.content.NarrativeContentService.MissionNarrativeContent;
import game.codecolony.mission.CommandReference;
import game.codecolony.mission.GridTile;
import game.codecolony.mission.MissionCoreStatus;
import game.codecolony.mission.MissionGridLayout;
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
    private static final List<String> HINTS = List.of(
            "Mission 01 expects a single method call.",
            "You do not need a variable yet.",
            "When Core.connect(); works, the status panel should change from Offline to Online and reveal the CORE's condition."
    );
    private static final List<CommandReference> COMMANDS = List.of(
            new CommandReference("Core.connect()", "Establishes a control link to the next available CORE unit.")
    );
    private static final List<GridTile> GRID = MissionGridLayout.defaultGrid();

    private final WakeTheCoreMissionExecutionService missionExecutionService;
    private final NarrativeContentService narrativeContentService;

    public WakeTheCoreMissionService(final WakeTheCoreMissionExecutionService missionExecutionService,
                                     final NarrativeContentService narrativeContentService) {
        this.missionExecutionService = missionExecutionService;
        this.narrativeContentService = narrativeContentService;
    }

    public MissionPage initialPage() {
        final MissionNarrativeContent missionNarrative = narrativeContentService.loadMissionNarrative("mission-01");
        return new MissionPage(
                missionNarrative.title(),
                missionNarrative.summary(),
                missionNarrative.objective(),
                missionNarrative.briefingHtml(),
                BRIEFING_AUDIO_PATH,
                HINTS,
                COMMANDS,
                GRID,
                DEFAULT_CODE,
                DEFAULT_CODE,
                MISSION_PATH,
                MISSION_PATH,
                NEXT_MISSION_PATH,
                true,
                initialRunResult()
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
                GRID,
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
                        "Running code will update the CORE status and feedback panels."
                ),
                List.of(
                        "Mission 01 expects a single method call: Core.connect();",
                        "The first successful run should bring CORE-01 online."
                ),
                new MissionCoreStatus("CORE-01", "Offline", null, null, null, null, "", "", "No telemetry available while offline."),
                "",
                "",
                false
        );
    }

    private String nextMissionPath(final String code) {
        return NEXT_MISSION_PATH + "?code=" + URLEncoder.encode(code, StandardCharsets.UTF_8);
    }
}
