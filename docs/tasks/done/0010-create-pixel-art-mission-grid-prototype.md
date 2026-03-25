# Task 0010: Create Pixel-Art Mission Grid Prototype

## Status

Done

## Summary

Replace the plain Mission 01 room grid cards with a pixel-art style tile presentation while keeping the existing 3x3 mission model unchanged.

## Motivation

The current mission grid is readable, but it still looks like a layout prototype rather than part of a game. Mission 01 would benefit from a stronger visual identity that makes the room feel like a top-down playable space.

A small pixel-art presentation pass can improve atmosphere and readability without changing mission logic, simulation behavior, or the learner-facing rules.

## Related Documents

- `docs/spec/missions/mission-01-wake-the-core.md`
- `docs/spec/gameplay-spec.md`

## Scope

This task should cover:

- converting the Mission 01 room tiles into square game-style cells
- introducing a pixel-art floor treatment for ordinary tiles
- introducing a distinct docked CORE visual treatment
- introducing a distinct damaged relay visual treatment
- keeping supporting labels readable without letting them dominate the tile art

## Out Of Scope

- changing the mission layout or 3x3 logic
- introducing animation-heavy effects
- replacing the CSS prototype with a full sprite pipeline
- redesigning other panels on the page

## Acceptance Criteria

- [x] Mission 01 renders as a square tile grid rather than stretched rectangular cards.
- [x] Floor, CORE, and relay tiles are visually distinct in a pixel-art style.
- [x] The grid feels like a game board rather than a generic HTML layout.
- [x] Existing automated tests continue to pass.
