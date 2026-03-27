package game.codecolony.mission.mission02;

import game.codecolony.mission.*;

import game.codecolony.runtime.MissionEvent;

import java.util.List;

record ChargeTheCoreMissionSimulation(boolean connected, int connectAttempts, int batteryLevel, List<MissionEvent> events) {
}
