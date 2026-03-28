# Task 0035: Refactor Mission Browser Smoke Test With WebApplication (Stage 2)

## Summary

Continue the staged extraction in `MissionBrowserSmokeTest` by replacing the remaining raw Playwright block with `WebApplication` methods.

## Why

Stage 1 introduced the helper but still relied on a large `withPage(...)` lambda. This stage improves readability and keeps a consistent abstraction boundary.

## In Scope

- remove the large `withPage(...)` lambda
- extract mission-state assertions for Mission 01 completion, Mission 02 start/completion, and Mission 03 start
- extract mission navigation and Mission 02 run-response interaction methods

## Out Of Scope

- moving `WebApplication` into a shared test utility package
- introducing new gameplay assertions beyond the current smoke flow

## Acceptance Criteria

- [x] `MissionBrowserSmokeTest` no longer uses `withPage(...)`.
- [x] Mission 01 to Mission 03 flow remains behaviorally unchanged.
- [x] `./mvnw clean verify` passes.
