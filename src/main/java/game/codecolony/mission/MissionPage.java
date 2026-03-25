package game.codecolony.mission;

import java.util.List;

public record MissionPage(String missionTitle, String missionSummary, String missionObjective,
                          String briefingHtml, String briefingAudioPath,
                          List<String> missionHints, List<CommandReference> availableCommands,
                          List<GridTile> gridTiles, String code, String initialCode,
                          String missionPath, String resetPath, String nextMissionPath,
                          boolean lockOnSuccess, MissionRunResult runResult) {
}
