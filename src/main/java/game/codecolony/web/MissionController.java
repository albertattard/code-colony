package game.codecolony.web;

import game.codecolony.mission.WakeTheCoreMissionService;
import game.codecolony.mission.WakeTheCoreMissionService.MissionPage;
import game.codecolony.content.NarrativeContentService;
import game.codecolony.content.NarrativeContentService.MissionNarrativeContent;
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
    private static final String NEXT_MISSION_VIEW = "mission-handoff";
    private static final String RESULT_FRAGMENT = "fragments/mission-panels :: resultPanels";

    private final WakeTheCoreMissionService missionService;
    private final NarrativeContentService narrativeContentService;

    public MissionController(final WakeTheCoreMissionService missionService,
                             final NarrativeContentService narrativeContentService) {
        this.missionService = missionService;
        this.narrativeContentService = narrativeContentService;
    }

    @GetMapping(MISSION_PATH)
    public String mission(final Model model) {
        populateModel(model, missionService.initialPage());
        return MISSION_VIEW;
    }

    @GetMapping(NEXT_MISSION_PATH)
    public String nextMission(final Model model) {
        final MissionNarrativeContent missionNarrative = narrativeContentService.loadMissionNarrative("mission-02");
        model.addAttribute("missionTitle", missionNarrative.title());
        model.addAttribute("missionSummary", missionNarrative.summary());
        model.addAttribute("missionObjective", missionNarrative.objective());
        model.addAttribute("briefingHtml", missionNarrative.briefingHtml());
        return NEXT_MISSION_VIEW;
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
        model.addAttribute("briefingHtml", missionPage.briefingHtml());
        model.addAttribute("briefingAudioPath", missionPage.briefingAudioPath());
        model.addAttribute("missionHints", missionPage.missionHints());
        model.addAttribute("availableCommands", missionPage.availableCommands());
        model.addAttribute("gridTiles", missionPage.gridTiles());
        model.addAttribute("code", missionPage.code());
        model.addAttribute("nextMissionPath", missionPage.nextMissionPath());
        model.addAttribute("runResult", missionPage.runResult());
    }

    private boolean isHtmxRequest(final String htmxRequest) {
        return "true".equalsIgnoreCase(htmxRequest);
    }
}
