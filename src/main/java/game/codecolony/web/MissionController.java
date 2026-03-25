package game.codecolony.web;

import game.codecolony.mission.ChargeTheCoreMissionService;
import game.codecolony.mission.MissionPage;
import game.codecolony.mission.WakeTheCoreMissionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MissionController {

    private static final String MISSION_PATH = "/missions/wake-the-core";
    private static final String NEXT_MISSION_PATH = "/missions/charge-the-core";
    private static final String MISSION_VIEW = "mission";
    private static final String RESULT_FRAGMENT = "fragments/mission-panels :: resultPanels";

    private final WakeTheCoreMissionService wakeTheCoreMissionService;
    private final ChargeTheCoreMissionService chargeTheCoreMissionService;

    public MissionController(final WakeTheCoreMissionService wakeTheCoreMissionService,
                             final ChargeTheCoreMissionService chargeTheCoreMissionService) {
        this.wakeTheCoreMissionService = wakeTheCoreMissionService;
        this.chargeTheCoreMissionService = chargeTheCoreMissionService;
    }

    @GetMapping(MISSION_PATH)
    public String mission(final Model model) {
        populateModel(model, wakeTheCoreMissionService.initialPage());
        return MISSION_VIEW;
    }

    @GetMapping(NEXT_MISSION_PATH)
    public String nextMission(@RequestParam(defaultValue = "") final String code, final Model model) {
        populateModel(model, chargeTheCoreMissionService.initialPage(code));
        return MISSION_VIEW;
    }

    @PostMapping(MISSION_PATH + "/run")
    public String runMission(@RequestParam(defaultValue = "") final String code,
                             @RequestHeader(value = "HX-Request", required = false) final String htmxRequest,
                             final Model model) {
        populateModel(model, wakeTheCoreMissionService.pageForCode(code));
        return isHtmxRequest(htmxRequest) ? RESULT_FRAGMENT : MISSION_VIEW;
    }

    @PostMapping(NEXT_MISSION_PATH + "/run")
    public String runNextMission(@RequestParam(defaultValue = "") final String code,
                                 @RequestParam(defaultValue = "") final String initialCode,
                                 @RequestHeader(value = "HX-Request", required = false) final String htmxRequest,
                                 final Model model) {
        populateModel(model, chargeTheCoreMissionService.pageForCode(code, initialCode));
        return isHtmxRequest(htmxRequest) ? RESULT_FRAGMENT : MISSION_VIEW;
    }

    private void populateModel(final Model model, final MissionPage missionPage) {
        model.addAttribute("missionTitle", missionPage.missionTitle());
        model.addAttribute("missionSummary", missionPage.missionSummary());
        model.addAttribute("missionObjective", missionPage.missionObjective());
        model.addAttribute("briefingHtml", missionPage.briefingHtml());
        model.addAttribute("briefingAudioPath", missionPage.briefingAudioPath());
        model.addAttribute("missionHints", missionPage.missionHints());
        model.addAttribute("availableCommands", missionPage.availableCommands());
        model.addAttribute("gridTiles", missionPage.gridTiles());
        model.addAttribute("code", missionPage.code());
        model.addAttribute("initialCode", missionPage.initialCode());
        model.addAttribute("missionPath", missionPage.missionPath());
        model.addAttribute("resetPath", missionPage.resetPath());
        model.addAttribute("nextMissionPath", missionPage.nextMissionPath());
        model.addAttribute("lockOnSuccess", missionPage.lockOnSuccess());
        model.addAttribute("runResult", missionPage.runResult());
    }

    private boolean isHtmxRequest(final String htmxRequest) {
        return "true".equalsIgnoreCase(htmxRequest);
    }
}
