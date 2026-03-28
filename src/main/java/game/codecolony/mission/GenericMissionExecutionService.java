package game.codecolony.mission;

import org.springframework.stereotype.Service;

@Service
public final class GenericMissionExecutionService {

    private static final MissionExecutionRunner RUNNER = new MissionExecutionRunner();

    public MissionRunResult execute(final String code, final MissionExecutionConfig config) {
        return RUNNER.execute(code, config);
    }
}
