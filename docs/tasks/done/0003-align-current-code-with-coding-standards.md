# Task 0003: Align Current Code With Coding Standards

## Status

Done

## Summary

Refactor the current application code so it better matches the coding standards documented in `AGENTS.md`.

## Motivation

The current shell works, but the web layer still owns mission-specific presentation data and placeholder run behavior. The code should move closer to the project's conventions by keeping controllers thinner, preferring clearer domain structure, and applying `final` where mutation is not required.

## Related Documents

- `AGENTS.md`

## Scope

This task should cover:

- moving mission-specific page logic out of the web controller
- improving naming and structure where needed
- preferring `final` for variables, parameters, and fields when mutation is not needed
- keeping the current behavior unchanged

## Out Of Scope

This task should not include:

- changing gameplay behavior
- implementing learner code execution
- changing the mission layout beyond what already exists

## Acceptance Criteria

- [x] The controller is thinner than before.
- [x] Mission-specific logic is moved out of the web layer.
- [x] Current behavior remains unchanged.
- [x] The test suite passes.

## Verification

- `mvn -Dmaven.repo.local=/tmp/code-colony-m2 test`
