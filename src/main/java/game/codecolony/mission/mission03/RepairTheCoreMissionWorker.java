package game.codecolony.mission.mission03;

import game.codecolony.mission.MissionWorkerRunner;

public final class RepairTheCoreMissionWorker {

    private RepairTheCoreMissionWorker() {
    }

    public static void main(final String[] args) throws Exception {
        final RepairTheCoreMissionSimulator simulator = new RepairTheCoreMissionSimulator();
        final RepairTheCoreMissionValidator validator = new RepairTheCoreMissionValidator();

        MissionWorkerRunner.run(args, simulator, simulator::finish, validator::validate);
    }
}
