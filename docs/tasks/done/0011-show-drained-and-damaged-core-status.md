# Task 0011: Show Drained And Damaged CORE Status

## Summary

Update Mission 01 so that connecting to the docked CORE reveals that the unit is online but not yet operational: its battery is empty and its health is low.

## Why

Mission 01 should set up the next recovery step instead of implying the CORE is already ready for action. Showing depleted power and visible damage gives the learner a clearer sense of progression and prepares Mission 02 to focus on charging the unit.

## In Scope

- updating the Mission 01 spec to describe the connected-but-depleted state
- showing battery and health as five-segment bars once the CORE is connected
- rendering the battery bar as empty and the health bar as low in Mission 01
- keeping the offline state free of telemetry details
- updating automated tests for the new learner-facing status presentation

## Out Of Scope

- implementing `move()`, `rotateClockwise()`, `rotateCounterClockwise()`, `charge()`, or `repair()`
- implementing battery consumption for actions
- implementing Mission 02

## Relevant Specs

- `docs/spec/missions/mission-01-wake-the-core.md`
- `docs/spec/gameplay-spec.md`

## Acceptance Criteria

- [x] Mission 01 no longer describes the connected CORE as fully charged.
- [x] Before connection, the status panel shows no battery or health telemetry.
- [x] After `Core.connect();`, the status panel shows:
- [x] an online state
- [x] a five-segment battery bar with zero filled segments
- [x] a five-segment health bar with a low filled value
- [x] The automated test suite covers the new connected status presentation.
