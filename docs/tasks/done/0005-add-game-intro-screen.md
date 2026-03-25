# Task 0005: Add Game Intro Screen

## Status

Done

## Summary

Add a player intro screen before Mission 01 that introduces the game premise, explains the player's role, describes the browser-based Java gameplay loop, and hands off cleanly into the first mission.

## Motivation

The current flow drops the player directly into Mission 01. That works for internal iteration, but it assumes the player already understands the story context, their role, what a CORE unit is, and how the game is played.

For beginner learners, this is unnecessary cognitive load before they write their first line of Java. A short intro screen should reduce that friction and make the transition into Mission 01 feel deliberate rather than abrupt.

This task should implement the onboarding slice described in the game intro spec without expanding scope into a full menu, world map, or account system.

## Related Documents

- `docs/spec/game-intro-spec.md`
- `docs/spec/product-spec.md`
- `docs/spec/gameplay-spec.md`
- `docs/spec/missions/mission-01-wake-the-core.md`

## Scope

This task should cover:

- adding a game intro route as the first player-facing entry screen
- presenting a concise game premise and player role
- explaining the mission loop in beginner-friendly language
- preparing the player for Mission 01 as a small first programming task
- providing a single primary action to start Mission 01
- updating the browser flow so the player lands on the intro screen instead of directly on the mission screen
- adding automated test coverage for the new entry flow

## Out Of Scope

This task should not include:

- a multi-destination main menu
- mission selection or progression UI beyond the handoff to Mission 01
- save state or profile support
- narrative branching
- artwork-heavy presentation requirements

## Acceptance Criteria

- [x] Visiting the main entry point shows a game intro screen instead of landing directly on Mission 01.
- [x] The intro screen explains the player's role and the colony situation in concise language.
- [x] The intro screen explains that the player writes small Java commands in the browser and runs them from the game interface.
- [x] The intro screen prepares the player for Mission 01 without replacing the mission briefing.
- [x] The intro screen provides one clear primary action to start Mission 01.
- [x] Starting Mission 01 from the intro screen takes the player to the existing mission page.
- [x] Automated tests cover the intro screen and the transition into Mission 01.

## Notes

This task should keep the intro narrow and functional. The goal is orientation, not a polished front-end showcase.

The screen should answer the beginner's initial questions before they see the mission editor:

- who they are
- where they are
- what CORE units are for
- how the code-to-action loop works
- what they will do first
