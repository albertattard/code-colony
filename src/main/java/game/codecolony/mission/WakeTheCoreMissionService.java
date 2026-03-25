package game.codecolony.mission;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class WakeTheCoreMissionService {

    private static final String DEFAULT_CODE = "";

    private static final String TITLE = "Mission 01: Wake The CORE";
    private static final String SUMMARY =
            "Standby power is active in Maintenance Room A1. Re-establish a control link and bring the docked CORE online.";
    private static final String OBJECTIVE = "Call CORE.connect(); to bring CORE-01 online.";
    private static final String BRIEFING_HEADING = "Mission Briefing";
    private static final String BRIEFING_CONTEXT =
            "Standby power has returned to Maintenance Room A1. One CORE unit is still docked, charged, and waiting for a control link.";
    private static final String BRIEFING_HINT = "Use CORE.connect(); to bring the docked CORE online.";
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

    public WakeTheCoreMissionService(final WakeTheCoreMissionExecutionService missionExecutionService) {
        this.missionExecutionService = missionExecutionService;
    }

    public MissionPage initialPage() {
        return new MissionPage(
                TITLE,
                SUMMARY,
                OBJECTIVE,
                BRIEFING_HEADING,
                BRIEFING_CONTEXT,
                BRIEFING_HINT,
                HINTS,
                COMMANDS,
                GRID,
                DEFAULT_CODE,
                WakeTheCoreRunResult.initial()
        );
    }

    public MissionPage pageForCode(final String code) {
        return new MissionPage(
                TITLE,
                SUMMARY,
                OBJECTIVE,
                BRIEFING_HEADING,
                BRIEFING_CONTEXT,
                BRIEFING_HINT,
                HINTS,
                COMMANDS,
                GRID,
                code,
                missionExecutionService.execute(code)
        );
    }

    public record MissionPage(String missionTitle, String missionSummary, String missionObjective,
                              String briefingHeading, String briefingContext, String briefingHint,
                              List<String> missionHints, List<CommandReference> availableCommands,
                              List<GridTile> gridTiles, String code, WakeTheCoreRunResult runResult) {
    }

    public record CommandReference(String signature, String description) {
    }

    public record GridTile(String rowLabel, String columnLabel, String cellType, String shortLabel, String fullLabel) {
    }
}
