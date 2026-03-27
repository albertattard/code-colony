# Task 0029: Remove Legacy Grid Layout and Align Map Docs

## Summary

Remove the old shared text grid loader now that all current missions use mission-scoped YAML maps, and align mission specs with the `base + spawns` map model.

## Why

Keeping both the legacy text-grid path and mission map YAML path creates duplicate sources of truth and increases maintenance risk.

## In Scope

- removing `MissionGridLayout` and its resource/test
- adding cross-mission map loading coverage for mission maps
- updating mission specs to describe CORE start placement via map spawns

## Out Of Scope

- map schema changes
- mission behavior changes

## Relevant Specs

- `docs/spec/gameplay-spec.md`
- `docs/spec/missions/mission-01-wake-the-core.md`
- `docs/spec/missions/mission-02-charge-the-core.md`

## Acceptance Criteria

- [x] No production code references `MissionGridLayout` or `mission-grid-layout.txt`.
- [x] All current mission maps are covered by tests.
- [x] Mission specs reflect `base + spawns` map semantics.
- [x] `./mvnw clean verify` passes.
