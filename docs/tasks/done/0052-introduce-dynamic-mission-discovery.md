# Task 0052: Introduce Dynamic Mission Discovery (Stage 1)

## Summary

Replace hardcoded mission-id catalog logic with filesystem discovery from `content/missions/` and fail fast when required mission files are missing.

## Why

Mission runtime should discover available missions without editing Java code for each new mission.

## In Scope

- implement filesystem-backed mission discovery
- remove hardcoded mission list usage in mission loaders
- validate mission directory contract (`content.md`, `map.yaml`, `mission.yaml`)
- add tests for discovery and missing-file failures

## Out Of Scope

- generic mission controller/service execution refactor
- mission progression routing redesign

## Acceptance Criteria

- [x] Mission discovery uses filesystem directories under `content/missions/`.
- [x] Mission loaders do not depend on hardcoded mission id lists.
- [x] Missing required mission files fail fast with clear errors.
- [x] Automated tests cover discovery and missing-file behavior.
- [x] `./mvnw clean verify` passes.
