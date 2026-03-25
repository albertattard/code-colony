package game.codecolony.mission;

public record WakeTheCoreCoreStatus(String unitName, String state, Integer batteryLevel, Integer batteryCapacity,
                                    Integer healthLevel, Integer healthCapacity,
                                    String dock, String position, String note) {
}
