# Task 0008: Add Mission Briefing Audio Playback

## Status

Done

## Summary

Add optional audio playback to the Mission 01 briefing modal so the player can listen to the same approved briefing text shown on screen.

## Motivation

Mission briefings are now stored as content-backed text and can be converted into voice assets. Exposing that audio in the briefing modal lets the player consume the same mission context in either written or spoken form without leaving the mission screen.

For beginner learners, the audio must remain secondary to the written text. The player should still be able to read, close, and replay the briefing without relying on autoplay or hidden information.

## Related Documents

- `docs/spec/missions/mission-01-wake-the-core.md`
- `docs/spec/gameplay-spec.md`
- `docs/adr/0003-store-player-facing-narrative-as-resource-content.md`

## Scope

This task should cover:

- exposing the Mission 01 briefing audio asset in the mission modal
- keeping the written briefing text visible beside or above the player
- adding automated coverage that the Mission 01 page includes the expected audio source

## Out Of Scope

This task should not include:

- autoplay behavior
- a generalized audio asset manifest
- audio support for every future mission

## Acceptance Criteria

- [x] The Mission 01 briefing modal includes an audio player.
- [x] The player can read the briefing without using the audio player.
- [x] The Mission 01 page references the expected mission briefing audio asset.
- [x] Automated tests cover the Mission 01 briefing audio presence.
