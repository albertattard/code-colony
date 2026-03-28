package game.codecolony.mission;

public final class MissionNotFoundException extends RuntimeException {

    public MissionNotFoundException(final String missionName) {
        super("Mission not found in enabled manifest set: " + missionName);
    }
}
