package game.codecolony.mission;

import game.codecolony.content.NarrativeContentService;
import game.codecolony.content.NarrativeContentService.MissionNarrativeContent;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public final class WakeTheCoreMissionService {

    private static final String DEFAULT_CODE = "";
    private static final String BRIEFING_AUDIO_PATH = "/audio/briefings/mission-01.mp3";
    private static final List<String> HINTS = List.of(
            "Mission 01 expects a single method call.",
            "You do not need a variable yet.",
            "When CORE.connect(); works, the status panel should change from Offline to Online."
    );
    private static final List<CommandReference> COMMANDS = List.of(
            new CommandReference("CORE.connect()", "Establishes a control link to the next available CORE unit.")
    );
    private static final List<GridTile> GRID = List.of(
            new GridTile("A", "1", "floor", "", "Walkable floor tile"),
            new GridTile("A", "2", "floor", "", "Walkable floor tile"),
            new GridTile("A", "3", "floor", "", "Walkable floor tile"),
            new GridTile("B", "1", "core", "C", "Docked CORE unit"),
            new GridTile("B", "2", "floor", "", "Walkable floor tile"),
            new GridTile("B", "3", "relay", "R", "Damaged relay cabinet"),
            new GridTile("C", "1", "floor", "", "Walkable floor tile"),
            new GridTile("C", "2", "floor", "", "Walkable floor tile"),
            new GridTile("C", "3", "floor", "", "Walkable floor tile")
    );

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
                WakeTheCoreRunResult.initial()
        );
    }

    public MissionPage pageForCode(final String code) {
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
                code,
                missionExecutionService.execute(code)
        );
    }

    public record MissionPage(String missionTitle, String missionSummary, String missionObjective,
                              String briefingHtml, String briefingAudioPath,
                              List<String> missionHints, List<CommandReference> availableCommands,
                              List<GridTile> gridTiles, String code, WakeTheCoreRunResult runResult) {
    }

    public record CommandReference(String signature, String description) {
    }

    public record GridTile(String rowLabel, String columnLabel, String cellType, String shortLabel, String fullLabel) {
    }
}
