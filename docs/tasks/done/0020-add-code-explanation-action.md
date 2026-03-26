# Task 0020: Add Code Explanation Action

## Summary

Add an `Explain` action that helps learners understand the mission-target Java code shape and the concepts behind it, without running the simulation.

## Why

Learners can get stuck even when they are close. They need lightweight guidance focused on understanding the mission-required Java concepts, not just mission pass/fail outcomes.

## In Scope

- updating gameplay spec to define the `Explain` interaction
- adding an `Explain` action to the mission code panel
- adding mission endpoints that provide guided mission-code explanations without running simulation actions
- showing explanation output in the feedback area
- keeping explanation text solution-safe for beginner missions
- adding automated tests for the explain action flow

## Out Of Scope

- generating full solved mission code
- auto-editing learner code
- LLM-backed or external API explanation generation

## Relevant Specs

- `docs/spec/gameplay-spec.md`
- `docs/spec/missions/mission-01-wake-the-core.md`
- `docs/spec/missions/mission-02-charge-the-core.md`

## Acceptance Criteria

- [x] The code panel shows an `Explain` action for active mission editing.
- [x] Explaining code does not execute mission simulation.
- [x] The feedback area can show guided explanation for the mission-target code shape.
- [x] Explanation output is grounded in mission context and beginner-level Java concepts.
- [x] Automated tests cover explain endpoint behavior.
