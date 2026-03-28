package game.codecolony.web;

import game.codecolony.mission.MissionRouteCatalog;
import game.codecolony.session.GameSessionService;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public final class GameSessionController {

    private final GameSessionService gameSessionService;
    private final MissionRouteCatalog missionRouteCatalog;

    public GameSessionController(final GameSessionService gameSessionService,
                                 final MissionRouteCatalog missionRouteCatalog) {
        this.gameSessionService = gameSessionService;
        this.missionRouteCatalog = missionRouteCatalog;
    }

    @PostMapping("/game-sessions")
    public String createGameSession() {
        final UUID gameSessionId = gameSessionService.createSession();
        final String firstMissionName = missionRouteCatalog.firstEnabledMission().name();
        return "redirect:/sessions/" + gameSessionId + "/missions/" + firstMissionName;
    }
}
