package game.codecolony.session;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

interface GameSessionStore {

    void save(GameSession gameSession);

    Optional<GameSession> findById(UUID gameSessionId);

    void remove(UUID gameSessionId);

    Collection<GameSession> findAll();
}
