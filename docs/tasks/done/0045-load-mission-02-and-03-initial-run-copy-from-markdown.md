# Task 0045: Load Mission 02 And 03 Initial Run Copy From Markdown

## Summary

Move Mission 02 and Mission 03 pre-run feedback copy from service methods into mission markdown content.

## Why

Initial run copy should follow the same content-authoring workflow as briefing and command/hint text so learner-facing wording changes do not require Java edits.

## In Scope

- add required initial-run sections to mission-02 and mission-03 briefing markdown files
- load those sections via `NarrativeContentService.loadMissionInitialRunContent(...)`
- update Mission 02 and Mission 03 services to construct `initialRunResult()` from loaded content
- keep runtime-only fields (`success`, stdout/stderr, status wiring) in Java

## Out Of Scope

- changing mission execution behavior
- changing template layout or shared labels

## Acceptance Criteria

- [x] Mission 02 `initialRunResult()` copy is loaded from markdown.
- [x] Mission 03 `initialRunResult()` copy is loaded from markdown.
- [x] Missing required initial-run sections fail fast.
- [x] `./mvnw clean verify` passes.
