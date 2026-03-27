# Task 0026: Introduce Mission Map YAML Loader

## Summary

Introduce mission-scoped YAML map files and loader/adapter support, then migrate Mission 03 to map-driven initialization and station rules.

## Why

Mission layout and station coordinates are currently hardcoded in Java. Moving this data to mission map files allows contributors to edit maps without code changes and aligns implementation with updated gameplay specs.

## In Scope

- adding `map.yaml` support under `src/main/resources/content/missions/mission-##/`
- implementing map schema models and YAML parsing
- validating map structure, symbols, coordinates, and spawn status values
- adding `mission-03/map.yaml` with behavior-equivalent values
- introducing an adapter from parsed map data to current mission runtime structures
- migrating Mission 03 to derive station behavior (`dock`/`repair`) and initial CORE state from map data
- adding tests for parser/validation and Mission 03 compatibility

## Out Of Scope

- migrating Mission 01 and Mission 02 in this task
- introducing map inheritance/reuse between missions
- changing learner-facing mission objectives or APIs

## Relevant Specs

- `docs/spec/gameplay-spec.md`
- `docs/spec/missions/mission-03-repair-the-core.md`
- `docs/spec/student-api-spec.md`

## Acceptance Criteria

- [x] `src/main/resources/content/missions/mission-03/map.yaml` exists and follows spec schema.
- [x] Map loader parses mission map YAML and validates required fields.
- [x] Invalid maps fail fast with contributor-readable diagnostics.
- [x] Mission 03 no longer hardcodes station coordinates for charge/repair logic.
- [x] Mission 03 initial CORE position, battery, and health come from map spawn data.
- [x] Existing Mission 03 behavior remains unchanged for learners.
- [x] Automated tests cover parsing, validation, and Mission 03 map-driven behavior.
- [x] `./mvnw clean verify` passes.
