# Task 0012: Show CORE Identifier In Status Panel

## Summary

Update the Mission 01 status panel to show the active CORE identifier.

## Why

The status panel should make it explicit which unit the learner is controlling. This becomes more important as the game grows to missions with more than one available CORE.

## In Scope

- updating the gameplay and Mission 01 specs to mention the active CORE identifier
- showing `CORE-01` in the Mission 01 status panel
- updating automated tests for the visible identifier

## Out Of Scope

- multi-CORE selection UI
- changing the learner API
- implementing missions with more than one active CORE

## Relevant Specs

- `docs/spec/gameplay-spec.md`
- `docs/spec/missions/mission-01-wake-the-core.md`

## Acceptance Criteria

- [x] The Mission 01 status panel shows the active CORE identifier.
- [x] Mission 01 displays `CORE-01` in that panel.
- [x] Automated tests cover the identifier in the rendered status panel.
