package game.codecolony.mission.mission02;

import game.codecolony.runtime.MissionEvent;

import java.util.List;

record ChargeTheCoreMissionSimulation(boolean connected,
                                      int connectAttempts,
                                      String startPosition,
                                      int batteryLevel,
                                      int batteryCapacity,
                                      int healthLevel,
                                      int healthCapacity,
                                      List<MissionEvent> events) {
}
