# Task 0047: Introduce Data-Driven Mission Runtime (Stage 1)

## Summary

Start the transition to a data-driven mission runtime by introducing `mission.yaml` and a mission behavior loader while keeping current mission behavior stable.

## Links

- Spec: `docs/spec/mission-definition-spec.md`
- ADR: `docs/adr/0004-adopt-data-driven-mission-definition.md`

## Why

Mission behavior should be authored through mission files so adding new missions does not require new mission-specific Java class stacks.

## In Scope (Stage 1)

- define initial `mission.yaml` schema for mission-01 to mission-03
- add loader + validation for `mission.yaml`
- load mission behavior config without changing mission outcome semantics
- wire mission ids to behavior config through a generic access path

## Out Of Scope (Stage 1)

- removing mission-specific services/execution services
- replacing validators with a fully generic objective engine

## Acceptance Criteria

- [x] Each current mission has a valid `mission.yaml`.
- [x] Mission behavior loader validates required fields and fails fast with clear diagnostics.
- [x] Existing mission behavior remains unchanged.
- [x] `./mvnw clean verify` passes.
