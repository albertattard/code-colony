package game.codecolony.mission;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class WakeTheCoreMissionService {

    private static final String DEFAULT_CODE = """
            var core = CORE.connect();
            core.moveRight();
            core.repair();
            """;

    private static final String TITLE = "Mission 01: Wake The CORE";
    private static final String SUMMARY =
            "Standby power is active in Maintenance Room A1. Wake the docked CORE unit, move it to the relay cabinet, and restore relay access.";
    private static final String OBJECTIVE = "Connect to CORE-01, move one tile right, and repair the damaged relay.";
    private static final List<String> HINTS = List.of(
            "Start by connecting to the docked CORE unit.",
            "The first mission room is small on purpose. Focus on sequence.",
            "This shell does not execute code yet. It previews the future interaction flow."
    );
    private static final List<CommandReference> COMMANDS = List.of(
            new CommandReference("CORE.connect()", "Establishes a control link to the docked robot."),
            new CommandReference("core.moveRight()", "Moves the CORE unit one tile to the right."),
            new CommandReference("core.repair()", "Repairs the damaged system on the current tile.")
    );
    private static final List<GridTile> GRID = List.of(
            new GridTile("A", "1", "wall", "W", "Wall"),
            new GridTile("A", "2", "wall", "W", "Wall"),
            new GridTile("A", "3", "wall", "W", "Wall"),
            new GridTile("B", "1", "core", "C", "Docked CORE unit"),
            new GridTile("B", "2", "floor", ".", "Walkable floor tile"),
            new GridTile("B", "3", "relay", "R", "Damaged relay cabinet"),
            new GridTile("C", "1", "wall", "W", "Wall"),
            new GridTile("C", "2", "floor", ".", "Walkable floor tile"),
            new GridTile("C", "3", "wall", "W", "Wall")
    );

    public MissionPage initialPage() {
        return new MissionPage(
                TITLE,
                SUMMARY,
                OBJECTIVE,
                HINTS,
                COMMANDS,
                GRID,
                DEFAULT_CODE,
                MissionRunResult.initial()
        );
    }

    public MissionPage pageForCode(final String code) {
        return new MissionPage(
                TITLE,
                SUMMARY,
                OBJECTIVE,
                HINTS,
                COMMANDS,
                GRID,
                code,
                MissionRunResult.placeholder(code)
        );
    }

    public record MissionPage(String missionTitle, String missionSummary, String missionObjective,
                              List<String> missionHints, List<CommandReference> availableCommands,
                              List<GridTile> gridTiles, String code, MissionRunResult runResult) {
    }

    public record CommandReference(String signature, String description) {
    }

    public record GridTile(String rowLabel, String columnLabel, String cellType, String shortLabel, String fullLabel) {
    }

    public record CoreStatus(String state, String battery, String dock, String position, String note) {
    }

    public record MissionRunResult(String headline, String summary, List<String> simulationEvents,
                                   List<String> feedbackItems, CoreStatus coreStatus, boolean success) {

        static MissionRunResult initial() {
            return new MissionRunResult(
                    "Awaiting Run",
                    "The mission shell is ready. Running code will update the map log and CORE state panels.",
                    List.of(
                            "CORE-01 is docked in Maintenance Room A1.",
                            "The relay cabinet remains offline one tile to the right.",
                            "Simulation playback will appear here once the learner clicks Run."
                    ),
                    List.of(
                            "This is a placeholder shell for the future execution loop.",
                            "Compile feedback, runtime feedback, and mission success summaries will appear in this panel."
                    ),
                    new CoreStatus("Offline", "Full", "Connected", "B1", "Standby"),
                    false
            );
        }

        static MissionRunResult placeholder(final String code) {
            final List<String> executableLines = code.lines()
                    .map(String::trim)
                    .filter(line -> !line.isBlank())
                    .toList();

            return new MissionRunResult(
                    "Placeholder Run Complete",
                    "The application shell accepted the submission and refreshed the mission panels. Learner code execution is not implemented yet.",
                    List.of(
                            "Received " + executableLines.size() + " non-empty code line(s).",
                            isConnected(executableLines)
                                    ? "Placeholder status updated to show CORE-01 online after connection."
                                    : "CORE-01 remains offline until a connection command is issued.",
                            "Future versions will play back robot actions here in sequence."
                    ),
                    List.of(
                            executableLines.isEmpty()
                                    ? "No learner commands were entered in this run."
                                    : "Code was submitted successfully to the placeholder run endpoint.",
                            "This response confirms the browser flow for Run and panel updates.",
                            "Mission success evaluation will be added once code execution and simulation exist."
                    ),
                    statusFor(executableLines),
                    false
            );
        }

        private static CoreStatus statusFor(final List<String> executableLines) {
            if (isConnected(executableLines)) {
                return new CoreStatus("Online", "Full", "Released", "B1", "Awaiting movement commands");
            }

            return new CoreStatus("Offline", "Full", "Connected", "B1", "Standby");
        }

        private static boolean isConnected(final List<String> executableLines) {
            return executableLines.stream().anyMatch(line -> line.contains("CORE.connect()"));
        }
    }
}
