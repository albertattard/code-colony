# ADR 0004: Adopt Data-Driven Mission Definition

## Status

Proposed

## Context

Current missions are still represented by mission-specific Java services and execution wiring, even though map and learner-facing text are already file-based.

This increases the amount of code required to add a mission and risks duplication across mission implementations.

The product direction is to make mission authoring primarily content/config driven, so adding a mission should be mostly creating a mission directory and files.

## Decision

Adopt a mission definition model based on three mission files:

- `content.md`
- `map.yaml`
- `mission.yaml`

Use mission-generic runtime components to load these files and execute mission behavior.

Treat mission-specific Java service/execution/validator classes as transitional adapters to be removed through staged migration.

## Consequences

Adding new missions becomes faster and less code-heavy.

Mission behavior becomes more consistent and easier to validate centrally.

The project must define and maintain a `mission.yaml` schema and robust validation.

Migration will require staged refactoring to avoid breaking existing missions and tests.
