# Task 0009: Stop Briefing Audio When Closing Modal

## Status

Done

## Summary

Stop and reset Mission 01 briefing audio when the player closes the briefing modal.

## Motivation

Briefing audio should remain tied to the briefing surface. If the modal is dismissed while audio continues in the background, the interaction feels inconsistent and can distract the learner while they start coding.

Closing the briefing should therefore stop the current playback and leave reopening under explicit player control.

## Related Documents

- `docs/spec/gameplay-spec.md`
- `docs/spec/missions/mission-01-wake-the-core.md`

## Scope

This task should cover:

- pausing Mission 01 briefing audio when the modal closes
- resetting playback so reopening the modal does not continue from the previous position
- adding automated coverage for the close behavior

## Out Of Scope

- autoplay behavior
- audio support for additional missions
- audio settings or global mute controls

## Acceptance Criteria

- [x] Closing the Mission 01 briefing modal pauses briefing audio.
- [x] Closing the Mission 01 briefing modal resets playback to the start.
- [x] Reopening the modal does not resume playback automatically.
- [x] Automated tests cover the close behavior.
