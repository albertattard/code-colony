# Task 0067: Move Runtime Retry Hint To mission.yaml (Stage 5 Slice 5)

## Summary

Move the remaining hardcoded runtime retry hint from Java validator/worker code into mission configuration.

## Why

A fallback learner-facing runtime guidance string is still hardcoded in generic runtime classes, preventing complete config ownership of learner-facing feedback copy.

## In Scope

- add `validation.runtimeRetryHint` to mission behavior config
- load this field from `mission.yaml`
- use it in generic worker/validator runtime failure feedback
- remove hardcoded retry hint literal from Java code

## Out Of Scope

- validation decision logic changes
- new objective kinds or mechanics

## Acceptance Criteria

- [x] Mission behavior loader reads `validation.runtimeRetryHint`.
- [x] Generic runtime failure feedback uses configured retry hint.
- [x] `./mvnw clean verify` passes.
