# Task 0027: Migrate Mission 01 to Mission Map YAML

## Summary

Migrate Mission 01 from hardcoded grid and status constants to mission-scoped map data loaded from `map.yaml`.

## Why

Mission 03 already reads map layout and CORE spawn values from YAML. Mission 01 should follow the same pattern so contributors can tune map and initial CORE state without Java code changes.

## In Scope

- adding `src/main/resources/content/missions/mission-01/map.yaml`
- wiring Mission 01 service grid rendering to map loader/adapter
- deriving Mission 01 CORE spawn position and meter values from map data
- removing hardcoded Mission 01 runtime constants for position/battery/health
- updating tests to cover map-driven Mission 01 behavior

## Out Of Scope

- migrating Mission 02 in this task
- changing learner-facing Mission 01 objective or command surface
- introducing map inheritance/reuse between missions

## Relevant Specs

- `docs/spec/gameplay-spec.md`
- `docs/spec/missions/mission-01-wake-the-core.md`
- `docs/spec/student-api-spec.md`

## Acceptance Criteria

- [x] `src/main/resources/content/missions/mission-01/map.yaml` exists and follows map schema.
- [x] Mission 01 grid rendering derives from the Mission 01 map file.
- [x] Mission 01 connected status (position/battery/health) derives from map spawn data.
- [x] Existing Mission 01 learner-visible behavior remains unchanged.
- [x] Automated tests cover Mission 01 map-driven behavior.
- [x] `./mvnw clean verify` passes.
