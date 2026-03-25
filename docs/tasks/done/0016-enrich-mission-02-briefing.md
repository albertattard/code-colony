# Task 0016: Enrich Mission 02 Briefing

## Summary

Expand the Mission 02 briefing text, add voiced playback for it, and show the audio player inside the Mission 02 briefing dialog.

## Why

Mission 02 is the first mission that asks the learner to build on earlier code. The briefing should acknowledge the player's progress, explain the next step clearly, and offer the same audio support already available in Mission 01.

## In Scope

- expanding the Mission 02 briefing content
- updating the Mission 02 spec to reflect optional voiced playback
- generating the Mission 02 audio file from the briefing Markdown
- showing the audio player in the Mission 02 briefing dialog
- updating automated tests that cover Mission 02 briefing content

## Out Of Scope

- changing Mission 02 gameplay rules
- changing the Mission 02 success conditions

## Relevant Specs

- `docs/spec/missions/mission-02-charge-the-core.md`

## Acceptance Criteria

- [x] The Mission 02 briefing congratulates the learner for connecting to CORE-01.
- [x] The Mission 02 briefing explains how to achieve the charging objective.
- [x] Mission 02 has a generated audio briefing file under `src/main/resources/static/audio/briefings/`.
- [x] The Mission 02 briefing dialog shows an audio player, consistent with Mission 01.
- [x] Automated tests cover the updated briefing content and audio path.
