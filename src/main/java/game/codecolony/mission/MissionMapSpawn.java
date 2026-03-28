package game.codecolony.mission;

public record MissionMapSpawn(String id,
                              String type,
                              String state,
                              String at,
                              MissionMapMeter battery,
                              MissionMapMeter health) {
}
