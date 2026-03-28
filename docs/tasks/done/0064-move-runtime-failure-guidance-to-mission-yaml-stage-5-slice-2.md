# Task 0064: Move Runtime Failure Guidance To mission.yaml (Stage 5 Slice 2)

## Summary

Move mission-specific runtime failure guidance copy from Java validator code into `mission.yaml`.

## Why

`GenericMissionValidator` still contains mission-specific learner-facing guidance strings, which prevents configuration-complete mission behavior.

## In Scope

- extend `mission.yaml` schema with validation runtime guidance text
- load this field through mission behavior config
- pass guidance to generic worker/validator via worker arguments
- keep validation logic and outcomes unchanged

## Out Of Scope

- moving all success/incomplete copy to config
- new objective kinds or mechanics

## Acceptance Criteria

- [x] Mission behavior loader reads mission-specific runtime guidance text.
- [x] Generic validator runtime-failure guidance uses config values.
- [x] `./mvnw clean verify` passes.
