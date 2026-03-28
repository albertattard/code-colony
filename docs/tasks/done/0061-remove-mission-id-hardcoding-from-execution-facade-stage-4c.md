# Task 0061: Remove Mission-ID Hardcoding From Execution Facade (Stage 4C)

## Summary

Refactor `MissionExecutionFacade` to build mission execution config from mission behavior metadata instead of hardcoded mission-id switch/mapping.

## Why

The execution facade still hardcodes `mission-01/02/03`, which blocks adding missions without Java changes even when they use already-supported objective kinds.

## In Scope

- remove mission-id config map from `MissionExecutionFacade`
- resolve execution profile using `mission.yaml` objective kind
- preserve mission 01/02/03 behavior and existing tests

## Out Of Scope

- introducing new objective kinds or mechanics
- removing mission-specific workers/simulators/validators
- changing mission text/content

## Acceptance Criteria

- [x] `MissionExecutionFacade` no longer hardcodes `mission-01/02/03` routing.
- [x] Mission 01/02/03 execution behavior remains unchanged.
- [x] `./mvnw clean verify` passes.
