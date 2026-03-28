# Task 0062: Remove Mission-Specific Runtime Classes (Stage 4D)

## Summary

Replace mission-specific worker/simulator/validator classes with generic runtime classes for currently supported objective kinds.

## Why

Mission-specific runtime classes still block no-code mission addition for missions that reuse existing mechanics.

## In Scope

- add generic mission worker/simulator/validator runtime classes
- route `MissionExecutionFacade` profiles to generic runtime classes
- remove mission-01/02/03 worker/simulator/validator/simulation classes
- preserve mission behavior and tests

## Out Of Scope

- adding new objective kinds or mechanics
- changing mission narrative/content

## Acceptance Criteria

- [x] `MissionExecutionFacade` uses generic runtime classes for supported objective kinds.
- [x] Mission-specific runtime classes for mission-01/02/03 are removed.
- [x] `./mvnw clean verify` passes.
