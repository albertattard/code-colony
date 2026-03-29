# Task 0070: Parse And Validate Runtime Contract (Stage 5 Slice 8)

## Summary

Implement loader-level parsing and validation for the optional `runtime` section in `mission.yaml` without changing runtime execution wiring.

## Why

The `runtime` contract is now specified, but mission behavior loading does not yet parse or validate it. This slice adds typed config support and fail-fast diagnostics before dual-path execution wiring is introduced.

## Spec Impact

No spec changes expected in this slice. Implementation should follow `docs/spec/mission-definition-spec.md` runtime contract.

## In Scope

- extend mission behavior config model with optional typed runtime settings
- parse `runtime` when present
- validate worker/simulator ids
- validate `initialStatus` mode and required fields
- validate ordered runtime args structure (`name`, `value`, duplicates)
- validate placeholder syntax format at loader level
- add loader tests for success and failure cases

## Out Of Scope

- wiring execution to consume the new `runtime` settings
- removing objective-profile fallback logic
- mission yaml migration for runtime profiles

## Acceptance Criteria

- [x] `MissionBehaviorLoader` parses optional `runtime` config into typed model.
- [x] Invalid `runtime` config fails fast with clear diagnostics.
- [x] Existing missions continue loading with missing `runtime`.
- [x] `./mvnw clean verify` passes.
