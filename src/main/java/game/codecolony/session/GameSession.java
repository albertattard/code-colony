package game.codecolony.session;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.util.UUID;

final class GameSession {

    private final UUID id;
    private final Instant createdAt;
    private final Map<String, MissionState> missionStates;
    private Instant lastAccessedAt;

    GameSession(final UUID id, final Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.lastAccessedAt = createdAt;
        missionStates = new ConcurrentHashMap<>();
    }

    UUID id() {
        return id;
    }

    Instant createdAt() {
        return createdAt;
    }

    synchronized Instant lastAccessedAt() {
        return lastAccessedAt;
    }

    synchronized void touch(final Instant accessTime) {
        lastAccessedAt = accessTime;
    }

    MissionState missionState(final String missionId) {
        return missionStates.computeIfAbsent(missionId, key -> new MissionState());
    }
}
