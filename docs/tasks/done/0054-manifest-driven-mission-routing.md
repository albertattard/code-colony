# Task 0054: Manifest-Driven Mission Routing (Stage 2B)

## Summary

Drive mission routing, start mission selection, and next-mission links from `content/missions/missions.yaml` enabled mission order.

## Why

Mission order and inclusion/exclusion should work without changing controller constants.

## In Scope

- add runtime route catalog from mission manifest
- use manifest-first mission route for session start
- replace hardcoded mission route constants with dynamic `{missionName}` handling
- compute next mission link from manifest order
- keep existing mission services as compatibility bridge
- update controller/browser tests for manifest-driven flow

## Out Of Scope

- replacing mission-specific execution services
- introducing new mission mechanics

## Acceptance Criteria

- [x] session start redirects to first enabled mission from manifest
- [x] mission page/run/reset/explain routes use `{missionName}`
- [x] next mission links follow manifest order
- [x] tests cover manifest-driven routing behavior
- [x] `./mvnw clean verify` passes
