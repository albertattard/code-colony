# Task 0076: Prove No-Code Mission Addition With mission-04 (Stage 6)

## Summary

Add `mission-04` through mission files and manifest registration, and verify it is discoverable/playable without mission-specific runtime class additions.

## Why

Stage 6 requires proving that a new mission can be added with existing mechanics through content configuration and mission files.

## Spec Impact

No spec changes expected. This task validates the existing mission directory and runtime contracts.

## In Scope

- add `content/missions/mission-04/` with `content.md`, `map.yaml`, `mission.yaml`
- register mission-04 in `content/missions/missions.yaml`
- remove remaining mission-id hardcoding that blocks generic mission loading/presentation
- update automated tests for manifest order and mission navigation

## Out Of Scope

- adding new runtime mechanics or objective kinds
- redesigning mission UI

## Acceptance Criteria

- [x] mission-04 is discoverable through manifest/catalog.
- [x] mission-04 is playable with existing runtime pipeline.
- [x] no mission-specific runtime classes are introduced for mission-04.
- [x] `./mvnw clean verify` passes.
