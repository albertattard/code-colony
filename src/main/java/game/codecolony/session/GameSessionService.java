package game.codecolony.session;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public final class GameSessionService {

    private static final Duration SESSION_TIMEOUT = Duration.ofHours(4);

    private final GameSessionStore gameSessionStore;

    public GameSessionService(final GameSessionStore gameSessionStore) {
        this.gameSessionStore = gameSessionStore;
    }

    public UUID createSession() {
        evictExpiredSessions();
        final Instant now = now();
        final UUID gameSessionId = UUID.randomUUID();
        gameSessionStore.save(new GameSession(gameSessionId, now));
        return gameSessionId;
    }

    public void initializeStartCodeIfMissing(final UUID gameSessionId, final String missionId, final String startCode) {
        final MissionState missionState = missionState(gameSessionId, missionId);
        missionState.initializeStartCodeIfMissing(startCode);
    }

    public String startCodeOrDefault(final UUID gameSessionId, final String missionId, final String fallbackCode) {
        final MissionState missionState = missionState(gameSessionId, missionId);
        final String startCode = missionState.startCode();
        return startCode == null ? fallbackCode : startCode;
    }

    public String currentCodeOrDefault(final UUID gameSessionId, final String missionId, final String fallbackCode) {
        final MissionState missionState = missionState(gameSessionId, missionId);
        final String currentCode = missionState.currentCode();
        return currentCode == null ? fallbackCode : currentCode;
    }

    public void updateCurrentCode(final UUID gameSessionId, final String missionId, final String code) {
        missionState(gameSessionId, missionId).setCurrentCode(code);
    }

    public void resetCurrentCodeToStartCode(final UUID gameSessionId, final String missionId, final String fallbackStartCode) {
        missionState(gameSessionId, missionId).resetCurrentCodeToStartCode(fallbackStartCode);
    }

    public void markMissionSuccessful(final UUID gameSessionId, final String missionId) {
        missionState(gameSessionId, missionId).markSuccessful();
    }

    public String currentCodeForCompletedMissionOrDefault(final UUID gameSessionId, final String missionId, final String fallbackCode) {
        final MissionState missionState = missionState(gameSessionId, missionId);
        if (!missionState.completed()) {
            return fallbackCode;
        }
        final String currentCode = missionState.currentCode();
        return currentCode == null ? fallbackCode : currentCode;
    }

    public boolean isMissionCompleted(final UUID gameSessionId, final String missionId) {
        return missionState(gameSessionId, missionId).completed();
    }

    private MissionState missionState(final UUID gameSessionId, final String missionId) {
        final GameSession gameSession = requireActiveSession(gameSessionId);
        return gameSession.missionState(missionId);
    }

    private GameSession requireActiveSession(final UUID gameSessionId) {
        evictExpiredSessions();
        final GameSession gameSession = gameSessionStore.findById(gameSessionId)
                .orElseThrow(() -> new GameSessionNotFoundException(gameSessionId));
        if (isExpired(gameSession, now())) {
            gameSessionStore.remove(gameSessionId);
            throw new GameSessionNotFoundException(gameSessionId);
        }
        gameSession.touch(now());
        return gameSession;
    }

    private void evictExpiredSessions() {
        final Instant now = now();
        for (final GameSession gameSession : gameSessionStore.findAll()) {
            if (isExpired(gameSession, now)) {
                gameSessionStore.remove(gameSession.id());
            }
        }
    }

    private boolean isExpired(final GameSession gameSession, final Instant now) {
        final Instant inactivityCutoff = now.minus(SESSION_TIMEOUT);
        return gameSession.lastAccessedAt().isBefore(inactivityCutoff);
    }

    private Instant now() {
        return Instant.now();
    }
}
