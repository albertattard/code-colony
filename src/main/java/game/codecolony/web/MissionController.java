package game.codecolony.web;

import game.codecolony.mission.ChargeTheCoreMissionService;
import game.codecolony.mission.CodeExplanation;
import game.codecolony.mission.CodeExplanationService;
import game.codecolony.mission.MissionPage;
import game.codecolony.mission.WakeTheCoreMissionService;
import game.codecolony.session.GameSessionNotFoundException;
import game.codecolony.session.GameSessionService;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class MissionController {

    private static final String SESSION_ROUTE_PREFIX = "/sessions/{gameSessionId}/missions";
    private static final String MISSION_ONE_PATH = SESSION_ROUTE_PREFIX + "/wake-the-core";
    private static final String MISSION_TWO_PATH = SESSION_ROUTE_PREFIX + "/charge-the-core";
    private static final String MISSION_ONE_RESET_PATH = MISSION_ONE_PATH + "/reset";
    private static final String MISSION_TWO_RESET_PATH = MISSION_TWO_PATH + "/reset";
    private static final String MISSION_ONE_ID = "mission-01";
    private static final String MISSION_TWO_ID = "mission-02";
    private static final String MISSION_TWO_DEFAULT_CODE = "Core.connect();";
    private static final String MISSION_VIEW = "mission";
    private static final String RESULT_FRAGMENT = "fragments/mission-panels :: resultPanels";

    private final WakeTheCoreMissionService wakeTheCoreMissionService;
    private final ChargeTheCoreMissionService chargeTheCoreMissionService;
    private final CodeExplanationService codeExplanationService;
    private final GameSessionService gameSessionService;

    public MissionController(final WakeTheCoreMissionService wakeTheCoreMissionService,
                             final ChargeTheCoreMissionService chargeTheCoreMissionService,
                             final CodeExplanationService codeExplanationService,
                             final GameSessionService gameSessionService) {
        this.wakeTheCoreMissionService = wakeTheCoreMissionService;
        this.chargeTheCoreMissionService = chargeTheCoreMissionService;
        this.codeExplanationService = codeExplanationService;
        this.gameSessionService = gameSessionService;
    }

    @GetMapping(MISSION_ONE_PATH)
    public String mission(@PathVariable final UUID gameSessionId, final Model model) {
        final MissionPage missionPage = buildMissionOnePageForCurrentState(gameSessionId);
        populateModel(model, scopeMissionOnePage(gameSessionId, missionPage), null);
        return MISSION_VIEW;
    }

    @GetMapping(MISSION_ONE_RESET_PATH)
    public String resetMissionOne(@PathVariable final UUID gameSessionId, final Model model) {
        gameSessionService.resetCurrentCodeToStartCode(gameSessionId, MISSION_ONE_ID, "");
        return mission(gameSessionId, model);
    }

    @GetMapping(MISSION_TWO_PATH)
    public String nextMission(@PathVariable final UUID gameSessionId, final Model model) {
        final MissionPage missionPage = buildMissionTwoPageForCurrentState(gameSessionId);
        populateModel(model, scopeMissionTwoPage(gameSessionId, missionPage), null);
        return MISSION_VIEW;
    }

    @GetMapping(MISSION_TWO_RESET_PATH)
    public String resetMissionTwo(@PathVariable final UUID gameSessionId, final Model model) {
        final String resetCode = gameSessionService.currentCodeForCompletedMissionOrDefault(
                gameSessionId,
                MISSION_ONE_ID,
                MISSION_TWO_DEFAULT_CODE
        );
        gameSessionService.resetCurrentCodeToStartCode(gameSessionId, MISSION_TWO_ID, resetCode);
        return nextMission(gameSessionId, model);
    }

    @PostMapping(MISSION_ONE_PATH + "/run")
    public String runMission(@PathVariable final UUID gameSessionId,
                             @RequestParam(defaultValue = "") final String code,
                             @RequestHeader(value = "HX-Request", required = false) final String htmxRequest,
                             final Model model) {
        gameSessionService.updateCurrentCode(gameSessionId, MISSION_ONE_ID, code);
        final MissionPage missionPage = wakeTheCoreMissionService.pageForCode(code);
        if (missionPage.runResult().success()) {
            gameSessionService.markMissionSuccessful(gameSessionId, MISSION_ONE_ID);
        }
        populateModel(model, scopeMissionOnePage(gameSessionId, missionPage), null);
        return isHtmxRequest(htmxRequest) ? RESULT_FRAGMENT : MISSION_VIEW;
    }

    @PostMapping(MISSION_TWO_PATH + "/run")
    public String runNextMission(@PathVariable final UUID gameSessionId,
                                 @RequestParam(defaultValue = "") final String code,
                                 @RequestHeader(value = "HX-Request", required = false) final String htmxRequest,
                                 final Model model) {
        gameSessionService.updateCurrentCode(gameSessionId, MISSION_TWO_ID, code);
        final String fallbackStartCode = gameSessionService.currentCodeForCompletedMissionOrDefault(
                gameSessionId,
                MISSION_ONE_ID,
                MISSION_TWO_DEFAULT_CODE
        );
        final String initialCode = gameSessionService.startCodeOrDefault(gameSessionId, MISSION_TWO_ID, fallbackStartCode);
        final MissionPage missionPage = chargeTheCoreMissionService.pageForCode(code, initialCode);
        if (missionPage.runResult().success()) {
            gameSessionService.markMissionSuccessful(gameSessionId, MISSION_TWO_ID);
        }
        populateModel(model, scopeMissionTwoPage(gameSessionId, missionPage), null);
        return isHtmxRequest(htmxRequest) ? RESULT_FRAGMENT : MISSION_VIEW;
    }

    @PostMapping(MISSION_ONE_PATH + "/explain")
    public String explainMission(@PathVariable final UUID gameSessionId,
                                 @RequestParam(defaultValue = "") final String code,
                                 @RequestHeader(value = "HX-Request", required = false) final String htmxRequest,
                                 final Model model) {
        gameSessionService.updateCurrentCode(gameSessionId, MISSION_ONE_ID, code);
        final MissionPage missionPage = buildMissionOnePageForCurrentState(gameSessionId);
        final CodeExplanation codeExplanation = codeExplanationService.explain(MISSION_ONE_ID, code);
        populateModel(model, scopeMissionOnePage(gameSessionId, missionPage), codeExplanation);
        return isHtmxRequest(htmxRequest) ? RESULT_FRAGMENT : MISSION_VIEW;
    }

    @PostMapping(MISSION_TWO_PATH + "/explain")
    public String explainNextMission(@PathVariable final UUID gameSessionId,
                                     @RequestParam(defaultValue = "") final String code,
                                     @RequestHeader(value = "HX-Request", required = false) final String htmxRequest,
                                     final Model model) {
        gameSessionService.updateCurrentCode(gameSessionId, MISSION_TWO_ID, code);
        final MissionPage missionPage = buildMissionTwoPageForCurrentState(gameSessionId);
        final CodeExplanation codeExplanation = codeExplanationService.explain(MISSION_TWO_ID, code);
        populateModel(model, scopeMissionTwoPage(gameSessionId, missionPage), codeExplanation);
        return isHtmxRequest(htmxRequest) ? RESULT_FRAGMENT : MISSION_VIEW;
    }

    @ExceptionHandler(GameSessionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String sessionNotFound() {
        return "session-expired";
    }

    private MissionPage buildMissionOnePageForCurrentState(final UUID gameSessionId) {
        gameSessionService.initializeStartCodeIfMissing(gameSessionId, MISSION_ONE_ID, "");
        final String currentCode = gameSessionService.currentCodeOrDefault(gameSessionId, MISSION_ONE_ID, "");
        return gameSessionService.isMissionCompleted(gameSessionId, MISSION_ONE_ID)
                ? wakeTheCoreMissionService.pageForCode(currentCode)
                : withCode(wakeTheCoreMissionService.initialPage(), currentCode);
    }

    private MissionPage buildMissionTwoPageForCurrentState(final UUID gameSessionId) {
        final String carriedCode = gameSessionService.currentCodeForCompletedMissionOrDefault(
                gameSessionId,
                MISSION_ONE_ID,
                MISSION_TWO_DEFAULT_CODE
        );
        gameSessionService.initializeStartCodeIfMissing(gameSessionId, MISSION_TWO_ID, carriedCode);
        final String startCode = gameSessionService.startCodeOrDefault(gameSessionId, MISSION_TWO_ID, carriedCode);
        final String currentCode = gameSessionService.currentCodeOrDefault(gameSessionId, MISSION_TWO_ID, startCode);
        return gameSessionService.isMissionCompleted(gameSessionId, MISSION_TWO_ID)
                ? chargeTheCoreMissionService.pageForCode(currentCode, startCode)
                : withCode(chargeTheCoreMissionService.initialPage(startCode), currentCode);
    }

    private MissionPage scopeMissionOnePage(final UUID gameSessionId, final MissionPage missionPage) {
        return withPaths(
                missionPage,
                missionOnePath(gameSessionId),
                missionOneResetPath(gameSessionId),
                missionTwoPath(gameSessionId)
        );
    }

    private MissionPage scopeMissionTwoPage(final UUID gameSessionId, final MissionPage missionPage) {
        return withPaths(
                missionPage,
                missionTwoPath(gameSessionId),
                missionTwoResetPath(gameSessionId),
                ""
        );
    }

    private String missionOnePath(final UUID gameSessionId) {
        return "/sessions/" + gameSessionId + "/missions/wake-the-core";
    }

    private String missionOneResetPath(final UUID gameSessionId) {
        return missionOnePath(gameSessionId) + "/reset";
    }

    private String missionTwoPath(final UUID gameSessionId) {
        return "/sessions/" + gameSessionId + "/missions/charge-the-core";
    }

    private String missionTwoResetPath(final UUID gameSessionId) {
        return missionTwoPath(gameSessionId) + "/reset";
    }

    private MissionPage withCode(final MissionPage missionPage, final String code) {
        return new MissionPage(
                missionPage.missionTitle(),
                missionPage.missionSummary(),
                missionPage.missionObjective(),
                missionPage.briefingHtml(),
                missionPage.briefingAudioPath(),
                missionPage.missionHints(),
                missionPage.availableCommands(),
                missionPage.gridTiles(),
                code,
                missionPage.initialCode(),
                missionPage.missionPath(),
                missionPage.resetPath(),
                missionPage.nextMissionPath(),
                missionPage.lockOnSuccess(),
                missionPage.runResult()
        );
    }

    private MissionPage withPaths(final MissionPage missionPage,
                                  final String missionPath,
                                  final String resetPath,
                                  final String nextMissionPath) {
        return new MissionPage(
                missionPage.missionTitle(),
                missionPage.missionSummary(),
                missionPage.missionObjective(),
                missionPage.briefingHtml(),
                missionPage.briefingAudioPath(),
                missionPage.missionHints(),
                missionPage.availableCommands(),
                missionPage.gridTiles(),
                missionPage.code(),
                missionPage.initialCode(),
                missionPath,
                resetPath,
                nextMissionPath,
                missionPage.lockOnSuccess(),
                missionPage.runResult()
        );
    }

    private void populateModel(final Model model,
                               final MissionPage missionPage,
                               final CodeExplanation codeExplanation) {
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
        model.addAttribute("codeExplanation", codeExplanation);
    }

    private boolean isHtmxRequest(final String htmxRequest) {
        return "true".equalsIgnoreCase(htmxRequest);
    }
}
