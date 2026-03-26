# Task 0022: Implement Mission 03 Repair The CORE

## Summary

Add Mission 03 as the next progression step after Mission 02, focused on moving CORE-01 to the repair station and restoring its health.

## Why

After Mission 02 restores power, the player needs a clear in-world goal for the next mission. Repairing CORE-01 introduces movement plus a location-dependent action with visible world impact.

## In Scope

- adding Mission 03 spec and player-facing content (`briefing.md`, `explain.md`)
- wiring Mission 03 routes and progression in `MissionController`
- implementing Mission 03 execution and validation rules for movement and repair station behavior
- carrying Mission 02 successful code into Mission 03 session state
- updating tests for mission progression, mission execution, and rendered content

## Out Of Scope

- introducing new student API commands
- introducing loops or conditionals as mission requirements
- implementing Mission 04

## Relevant Specs

- `docs/spec/gameplay-spec.md`
- `docs/spec/student-api-spec.md`
- `docs/spec/missions/mission-02-charge-the-core.md`
- `docs/spec/missions/mission-03-repair-the-core.md`

## Acceptance Criteria

- [x] Mission 02 completion links to Mission 03.
- [x] Mission 03 preloads carried code from Mission 02 when available.
- [x] Mission 03 succeeds only when CORE-01 reaches B3 and repair completes at the station.
- [x] Mission 03 completed state locks code and preserves `Explain` action.
- [x] Automated tests cover Mission 03 routing and mission execution behavior.
