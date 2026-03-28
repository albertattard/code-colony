# Task 0046: Merge Mission Markdown Into content.md

## Summary

Merge mission markdown into a single `content.md` file per mission.

## Why

Mission copy can be authored in one place per mission while keeping map data separate in `map.yaml`.

## In Scope

- move explanation sections into each mission `content.md`
- update `NarrativeContentService` to load mission narrative, console, initial-run, and explanation sections from `content.md`
- remove mission `explain.md` files
- rename mission `briefing.md` files to `content.md`
- update content docs to reflect the merged structure

## Out Of Scope

- changing explanation rendering behavior
- changing map file format

## Acceptance Criteria

- [x] Mission explanation loading reads from mission `content.md`.
- [x] Mission narrative, console, and initial-run loading read from mission `content.md`.
- [x] Mission `briefing.md` files are renamed to `content.md`.
- [x] Mission `explain.md` files are removed.
- [x] Mission 01/02/03 explanation tests still pass.
- [x] `./mvnw clean verify` passes.
