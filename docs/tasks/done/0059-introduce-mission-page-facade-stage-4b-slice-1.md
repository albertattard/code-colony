# Task 0059: Introduce Mission Page Facade (Stage 4B Slice 1)

## Summary

Introduce a generic mission page facade and route page assembly through it.

## Why

`MissionController` still hardcodes mission-specific page-service wiring and switch blocks. This is a blocker to fully generic mission runtime.

## In Scope

- add a mission page facade as a generic entry point by mission id
- move mission page dispatch/default-code switches from controller into the facade
- update `MissionController` to depend on the new facade
- keep existing mission-specific page services as temporary delegates

## Out Of Scope

- removing mission-specific page services
- changing mission behavior, text, or progression
- moving additional mission behavior into config

## Acceptance Criteria

- [x] `MissionController` uses mission page facade instead of mission-specific page-service fields.
- [x] Mission 01-03 behavior remains unchanged.
- [x] `./mvnw clean verify` passes.
