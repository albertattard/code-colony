package game.codecolony.mission;

import game.codecolony.runtime.MissionEvent;

import java.util.List;

record GenericMissionSimulation(boolean connected,
                                int connectAttempts,
                                String startPosition,
                                String position,
                                int moves,
                                int batteryLevel,
                                int batteryCapacity,
                                int healthLevel,
                                int healthCapacity,
                                boolean repaired,
                                List<MissionEvent> events) {
}
