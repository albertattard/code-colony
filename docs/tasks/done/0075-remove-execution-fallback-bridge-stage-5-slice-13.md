# Task 0075: Remove Execution Fallback Bridge (Stage 5 Slice 13)

## Summary

Remove objective-profile fallback wiring from `MissionExecutionFacade` and require runtime configuration for mission execution.

## Why

Missions 01-03 are now runtime-driven. Keeping fallback objective profiles duplicates logic and blocks full data-driven runtime behavior.

## Spec Impact

No spec update required in this slice. This is cleanup aligned with the staged migration plan.

## In Scope

- remove `PROFILE_BY_OBJECTIVE_KIND` fallback from `MissionExecutionFacade`
- fail fast when runtime configuration is missing
- update tests that relied on fallback behavior

## Out Of Scope

- introducing new runtime mechanics
- changing learner-facing mission behavior

## Acceptance Criteria

- [x] `MissionExecutionFacade` requires runtime settings.
- [x] Hardcoded objective-profile fallback logic is removed.
- [x] `./mvnw clean verify` passes.
