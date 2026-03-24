package game.codecolony.web;

import game.codecolony.mission.WakeTheCoreMissionService;
import game.codecolony.mission.WakeTheCoreMissionService.MissionPage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MissionController {

    private static final String MISSION_PATH = "/missions/wake-the-core";
    private static final String MISSION_VIEW = "mission";
    private static final String RESULT_FRAGMENT = "fragments/mission-panels :: resultPanels";

    private final WakeTheCoreMissionService missionService;

    public MissionController(final WakeTheCoreMissionService missionService) {
        this.missionService = missionService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:" + MISSION_PATH;
    }

    @GetMapping(MISSION_PATH)
    public String mission(final Model model) {
        populateModel(model, missionService.initialPage());
        return MISSION_VIEW;
    }

    @PostMapping(MISSION_PATH + "/run")
    public String runMission(@RequestParam(defaultValue = "") final String code,
                             @RequestHeader(value = "HX-Request", required = false) final String htmxRequest,
                             final Model model) {
        populateModel(model, missionService.pageForCode(code));
        return isHtmxRequest(htmxRequest) ? RESULT_FRAGMENT : MISSION_VIEW;
    }

    private void populateModel(final Model model, final MissionPage missionPage) {
        model.addAttribute("missionTitle", missionPage.missionTitle());
        model.addAttribute("missionSummary", missionPage.missionSummary());
        model.addAttribute("missionObjective", missionPage.missionObjective());
        model.addAttribute("missionHints", missionPage.missionHints());
        model.addAttribute("availableCommands", missionPage.availableCommands());
        model.addAttribute("gridTiles", missionPage.gridTiles());
        model.addAttribute("code", missionPage.code());
        model.addAttribute("runResult", missionPage.runResult());
    }

    private boolean isHtmxRequest(final String htmxRequest) {
        return "true".equalsIgnoreCase(htmxRequest);
    }
}
