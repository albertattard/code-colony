# Task 0068: Move Runtime Command Gating To mission.yaml (Stage 5 Slice 6)

## Summary

Move runtime command availability rules from hardcoded objective checks in `GenericMissionSimulator` to optional `allowedRuntimeCommands` mission config.

## Why

Runtime command gating is still hardcoded by objective kind, which prevents fully config-driven mission behavior.

## In Scope

- add optional `allowedRuntimeCommands` to mission behavior config
- default to all commands allowed when the field is missing
- wire allowed runtime commands into generic worker/simulator
- remove objective-kind command gating checks from simulator

## Out Of Scope

- changing objective validation logic
- changing command semantics

## Acceptance Criteria

- [x] Runtime command gating comes from optional mission config.
- [x] Missing `allowedRuntimeCommands` means all commands are allowed.
- [x] `./mvnw clean verify` passes.
