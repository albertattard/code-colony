# Task 0031: Make Mission Worker Config Type-Safe

## Summary

Refactor mission execution configuration to use worker class references (`Class<?>`) instead of raw class-name strings.

## Why

String-based class names are fragile under refactors and typo-prone. Using class references improves compile-time safety while still allowing process launch to use class names at the execution boundary.

## In Scope

- replace `workerClassName` in `MissionExecutionConfig` with `workerClass`
- update mission execution services to pass worker class literals
- update runner to resolve worker class name from class reference at launch
- update tests for the new type-safe API

## Out Of Scope

- behavioral changes to mission execution
- broader process-launch architecture changes

## Acceptance Criteria

- [x] `MissionExecutionConfig` builder accepts worker class references.
- [x] Mission execution services no longer pass worker class names as raw strings.
- [x] Mission execution still launches worker processes correctly.
- [x] `./mvnw clean verify` passes.
