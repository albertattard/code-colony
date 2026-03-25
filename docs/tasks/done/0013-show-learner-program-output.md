# Task 0013: Show Learner Program Output

## Summary

Add a dedicated learner program output panel that shows `stdout` and `stderr` separately from mission feedback.

## Why

Learners should be free to experiment with normal Java output such as `System.out.println(...)` without confusing that output with simulator events or mission results. Showing program output explicitly keeps the game rules clear while still rewarding exploration.

## In Scope

- updating the gameplay spec to define learner program output behavior
- updating the Mission 01 spec to allow harmless extra output
- defining a dedicated `Program Output` panel for `stdout` and `stderr`
- stating how empty and large outputs should be handled

## Out Of Scope

- changing mission success rules beyond allowing harmless extra output
- adding input APIs such as `Scanner`

## Relevant Specs

- `docs/spec/gameplay-spec.md`
- `docs/spec/missions/mission-01-wake-the-core.md`

## Acceptance Criteria

- [x] The specs state that learner `stdout` and `stderr` are shown separately from mission feedback.
- [x] The specs state that harmless extra output does not fail Mission 01 when the objective is still met.
- [x] The specs define a dedicated `Program Output` panel and when it should be shown.
