# Task 0058: Remove Mission-Specific Execution Services (Stage 4A)

## Summary

Introduce a generic mission execution facade and remove mission-specific execution service classes.

## Why

Mission-specific execution services remain a direct blocker to mission-generic runtime.

## In Scope

- add one generic mission execution facade by mission content id
- wire mission services to use the generic facade
- remove mission-01/02/03 execution service classes
- preserve behavior and tests

## Out Of Scope

- removing mission-specific page services
- removing mission-specific workers/simulators/validators

## Acceptance Criteria

- [x] Generic mission execution facade is used instead of mission-specific execution services.
- [x] mission-01/02/03 execution service classes are removed.
- [x] Existing behavior remains unchanged.
- [x] `./mvnw clean verify` passes.
