package game.codecolony.mission;

public record MissionStatusMeter(int level, int capacity) {

    public MissionStatusMeter {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be greater than zero");
        }
        if (level < 0 || level > capacity) {
            throw new IllegalArgumentException("level must be between 0 and capacity");
        }
    }

    public static MissionStatusMeter ofNullable(final Integer level, final Integer capacity) {
        if (level == null && capacity == null) {
            return null;
        }
        if (level == null || capacity == null) {
            throw new IllegalArgumentException("level and capacity must both be provided");
        }
        return new MissionStatusMeter(level, capacity);
    }
}
