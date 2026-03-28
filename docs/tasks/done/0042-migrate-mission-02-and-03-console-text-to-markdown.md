# Task 0042: Migrate Mission 02 And 03 Console Text To Markdown

## Summary

Apply the Mission 01 console-content pattern to Mission 02 and Mission 03 so command-reference and hint copy is sourced from mission markdown content.

## Why

Learner-facing text should default to content files rather than mission service constants. This keeps copy iteration fast and consistent across missions.

## In Scope

- migrate Mission 02 hints and command references to markdown-backed loading
- migrate Mission 03 hints and command references to markdown-backed loading
- keep fallback defaults in services for runtime safety
- add/update tests for mission console content loading

## Out Of Scope

- changing mission simulation behavior
- changing template titles or panel layout

## Acceptance Criteria

- [x] Mission 02 code panel hints and commands are loaded from markdown content.
- [x] Mission 03 code panel hints and commands are loaded from markdown content.
- [x] Services keep sensible fallback defaults when markdown sections are missing.
- [x] `./mvnw clean verify` passes.
