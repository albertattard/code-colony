# Task 0025: Extract Mission Status Meter Value Object

## Summary

Extract a dedicated value object for repeated `level`/`capacity` pairs used by mission CORE status.

## Why

`MissionCoreStatus` carried repeated primitive pairs for battery and health. A single value object improves model clarity, centralizes validation, and supports additional status bars with the same semantics.

## In Scope

- adding an immutable `MissionStatusMeter` value object
- updating `MissionCoreStatus` to hold `battery` and `health` meter objects
- preserving existing compatibility methods (`batteryLevel()`, `batteryCapacity()`, `healthLevel()`, `healthCapacity()`)
- ensuring mission worker support classes include the new value object

## Out Of Scope

- changing mission gameplay behavior
- changing template rendering contracts

## Acceptance Criteria

- [x] `MissionStatusMeter` enforces basic invariants for `level` and `capacity`.
- [x] `MissionCoreStatus` models battery and health through meter objects.
- [x] Existing call sites and templates continue to work without behavior changes.
- [x] Isolated mission worker execution includes the new value object.
- [x] `./mvnw clean verify` passes.
