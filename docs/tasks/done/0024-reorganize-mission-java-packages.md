# Task 0024: Reorganize Mission Java Packages

## Summary

Reorganize mission-specific Java files under `src/main/java/game/codecolony/mission` into per-mission subpackages.

## Why

As more missions are added, grouping mission runtime code by mission package keeps navigation and maintenance clearer for contributors.

## In Scope

- moving Mission 01 classes into `mission01`
- moving Mission 02 classes into `mission02`
- moving Mission 03 classes into `mission03`
- updating package declarations and imports in production and test code

## Out Of Scope

- changing mission behavior
- changing mission routes or template layout

## Acceptance Criteria

- [x] Mission 01 Java files reside under `game.codecolony.mission.mission01`.
- [x] Mission 02 Java files reside under `game.codecolony.mission.mission02`.
- [x] Mission 03 Java files reside under `game.codecolony.mission.mission03`.
- [x] `MissionController` and mission tests compile against updated package names.
- [x] `./mvnw clean verify` passes.
