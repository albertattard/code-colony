# Task 0057: Extract Mission Execution Config Factory (Stage 3C)

## Summary

Extract shared mission execution config assembly into a reusable factory and migrate mission execution services to use it.

## Why

Mission execution services still duplicate config-building structure, which slows further Stage 3 and Stage 4 consolidation.

## In Scope

- add a shared mission execution config factory
- migrate mission-01/02/03 execution services to use the factory
- keep mission-specific behavior differences explicit through factory inputs
- preserve runtime behavior

## Out Of Scope

- removing mission-specific execution services
- removing mission-specific workers/simulators/validators

## Acceptance Criteria

- [x] Shared config factory exists and is used by mission-01/02/03 execution services.
- [x] Mission behavior is unchanged.
- [x] `./mvnw clean verify` passes.
