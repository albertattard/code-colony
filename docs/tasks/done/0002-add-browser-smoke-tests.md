# Task 0002: Add Browser Smoke Tests

## Status

Done

## Summary

Add a minimal browser-level smoke test layer using Playwright for Java.

## Motivation

The current test suite verifies the Spring Boot shell and endpoint behavior, but it does not exercise the mission page through a real browser. A thin browser test will help catch layout and interaction regressions in the rendered UI while the skeleton is still evolving.

## Related Documents

- `docs/spec/gameplay-spec.md`
- `docs/spec/missions/mission-01-wake-the-core.md`

## Scope

This task should cover:

- adding Playwright for Java as a test dependency
- adding one browser smoke test for Mission 01
- verifying that the page renders and the Run flow updates the page

## Out Of Scope

This task should not include:

- a large browser test suite
- visual regression testing
- full mission engine validation

## Acceptance Criteria

- [x] The project can run a Playwright-based browser smoke test.
- [x] The smoke test opens Mission 01 in a real browser engine.
- [x] The smoke test verifies the page layout and the Run interaction at a high level.
- [x] The full test suite passes.

## Verification

- `mvn -Dmaven.repo.local=/tmp/code-colony-m2 test`
