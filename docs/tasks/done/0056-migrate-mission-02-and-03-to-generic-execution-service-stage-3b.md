# Task 0056: Migrate Mission 02 And 03 To Generic Execution Service (Stage 3B)

## Summary

Migrate mission-02 and mission-03 execution services to use the shared `GenericMissionExecutionService`.

## Why

This continues Stage 3 incremental migration away from mission-specific execution runners.

## In Scope

- switch mission-02 execution service from `MissionExecutionRunner` to `GenericMissionExecutionService`
- switch mission-03 execution service from `MissionExecutionRunner` to `GenericMissionExecutionService`
- preserve all behavior and existing config

## Out Of Scope

- removing mission-specific workers/simulators/validators
- generic config-builder consolidation

## Acceptance Criteria

- [x] mission-02 execution service uses `GenericMissionExecutionService`
- [x] mission-03 execution service uses `GenericMissionExecutionService`
- [x] existing mission behavior remains unchanged
- [x] `./mvnw clean verify` passes
