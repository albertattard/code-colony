package game.codecolony.mission.mission02;

import game.codecolony.mission.MissionWorkerRunner;

public final class ChargeTheCoreMissionWorker {

    private ChargeTheCoreMissionWorker() {
    }

    public static void main(final String[] args) throws Exception {
        final ChargeTheCoreMissionSimulator simulator = new ChargeTheCoreMissionSimulator();
        final ChargeTheCoreMissionValidator validator = new ChargeTheCoreMissionValidator();

        MissionWorkerRunner.run(args, simulator, simulator::finish, validator::validate);
    }
}
