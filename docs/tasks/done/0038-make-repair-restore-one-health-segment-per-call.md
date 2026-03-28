# Task 0038: Make Repair Restore One Health Segment Per Call

## Summary

Change Mission 03 repair behavior so each successful `core.repair()` restores one health segment, matching the incremental pattern used by `core.charge()`.

## Why

Current repair behavior jumps directly to full health in one call, which is inconsistent with charging semantics and reduces step-by-step feedback for learners.

## In Scope

- update mission simulation/validation so repair increments health by one per successful call
- keep mission success requiring full health
- update mission hints/tests/spec text to reflect incremental repair

## Out Of Scope

- changing mission map layout
- adding new commands or station types

## Acceptance Criteria

- [x] each successful `core.repair()` increases health by exactly one segment, capped at max
- [x] mission succeeds only when health reaches full capacity at repair station
- [x] tests are updated and passing via `./mvnw clean verify`
