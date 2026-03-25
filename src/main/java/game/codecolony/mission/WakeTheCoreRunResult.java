package game.codecolony.mission;

import java.util.List;

public record WakeTheCoreRunResult(String headline, String summary, List<String> simulationEvents,
                                   List<String> feedbackItems, WakeTheCoreCoreStatus coreStatus, boolean success) {

    public static WakeTheCoreRunResult initial() {
        return new WakeTheCoreRunResult(
                "Awaiting Run",
                "Enter CORE.connect(); and click Run to bring CORE-01 online.",
                List.of(
                        "CORE-01 is docked in Maintenance Room A1.",
                        "The control link is offline.",
                        "Running code will update the CORE status and feedback panels."
                ),
                List.of(
                        "Mission 01 expects a single method call: CORE.connect();",
                        "The first successful run should bring CORE-01 online."
                ),
                new WakeTheCoreCoreStatus("Offline", "", "", "", "No telemetry available while offline."),
                false
        );
    }
}
