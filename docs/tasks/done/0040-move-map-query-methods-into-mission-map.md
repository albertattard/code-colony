# Task 0040: Move Map Query Methods Into MissionMap

## Summary

Move map-query operations such as `requireCoreSpawn(...)` and `requireFirstCoordinateByType(...)` from `MissionMapAdapter` into `MissionMap` so query logic lives with map data.

## Why

`MissionMapAdapter` currently mixes data-to-view adaptation with map-domain lookups. Placing queries on `MissionMap` improves API discoverability and keeps adapter responsibilities focused on transformation.

## In Scope

- add query methods to `MissionMap`
- migrate existing mission services/execution services to call `MissionMap` methods
- keep `MissionMapAdapter` for grid-tile conversion only
- update tests impacted by API move

## Out Of Scope

- changing map file format
- changing runtime mission behavior

## Acceptance Criteria

- [x] Map query call sites use `MissionMap` methods instead of `MissionMapAdapter`.
- [x] `MissionMapAdapter` no longer contains domain query methods.
- [x] Behavior remains unchanged.
- [x] `./mvnw clean verify` passes.
