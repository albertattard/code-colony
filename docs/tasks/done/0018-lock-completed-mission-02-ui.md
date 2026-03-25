# Task 0018: Lock Completed Mission 02 UI

## Summary

When Mission 02 succeeds, switch the page into a completed state: make the code editor read-only, hide `Run` and `Reset`, and show `Next`.

## Why

Mission 02 should acknowledge completion as clearly as Mission 01. Leaving the editing and retry controls visible after success makes the progression feel inconsistent.

## In Scope

- updating the Mission 02 spec to require the completed-state UI
- making the Mission 02 editor read-only after success
- hiding `Run` and `Reset` after success
- showing `Next` after success
- updating automated tests for the Mission 02 completed-state behavior

## Out Of Scope

- implementing Mission 03
- defining the final destination for the Mission 02 `Next` action

## Relevant Specs

- `docs/spec/missions/mission-02-charge-the-core.md`

## Acceptance Criteria

- [x] After Mission 02 succeeds, the code editor is read-only.
- [x] After Mission 02 succeeds, `Run` and `Reset` are no longer shown.
- [x] After Mission 02 succeeds, `Next` is shown.
- [x] Automated tests cover the Mission 02 completed-state behavior.
