package game.codecolony.mission;

import java.util.List;

public record MissionRunResult(String headline, String summary, List<String> simulationEvents,
                               List<String> feedbackItems, MissionCoreStatus coreStatus,
                               String stdout, String stderr, boolean success) {
}
