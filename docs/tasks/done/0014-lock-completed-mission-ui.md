# Task 0014: Lock Completed Mission UI

## Summary

When Mission 01 succeeds, switch the page into a completed state: make the code editor read-only, hide `Run` and `Reset`, and show a `Next` action that moves the player to the next mission handoff.

## Why

Once the learner has completed the mission objective, the page should acknowledge that state clearly and guide them forward. Leaving the run/reset controls active makes the outcome feel unfinished and weakens the sense of progression.

## In Scope

- updating the gameplay spec to define the completed mission state in the UI
- updating the Mission 01 spec to describe the success-state control changes
- making the Mission 01 editor read-only after success
- hiding `Run` and `Reset` after success
- showing a `Next` action after success
- adding a simple Mission 02 handoff page so the action has a real destination
- updating automated tests for the completed mission flow

## Out Of Scope

- implementing Mission 02 gameplay
- persisting progression across browser sessions
- adding mission selection UI

## Relevant Specs

- `docs/spec/gameplay-spec.md`
- `docs/spec/missions/mission-01-wake-the-core.md`

## Acceptance Criteria

- [x] After Mission 01 succeeds, the code editor is read-only.
- [x] After Mission 01 succeeds, `Run` and `Reset` are no longer shown.
- [x] After Mission 01 succeeds, a `Next` action is shown.
- [x] The `Next` action navigates to a Mission 02 handoff page.
- [x] Automated tests cover the completed mission state and the handoff navigation.
