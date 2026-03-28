# Task 0043: Require Mission Console Content From Markdown

## Summary

Make mission code-panel hints and command references required mission content, loaded from markdown for all current missions.

## Why

Mission copy should come from authored content files, not Java constants. Required markdown sections keep a single source of truth and fail fast when content is incomplete.

## In Scope

- require `## Available Commands` and `## Hints` sections in mission briefing markdown
- remove mission-service fallback defaults for console hints and command references
- apply this to Mission 01, Mission 02, and Mission 03
- update docs to describe these sections as required

## Out Of Scope

- changing mission simulation behavior
- changing shared panel labels in templates

## Acceptance Criteria

- [x] `loadMissionConsoleContent()` fails fast if required console sections are missing.
- [x] Mission 01, Mission 02, and Mission 03 services do not define fallback console-copy constants.
- [x] Mission content docs mark `Available Commands` and `Hints` as required mission sections.
- [x] `./mvnw clean verify` passes.
