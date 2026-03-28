package game.codecolony.web;

import game.codecolony.mission.CodeExplanation;
import game.codecolony.mission.CodeExplanationService;
import game.codecolony.mission.MissionManifestEntry;
import game.codecolony.mission.MissionNotFoundException;
import game.codecolony.mission.MissionPage;
import game.codecolony.mission.MissionPageFacade;
import game.codecolony.mission.MissionRouteCatalog;
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
    private static final String MISSION_PATH = SESSION_ROUTE_PREFIX + "/{missionName}";
    private static final String MISSION_RESET_PATH = MISSION_PATH + "/reset";
    private static final String MISSION_RUN_PATH = MISSION_PATH + "/run";
    private static final String MISSION_EXPLAIN_PATH = MISSION_PATH + "/explain";
    private static final String MISSION_VIEW = "mission";
    private static final String RESULT_FRAGMENT = "fragments/mission-panels :: resultPanels";

    private final MissionPageFacade missionPageFacade;
    private final CodeExplanationService codeExplanationService;
    private final GameSessionService gameSessionService;
    private final MissionRouteCatalog missionRouteCatalog;

    public MissionController(final MissionPageFacade missionPageFacade,
                             final CodeExplanationService codeExplanationService,
                             final GameSessionService gameSessionService,
                             final MissionRouteCatalog missionRouteCatalog) {
        this.missionPageFacade = missionPageFacade;
        this.codeExplanationService = codeExplanationService;
        this.gameSessionService = gameSessionService;
        this.missionRouteCatalog = missionRouteCatalog;
    }

    @GetMapping(MISSION_PATH)
    public String mission(@PathVariable final UUID gameSessionId,
                          @PathVariable final String missionName,
                          final Model model) {
        final MissionPage missionPage = buildMissionPageForCurrentState(gameSessionId, missionName);
        populateModel(model, scopeMissionPage(gameSessionId, missionName, missionPage), null);
        return MISSION_VIEW;
    }

    @GetMapping(MISSION_RESET_PATH)
    public String resetMission(@PathVariable final UUID gameSessionId,
                               @PathVariable final String missionName,
                               final Model model) {
        final MissionManifestEntry missionEntry = missionRouteCatalog.requireEnabledMission(missionName);
        final String missionId = missionEntry.content();
        final String resetCode = initialStartCodeForMission(gameSessionId, missionName, missionId);
        gameSessionService.resetCurrentCodeToStartCode(gameSessionId, missionId, resetCode);
        return mission(gameSessionId, missionName, model);
    }

    @PostMapping(MISSION_RUN_PATH)
    public String runMission(@PathVariable final UUID gameSessionId,
                             @PathVariable final String missionName,
                             @RequestParam(defaultValue = "") final String code,
                             @RequestHeader(value = "HX-Request", required = false) final String htmxRequest,
                             final Model model) {
        final MissionManifestEntry missionEntry = missionRouteCatalog.requireEnabledMission(missionName);
        final String missionId = missionEntry.content();

        gameSessionService.updateCurrentCode(gameSessionId, missionId, code);
        final String startCode = startCodeForMission(gameSessionId, missionName, missionId);
        final MissionPage missionPage = pageForCode(missionId, code, startCode);
        if (missionPage.runResult().success()) {
            gameSessionService.markMissionSuccessful(gameSessionId, missionId);
        }

        populateModel(model, scopeMissionPage(gameSessionId, missionName, missionPage), null);
        return isHtmxRequest(htmxRequest) ? RESULT_FRAGMENT : MISSION_VIEW;
    }

    @PostMapping(MISSION_EXPLAIN_PATH)
    public String explainMission(@PathVariable final UUID gameSessionId,
                                 @PathVariable final String missionName,
                                 @RequestParam(defaultValue = "") final String code,
                                 @RequestHeader(value = "HX-Request", required = false) final String htmxRequest,
                                 final Model model) {
        final MissionManifestEntry missionEntry = missionRouteCatalog.requireEnabledMission(missionName);
        final String missionId = missionEntry.content();

        gameSessionService.updateCurrentCode(gameSessionId, missionId, code);
        final MissionPage missionPage = buildMissionPageForCurrentState(gameSessionId, missionName);
        final CodeExplanation codeExplanation = codeExplanationService.explain(missionId);

        populateModel(model, scopeMissionPage(gameSessionId, missionName, missionPage), codeExplanation);
        return isHtmxRequest(htmxRequest) ? RESULT_FRAGMENT : MISSION_VIEW;
    }

    @ExceptionHandler(GameSessionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String sessionNotFound() {
        return "session-expired";
    }

    @ExceptionHandler(MissionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String missionNotFound() {
        return "session-expired";
    }

    private MissionPage buildMissionPageForCurrentState(final UUID gameSessionId, final String missionName) {
        final MissionManifestEntry missionEntry = missionRouteCatalog.requireEnabledMission(missionName);
        final String missionId = missionEntry.content();
        final String startCode = startCodeForMission(gameSessionId, missionName, missionId);
        final String currentCode = gameSessionService.currentCodeOrDefault(gameSessionId, missionId, startCode);

        if (gameSessionService.isMissionCompleted(gameSessionId, missionId)) {
            return pageForCode(missionId, currentCode, startCode);
        }

        return withCode(initialPageForMission(missionId, startCode), currentCode);
    }

    private String startCodeForMission(final UUID gameSessionId,
                                       final String missionName,
                                       final String missionId) {
        final String initialStartCode = initialStartCodeForMission(gameSessionId, missionName, missionId);
        gameSessionService.initializeStartCodeIfMissing(gameSessionId, missionId, initialStartCode);
        return gameSessionService.startCodeOrDefault(gameSessionId, missionId, initialStartCode);
    }

    private String initialStartCodeForMission(final UUID gameSessionId,
                                              final String missionName,
                                              final String missionId) {
        final String defaultStartCode = defaultCodeForMission(missionId);
        return missionRouteCatalog.previousEnabledMission(missionName)
                .map(previousMission -> gameSessionService.currentCodeForCompletedMissionOrDefault(
                        gameSessionId,
                        previousMission.content(),
                        defaultStartCode
                ))
                .orElse(defaultStartCode);
    }

    private String defaultCodeForMission(final String missionId) {
        return missionPageFacade.defaultCodeForMission(missionId);
    }

    private MissionPage initialPageForMission(final String missionId, final String startCode) {
        return missionPageFacade.initialPageForMission(missionId, startCode);
    }

    private MissionPage pageForCode(final String missionId, final String code, final String startCode) {
        return missionPageFacade.pageForCode(missionId, code, startCode);
    }

    private MissionPage scopeMissionPage(final UUID gameSessionId,
                                         final String missionName,
                                         final MissionPage missionPage) {
        final String missionPath = missionPath(gameSessionId, missionName);
        final String resetPath = missionPath + "/reset";
        final String nextMissionPath = missionRouteCatalog.nextEnabledMission(missionName)
                .map(nextMission -> missionPath(gameSessionId, nextMission.name()))
                .orElse("");
        return withPaths(missionPage, missionPath, resetPath, nextMissionPath);
    }

    private String missionPath(final UUID gameSessionId, final String missionName) {
        return "/sessions/" + gameSessionId + "/missions/" + missionName;
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
