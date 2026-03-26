package game.codecolony.web;

import game.codecolony.session.GameSessionService;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public final class GameSessionController {

    private final GameSessionService gameSessionService;

    public GameSessionController(final GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    @PostMapping("/game-sessions")
    public String createGameSession() {
        final UUID gameSessionId = gameSessionService.createSession();
        return "redirect:/sessions/" + gameSessionId + "/missions/wake-the-core";
    }
}
