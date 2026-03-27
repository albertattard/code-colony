package game.codecolony.mission.mission01;

import game.codecolony.mission.MissionWorkerRunner;

public final class WakeTheCoreMissionWorker {

    private WakeTheCoreMissionWorker() {
    }

    public static void main(final String[] args) throws Exception {
        final WakeTheCoreMissionSimulator simulator = new WakeTheCoreMissionSimulator();
        final WakeTheCoreMissionValidator validator = new WakeTheCoreMissionValidator();

        MissionWorkerRunner.run(args, simulator, simulator::finish, validator::validate);
    }
}
