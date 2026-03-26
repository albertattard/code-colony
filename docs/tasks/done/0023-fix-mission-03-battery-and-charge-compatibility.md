# Task 0023: Fix Mission 03 Battery And Charge Compatibility

## Summary

Fix Mission 03 so carried Mission 02 charge commands do not fail execution, and movement updates battery state correctly.

## Why

Mission 03 preloads successful Mission 02 code, which commonly includes repeated `core.charge();` calls. Mission 03 must remain compatible with that carried code and should show battery consumption when movement occurs.

## In Scope

- updating Mission 03 spec to define battery usage and carried-command compatibility
- supporting `core.charge()` in the Mission 03 simulator while on `B1`
- making `core.move()` consume one battery segment in Mission 03
- surfacing updated battery state in Mission 03 status results
- adding tests for Mission 03 battery and UI behavior

## Out Of Scope

- changing Mission 03 primary objective or success conditions
- introducing new student API methods

## Relevant Specs

- `docs/spec/missions/mission-03-repair-the-core.md`
- `docs/spec/gameplay-spec.md`
- `docs/spec/student-api-spec.md`

## Acceptance Criteria

- [x] Mission 03 accepts `core.charge()` without runtime command rejection.
- [x] Mission 03 keeps charge capped at `5/5` and restricted to dock `B1`.
- [x] A successful `core.move()` reduces battery by one segment.
- [x] Mission 03 run results and rendered UI reflect updated battery after movement.
- [x] Dock (`B1`) and repair station (`B3`) visuals remain in the background when CORE moves onto or away from those tiles.
- [x] Automated tests cover the fixed behavior.
