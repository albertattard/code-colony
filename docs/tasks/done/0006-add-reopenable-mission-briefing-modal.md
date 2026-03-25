# Task 0006: Add Reopenable Mission Briefing Modal

## Status

Done

## Summary

Add a mission-local briefing modal that opens when a mission page loads and can be reopened at any time through a visible `Briefing` action.

## Motivation

The game intro screen gives the player overall context, but it should not carry the burden of teaching each mission's immediate task. Mission-specific instructions need to stay close to the mission itself.

For beginner learners, a reopenable briefing reduces memory load. The player can read the objective, close the modal, try some code, and open the briefing again without leaving the mission screen or losing progress in the editor.

Mission 01 is the first use of this pattern. Its briefing should keep the learner focused on one small action: connecting to a docked CORE unit.

## Related Documents

- `docs/spec/gameplay-spec.md`
- `docs/spec/game-intro-spec.md`
- `docs/spec/missions/mission-01-wake-the-core.md`

## Scope

This task should cover:

- adding a mission briefing modal to the mission screen
- opening the modal by default when the mission page loads
- adding a persistent `Briefing` action that reopens the modal
- presenting mission title, local context, objective, and a small hint in the modal
- ensuring the modal can be closed without resetting the mission page
- applying the pattern to Mission 01
- adding automated test coverage for the modal's presence and reopen behavior

## Out Of Scope

This task should not include:

- replacing the game intro screen
- adding branching briefing choices
- full voice playback controls inside the mission modal
- a generalized mission progression system
- implementation of later mission briefings beyond the pattern needed for Mission 01

## Acceptance Criteria

- [x] Mission 01 opens with a briefing modal visible.
- [x] The modal shows the mission title, short context, objective, and a hint for `CORE.connect();`.
- [x] The player can close the modal and continue using the mission screen.
- [x] The mission screen shows a visible `Briefing` action that reopens the same modal.
- [x] Reopening the briefing does not clear the current editor contents.
- [x] Automated tests cover the briefing modal flow for Mission 01.

## Notes

The modal should support the lesson, not overwhelm it.

For Mission 01, the briefing should remain short enough that the player can read it once, close it, and start coding immediately.
