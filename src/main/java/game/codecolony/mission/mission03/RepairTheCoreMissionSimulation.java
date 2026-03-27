package game.codecolony.mission.mission03;

import game.codecolony.runtime.MissionEvent;

import java.util.List;

record RepairTheCoreMissionSimulation(boolean connected,
                                      int connectAttempts,
                                      String position,
                                      int moves,
                                      int batteryLevel,
                                      int batteryCapacity,
                                      int healthLevel,
                                      int healthCapacity,
                                      boolean repaired,
                                      List<MissionEvent> events) {
}
