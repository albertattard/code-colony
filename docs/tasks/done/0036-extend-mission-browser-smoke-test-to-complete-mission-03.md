# Task 0036: Extend Mission Browser Smoke Test To Complete Mission 03

## Summary

Extend `MissionBrowserSmokeTest` so it executes Mission 03 code and verifies the repaired completion state, not only initial page load.

## Why

The smoke flow currently stops at Mission 03 initial state. Completing Mission 03 in-browser gives end-to-end confidence for the full mission progression.

## In Scope

- submit Mission 03 code through the browser test
- wait for Mission 03 run response
- assert completion indicators after repair

## Out Of Scope

- broader browser test framework extraction
- changing mission gameplay behavior

## Acceptance Criteria

- [x] Smoke test runs Mission 03 with learner code.
- [x] Smoke test asserts Mission 03 completed state in UI.
- [x] `./mvnw clean verify` passes.
