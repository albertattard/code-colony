# Task 0017: Align Mission 02 Initial State With Mission 01

## Summary

Make Mission 02 open with the same connected CORE-01 status the player saw at the end of Mission 01, while still requiring `Core.connect()` in code to obtain the reference used for charging.

## Why

Mission 02 is meant to feel like a direct continuation of Mission 01. Resetting the visible CORE state back to offline weakens that continuity and makes the progression feel less coherent.

## In Scope

- updating the Mission 02 spec to distinguish visible connection state from obtaining a code reference
- showing Mission 02 initial and reset status as online with the depleted battery and damaged health state
- preserving the learner's exact successful Mission 01 code in the Mission 02 editor
- keeping `Core.connect()` required in Mission 02 code so the learner can obtain a `CORE` reference
- updating automated tests for the revised Mission 02 initial state

## Out Of Scope

- changing Mission 02 success rules beyond the connect-reference clarification
- changing Mission 03

## Relevant Specs

- `docs/spec/missions/mission-02-charge-the-core.md`

## Acceptance Criteria

- [x] Mission 02 first load shows CORE-01 online with battery `0/5` and health `1/5`.
- [x] Mission 02 reset restores that same connected-looking state.
- [x] Mission 02 still requires `Core.connect()` in learner code before `core.charge()` can succeed.
- [x] Mission 02 preserves the learner's exact successful Mission 01 code text.
- [x] Automated tests cover the revised initial state and code continuity.
