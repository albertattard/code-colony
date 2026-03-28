# Task 0033: Move Initial CORE State to Mission Maps

## Summary

Add explicit CORE online/offline state to mission map spawn data and use that value for mission initial status rendering.

## Why

Initial CORE state is currently hardcoded in mission services/configs. Moving it to map content keeps initial state configuration in one place with other mission spawn data.

## In Scope

- add `state` to core spawns in mission map YAML
- load and validate core spawn state in map loader
- use map-provided state for mission initial status in mission services/execution configs
- update map loader tests/spec schema docs

## Out Of Scope

- changing mission runtime mechanics
- changing learner-facing objectives

## Acceptance Criteria

- [x] Mission map core spawns include explicit `state` (`offline` or `online`).
- [x] Map loading fails on invalid core state values.
- [x] Mission initial status state is sourced from map spawn state.
- [x] `./mvnw clean verify` passes.
