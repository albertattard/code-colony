# Task 0055: Introduce Generic Mission Execution Service (Stage 3A)

## Summary

Introduce a shared mission execution service and migrate mission-01 execution wiring to it as the first Stage 3 compatibility slice.

## Why

Mission-specific execution services are still a blocker for no-code mission additions.

## In Scope

- add a generic execution service that runs mission workers using `MissionExecutionConfig`
- migrate mission-01 execution service to delegate to the generic service
- keep mission-02 and mission-03 execution services unchanged for this slice
- maintain behavior parity and test coverage

## Out Of Scope

- removing mission-specific workers/simulators/validators
- migrating mission-02 and mission-03 execution wiring

## Acceptance Criteria

- [x] Generic execution service exists and is used by mission-01 execution service.
- [x] Mission-01 behavior remains unchanged.
- [x] Mission-02 and mission-03 continue working unchanged.
- [x] `./mvnw clean verify` passes.
