# Task 0015: Implement Mission 02 Charge The CORE

## Summary

Implement Mission 02 so the learner continues from Mission 01, rewrites the connection into a variable-based form, and charges CORE-01 from empty to full.

## Why

Mission 02 is the first step where the learner builds on previous code instead of solving an isolated puzzle. It introduces object reuse and repeated state changes while preserving the same environment and visual context.

## In Scope

- loading Mission 02 directly from Mission 01's `Next` action
- showing the Mission 02 page with the same layout as Mission 01
- opening the Mission 02 briefing modal on first load
- carrying forward the learner's successful Mission 01 code into the Mission 02 editor
- making Mission 02 writable so the learner can extend the previous code
- supporting `var core = Core.connect();` and repeated `core.charge();`
- increasing battery by one segment per successful charge call, capped at five
- updating UI feedback and status to reflect Mission 02 progress
- adding automated tests for Mission 02 success and failure cases

## Out Of Scope

- Mission 03 gameplay
- CORE movement rules
- repair mechanics
- persistence across browser sessions

## Relevant Specs

- `docs/spec/gameplay-spec.md`
- `docs/spec/student-api-spec.md`
- `docs/spec/missions/mission-02-charge-the-core.md`

## Acceptance Criteria

- [x] Clicking `Next` after Mission 01 loads Mission 02 directly.
- [x] Mission 02 opens with the same layout as Mission 01 and shows its own briefing modal.
- [x] Mission 02 preloads the learner's successful Mission 01 code into the editor.
- [x] The learner can update the code to use `var core = Core.connect();`.
- [x] Each successful `core.charge();` call restores one battery segment.
- [x] Battery is capped at `5/5`.
- [x] Mission 02 succeeds only when the battery reaches full charge.
- [x] Automated tests cover Mission 02 progression, charging, and full-battery success.
