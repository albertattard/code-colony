package game.codecolony.mission.mission01;

import game.codecolony.runtime.MissionEvent;

import java.util.List;

record WakeTheCoreMissionSimulation(boolean connected,
                                    int connectAttempts,
                                    String startPosition,
                                    int batteryLevel,
                                    int batteryCapacity,
                                    int healthLevel,
                                    int healthCapacity,
                                    List<MissionEvent> events) {
}
