# Task 0019: Introduce Game Session State

## Summary

Introduce a server-managed game session model keyed by a UUID path segment so mission continuity no longer depends on query-parameter code handoff.

## Why

Code continuity between missions is now meaningful product behavior. The game needs a first-class session concept that can carry mission state in memory without depending on `HttpSession` or login.

## In Scope

- updating gameplay spec to define game-session-backed mission state
- creating an in-memory `GameSession` model with mission state keyed by mission ID
- creating a `POST /game-sessions` start flow that redirects into session-scoped mission routes
- moving Mission 01 to Mission 02 code continuity to server-side session state
- updating reset and run routes to stay scoped to a single `gameSessionId`
- handling unknown or expired session IDs with a player-facing response
- updating automated tests for the new routing and continuity behavior

## Out Of Scope

- cross-session persistence in a database
- account or login integration
- multi-user ownership rules for sessions

## Relevant Specs

- `docs/spec/gameplay-spec.md`
- `docs/spec/missions/mission-01-wake-the-core.md`
- `docs/spec/missions/mission-02-charge-the-core.md`

## Acceptance Criteria

- [x] Clicking `Start` creates a new game session and routes into Mission 01 using a session-scoped URL.
- [x] Mission routes and run/reset actions remain within the same game session ID.
- [x] Session state stores mission data as a map keyed by mission ID.
- [x] Mission 02 preloads from Mission 01 code after Mission 01 completion via server-side session state.
- [x] Unknown session IDs show a player-facing expired-session response.
- [x] Automated tests cover session-scoped progression and continuity.
