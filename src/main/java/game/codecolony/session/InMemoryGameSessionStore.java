package game.codecolony.session;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

@Component
final class InMemoryGameSessionStore implements GameSessionStore {

    private final ConcurrentMap<UUID, GameSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void save(final GameSession gameSession) {
        sessions.put(gameSession.id(), gameSession);
    }

    @Override
    public Optional<GameSession> findById(final UUID gameSessionId) {
        return Optional.ofNullable(sessions.get(gameSessionId));
    }

    @Override
    public void remove(final UUID gameSessionId) {
        sessions.remove(gameSessionId);
    }

    @Override
    public Collection<GameSession> findAll() {
        return sessions.values();
    }
}
