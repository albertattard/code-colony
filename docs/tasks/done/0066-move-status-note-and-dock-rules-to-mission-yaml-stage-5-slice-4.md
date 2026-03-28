# Task 0066: Move Status Note And Dock Rules To mission.yaml (Stage 5 Slice 4)

## Summary

Move remaining status-note and dock-label rules from `GenericMissionValidator` into mission configuration.

## Why

`GenericMissionValidator` still hardcodes status-note text branches and a mission-specific dock-position rule, which blocks fully config-driven validation output.

## In Scope

- move status-note branch copy into `validation.messages`
- move mission-03 dock-connected position rule into config
- keep validation outcomes and mechanics unchanged

## Out Of Scope

- new objective kinds or mechanics
- changing mission progression

## Acceptance Criteria

- [x] Generic validator status-note text comes from mission config.
- [x] Mission-03 dock label rule comes from mission config.
- [x] `./mvnw clean verify` passes.
