package game.codecolony.mission;

import game.codecolony.runtime.MissionEvent;

import java.util.List;

record WakeTheCoreMissionSimulation(boolean connected, int connectAttempts, List<MissionEvent> events) {
}
