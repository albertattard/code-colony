package game.codecolony.mission;

import game.codecolony.mission.mission01.WakeTheCoreMissionService;
import game.codecolony.mission.mission02.ChargeTheCoreMissionService;
import game.codecolony.mission.mission03.RepairTheCoreMissionService;

import org.springframework.stereotype.Service;

@Service
public final class MissionPageFacade {

    private final WakeTheCoreMissionService wakeTheCoreMissionService;
    private final ChargeTheCoreMissionService chargeTheCoreMissionService;
    private final RepairTheCoreMissionService repairTheCoreMissionService;

    public MissionPageFacade(final WakeTheCoreMissionService wakeTheCoreMissionService,
                             final ChargeTheCoreMissionService chargeTheCoreMissionService,
                             final RepairTheCoreMissionService repairTheCoreMissionService) {
        this.wakeTheCoreMissionService = wakeTheCoreMissionService;
        this.chargeTheCoreMissionService = chargeTheCoreMissionService;
        this.repairTheCoreMissionService = repairTheCoreMissionService;
    }

    public String defaultCodeForMission(final String missionId) {
        return switch (missionId) {
            case "mission-01" -> "";
            case "mission-02" -> "Core.connect();";
            case "mission-03" -> "var core = Core.connect();";
            default -> throw new IllegalStateException("Unsupported mission content id: " + missionId);
        };
    }

    public MissionPage initialPageForMission(final String missionId, final String startCode) {
        return switch (missionId) {
            case "mission-01" -> wakeTheCoreMissionService.initialPage();
            case "mission-02" -> chargeTheCoreMissionService.initialPage(startCode);
            case "mission-03" -> repairTheCoreMissionService.initialPage(startCode);
            default -> throw new IllegalStateException("Unsupported mission content id: " + missionId);
        };
    }

    public MissionPage pageForCode(final String missionId, final String code, final String startCode) {
        return switch (missionId) {
            case "mission-01" -> wakeTheCoreMissionService.pageForCode(code);
            case "mission-02" -> chargeTheCoreMissionService.pageForCode(code, startCode);
            case "mission-03" -> repairTheCoreMissionService.pageForCode(code, startCode);
            default -> throw new IllegalStateException("Unsupported mission content id: " + missionId);
        };
    }
}
