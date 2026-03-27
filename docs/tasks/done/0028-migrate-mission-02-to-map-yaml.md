# Task 0028: Migrate Mission 02 to Mission Map YAML

## Summary

Migrate Mission 02 from hardcoded grid/state constants to mission-scoped map data loaded from `map.yaml`.

## Why

Mission 01 and Mission 03 already use map YAML for layout and CORE spawn state. Mission 02 should follow the same approach so map and initial telemetry changes can be made in content files instead of Java code.

## In Scope

- adding `src/main/resources/content/missions/mission-02/map.yaml`
- wiring Mission 02 grid rendering to map loader/adapter
- deriving Mission 02 initial CORE position and meter values from map spawn data
- removing hardcoded Mission 02 runtime constants for position and meter capacities/levels
- updating tests for Mission 02 map-driven behavior

## Out Of Scope

- removing legacy map loader classes in this task
- changing learner-facing Mission 02 objective, hints, or command surface
- introducing map inheritance/reuse between missions

## Relevant Specs

- `docs/spec/gameplay-spec.md`
- `docs/spec/missions/mission-02-charge-the-core.md`
- `docs/spec/student-api-spec.md`

## Acceptance Criteria

- [x] `src/main/resources/content/missions/mission-02/map.yaml` exists and follows map schema.
- [x] Mission 02 grid rendering derives from the Mission 02 map file.
- [x] Mission 02 connected status (position/battery/health) derives from map spawn data.
- [x] Existing Mission 02 learner-visible behavior remains unchanged.
- [x] Automated tests cover Mission 02 map-driven behavior.
- [x] `./mvnw clean verify` passes.
