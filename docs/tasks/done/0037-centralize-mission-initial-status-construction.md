# Task 0037: Centralize Mission Initial Status Construction

## Summary

Introduce a shared factory to build `MissionCoreStatus` from mission map spawn data, and migrate Mission 01-03 execution services to use it.

## Why

Each mission execution service currently manually assembles `missionInitialStatus`, duplicating state mapping and meter extraction logic.

## In Scope

- add a shared factory for initial status creation
- migrate Mission 01, 02, and 03 execution services to use the factory
- remove duplicated status-state mapping helpers from mission services

## Out Of Scope

- changing learner-visible status behavior
- redesigning `MissionCoreStatus`

## Acceptance Criteria

- [x] Mission execution services no longer manually assemble repeated initial status fields.
- [x] Initial status behavior remains unchanged for missions 01-03.
- [x] `./mvnw clean verify` passes.
