# Task 0039: Add Intro Interface Orientation Step

## Summary

Add an explicit interface orientation segment to the intro experience, including a simple run-loop diagram and clear wording that each Run starts from mission start state.

## Why

Players understand the mission narrative, but the interaction loop and UI panel roles are not yet communicated as explicitly as they should be for beginners.

## In Scope

- add a second intro segment for interface orientation (same page flow)
- include old-interface framing and panel purpose explanation
- include a simple `Write Code -> Run Program -> Objective met?` loop diagram
- explicitly communicate fresh-state-per-run behavior

## Out Of Scope

- changing mission mechanics
- adding a separate intro route/page

## Related Spec

- `docs/spec/game-intro-spec.md`
- `docs/spec/gameplay-spec.md`

## Acceptance Criteria

- [ ] Intro experience includes an interface orientation segment before Mission 01.
- [ ] Orientation includes loop guidance and explicitly states each Run starts fresh.
- [ ] Existing intro and browser smoke tests are updated to reflect the new intro content.
- [ ] `./mvnw clean verify` passes.
