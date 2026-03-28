# Task 0053: Add Mission Manifest Loader (Stage 2A)

## Summary

Introduce `content/missions/missions.yaml` as the source of mission order and mission slug-to-content mapping.

## Why

Mission order and inclusion/exclusion should be controlled by editable content files rather than hardcoded logic.

## In Scope

- add `content/missions/missions.yaml`
- add a manifest model and loader with validation
- wire `MissionCatalog` mission discovery to manifest order/content entries
- add tests for manifest parsing and validation

## Out Of Scope

- controller route migration to manifest slugs
- mission execution service refactor

## Acceptance Criteria

- [x] `content/missions/missions.yaml` exists and declares current missions.
- [x] Manifest loader validates mission entries and file contract.
- [x] `MissionCatalog` uses manifest order for current mission ids.
- [x] Tests cover success and validation failures.
- [x] `./mvnw clean verify` passes.
