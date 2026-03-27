package game.codecolony.mission.mission01;

import game.codecolony.mission.*;

import game.codecolony.runtime.MissionEvent;

import java.util.List;

record WakeTheCoreMissionSimulation(boolean connected, int connectAttempts, List<MissionEvent> events) {
}
