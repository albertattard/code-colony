package game.codecolony.session;

import java.util.UUID;

public final class GameSessionNotFoundException extends RuntimeException {

    public GameSessionNotFoundException(final UUID gameSessionId) {
        super("Game session not found: " + gameSessionId);
    }
}
