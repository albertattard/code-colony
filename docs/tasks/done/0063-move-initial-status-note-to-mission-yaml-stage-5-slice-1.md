# Task 0063: Move Initial Status Note To mission.yaml (Stage 5 Slice 1)

## Summary

Move mission initial status note text from Java code into `mission.yaml`.

## Why

Mission execution still embeds mission-specific status-note strings in Java. This blocks configuration-complete mission behavior.

## In Scope

- extend `mission.yaml` execution schema with `initialStatusNoteTemplate`
- load this field through mission behavior config
- use it in `MissionExecutionFacade` initial status creation
- support existing placeholders using mission/map context

## Out Of Scope

- moving all validator copy into config
- new objective kinds or new mechanics

## Acceptance Criteria

- [x] Mission behavior loader reads `execution.initialStatusNoteTemplate`.
- [x] `MissionExecutionFacade` uses the loaded template instead of hardcoded notes.
- [x] `./mvnw clean verify` passes.
