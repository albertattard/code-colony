# Task 0030: Add Mission Map Gameplay Validations

## Summary

Add mission-level validation rules on top of map schema validation so map configuration errors fail fast with clear diagnostics.

## Why

Schema validation ensures structural correctness, but missions also rely on gameplay-required map invariants. Encoding those checks in the loader prevents runtime failures caused by incomplete map configuration.

## In Scope

- enforcing exactly one `core_01` core spawn for current missions
- enforcing required station tile types (`dock`, `repair`) for current missions
- adding tests for these mission-level validation rules
- documenting mission-level map validation in gameplay spec

## Out Of Scope

- changing map schema fields
- mission behavior changes
- new mission content

## Relevant Specs

- `docs/spec/gameplay-spec.md`

## Acceptance Criteria

- [x] Mission map loading fails when `core_01` spawn is missing or duplicated.
- [x] Mission map loading fails when required `dock`/`repair` tile types are missing.
- [x] Diagnostics identify the invalid mission map and missing requirement.
- [x] Automated tests cover these mission-level validation failures.
- [x] `./mvnw clean verify` passes.
