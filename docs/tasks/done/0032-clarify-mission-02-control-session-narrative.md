# Task 0032: Clarify Mission 02 Control Session Narrative

## Summary

Clarify Mission 02 wording so `Core.connect()` is framed as re-establishing a control session for the current run, not waking CORE-01 for the first time.

## Why

Mission progression currently risks a contradiction: Mission 01 establishes CORE-01 as online, but Mission 02 still requires `Core.connect()`. Narrative and UI copy should distinguish world state continuity from per-run control handle setup.

## In Scope

- update Mission 02 spec language
- update Mission 02 briefing content
- update Mission 02 hints/command descriptions/status note wording in code

## Out Of Scope

- gameplay/mechanics changes
- API changes

## Acceptance Criteria

- [x] Mission 02 copy clearly states CORE-01 remains online from Mission 01.
- [x] Mission 02 copy clearly states `Core.connect()` is required to establish control for the current run.
- [x] Existing mission behavior remains unchanged.
- [x] `./mvnw clean verify` passes.
