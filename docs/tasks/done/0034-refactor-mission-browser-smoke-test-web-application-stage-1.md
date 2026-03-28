# Task 0034: Refactor Mission Browser Smoke Test With WebApplication (Stage 1)

## Summary

Start extracting reusable browser test actions/assertions in `MissionBrowserSmokeTest` into a `WebApplication` helper while keeping behavior unchanged.

## Why

The smoke test had a broken lifecycle during refactoring and duplicated low-level Playwright calls. A staged extraction improves readability and reduces future copy/paste risk.

## In Scope

- keep browser/session lifecycle inside a single `WebApplication` scope
- extract initial Mission 01 flow helpers (navigation, modal controls, code fill/assert, run-and-wait)
- keep remaining mission assertions intact for follow-up incremental extraction

## Out Of Scope

- full abstraction of all Mission 02 and Mission 03 assertions in this stage
- changes to gameplay or mission behavior

## Acceptance Criteria

- [x] `MissionBrowserSmokeTest` compiles with the new helper usage.
- [x] Initial Mission 01 flow uses `WebApplication` helper methods.
- [x] Remaining direct Playwright assertions are left for later staged refactors.
- [x] `./mvnw clean verify` passes.
