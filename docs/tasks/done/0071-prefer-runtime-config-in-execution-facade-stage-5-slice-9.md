# Task 0071: Prefer Runtime Config In Execution Facade (Stage 5 Slice 9)

## Summary

Wire mission execution to prefer `mission.yaml.runtime` when present, while keeping existing objective-kind profile mapping as a fallback path.

## Why

Runtime config is now parsed and validated, but execution still uses hardcoded profile branching. This slice introduces dual-path execution so missions can start migrating to runtime-driven wiring incrementally.

## Spec Impact

No spec update required in this slice. Behavior should align with the runtime contract already documented.

## In Scope

- update `MissionExecutionFacade` to use runtime config when available
- keep objective-profile fallback when runtime config is missing
- resolve runtime placeholders into worker arguments and initial status values
- add tests for runtime-present, fallback, and unresolved token failures

## Out Of Scope

- removing fallback profile map
- migrating mission yaml files to runtime blocks

## Acceptance Criteria

- [x] Runtime config is used when present.
- [x] Existing missions without runtime config continue using fallback profiles.
- [x] Unknown runtime placeholder tokens fail fast with mission-scoped diagnostics.
- [x] `./mvnw clean verify` passes.
