# Task 0044: Load Mission 01 Initial Run Copy From Markdown

## Summary

Move Mission 01 pre-run feedback copy from `WakeTheCoreMissionService.initialRunResult()` into mission markdown content.

## Why

Mission learner-facing copy should come from text content files instead of Java constants, so wording updates do not require code edits.

## In Scope

- add required Mission 01 markdown sections for initial run headline, summary, events, feedback, and status note
- load these sections through `NarrativeContentService`
- use loaded content when constructing Mission 01 `initialRunResult()`
- keep runtime-only fields (`success`, stdout/stderr, status wiring) in Java

## Out Of Scope

- migrating Mission 02 and Mission 03 initial run copy
- changing mission runtime behavior

## Acceptance Criteria

- [x] Mission 01 `initialRunResult()` text is loaded from mission markdown sections.
- [x] Missing Mission 01 initial-run sections fail fast with clear errors.
- [x] `./mvnw clean verify` passes.
