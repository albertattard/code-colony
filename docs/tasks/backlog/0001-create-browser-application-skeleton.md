# Task 0001: Create Browser Application Skeleton

## Status

Planned

## Summary

Create the initial browser-based application skeleton for Code Colony using the stack defined by the current ADRs and specs.

## Motivation

The project now has enough product, learning, student API, and gameplay definition to justify a thin implementation shell. The goal of this task is to establish a runnable application structure that supports further iteration without prematurely implementing the full game engine or learner code execution pipeline.

## Related Documents

- `docs/spec/product-spec.md`
- `docs/spec/learning-spec.md`
- `docs/spec/student-api-spec.md`
- `docs/spec/gameplay-spec.md`
- `docs/adr/0001-choose-browser-based-java-stack.md`

## Scope

This task should cover:

- creating the initial Java 25 project structure
- setting up a Spring Boot application
- enabling Thymeleaf-based page rendering
- preparing HTMX-driven page interaction points
- creating a first placeholder mission page
- creating placeholder sections for:
  - mission briefing
  - available commands
  - code input
  - run action
  - simulation output
  - mission feedback

## Out Of Scope

This task should not include:

- actual learner code compilation or execution
- mission engine implementation
- persistence or database integration
- authentication or user accounts
- polished visuals beyond a usable skeleton

## Acceptance Criteria

- The project builds and runs locally.
- The application serves a browser page for the initial mission shell.
- The page reflects the gameplay layout defined in the gameplay spec.
- The page includes placeholders for all core mission interaction areas.
- The implementation remains thin and does not prematurely commit to engine internals.

## Notes

This task establishes the application shell only. Deeper execution and simulation concerns should be handled in later tasks and ADRs.
