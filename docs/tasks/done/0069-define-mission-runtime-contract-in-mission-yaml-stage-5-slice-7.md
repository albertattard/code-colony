# Task 0069: Define mission.yaml Runtime Contract (Stage 5 Slice 7)

## Summary

Define a formal `runtime` contract in `mission.yaml` so execution wiring details can move from Java branching into mission data.

## Why

`MissionExecutionFacade` still hardcodes objective-kind execution profiles. A documented runtime schema is required before loader and execution refactors can safely migrate this logic to mission files.

## Spec Impact

This task is spec-first and documentation-only.

- update `docs/spec/mission-definition-spec.md` with `runtime` schema and validation rules
- no runtime code changes in this slice

## In Scope

- define `runtime.worker` and `runtime.simulator` fields
- define `runtime.initialStatus` shape and supported modes
- define `runtime.args` structure and deterministic argument ordering rules
- define placeholder syntax and resolution semantics
- define validation failure expectations for invalid runtime config

## Out Of Scope

- implementing runtime config parsing
- wiring execution to consume the new runtime section
- removing fallback hardcoded objective profiles

## Acceptance Criteria

- [x] Mission Definition Spec includes a concrete `runtime` section contract.
- [x] Placeholder and validation rules are explicit and contributor-readable.
- [x] The slice is tracked as a standalone task for follow-up implementation work.
