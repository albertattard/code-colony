package game.codecolony.mission;

@FunctionalInterface
public interface MissionResultValidator<T> {
    MissionRunResult validate(T simulation, String runtimeError, String stdout, String stderr);
}
