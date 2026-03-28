# Task 0065: Move Validation Outcome Copy To mission.yaml (Stage 5 Slice 3)

## Summary

Move mission success/incomplete learner-facing validation copy from Java into `mission.yaml`.

## Why

`GenericMissionValidator` still hardcodes most mission-specific outcome copy, blocking configuration-complete mission behavior.

## In Scope

- extend mission validation config with a message dictionary
- pass validation config payload to generic worker
- render success/incomplete copy from mission config in generic validator
- keep validation decision logic unchanged

## Out Of Scope

- new objective kinds or mechanics
- changing mission outcomes/conditions

## Acceptance Criteria

- [x] Mission behavior loader reads validation outcome messages.
- [x] Generic validator success/incomplete copy comes from config.
- [x] `./mvnw clean verify` passes.
