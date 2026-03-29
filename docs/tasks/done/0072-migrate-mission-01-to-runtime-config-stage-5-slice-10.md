# Task 0072: Migrate mission-01 To Runtime Config (Stage 5 Slice 10)

## Summary

Migrate `mission-01` to use `mission.yaml.runtime` so execution wiring for this mission is fully data-driven.

## Why

After introducing dual-path execution, each mission can migrate incrementally to runtime config. This slice proves the path with mission 01 while preserving behavior.

## Spec Impact

No spec update required in this slice. This is an implementation migration aligned with the existing runtime contract.

## In Scope

- add runtime block to `content/missions/mission-01/mission.yaml`
- preserve mission-01 behavior and outcomes
- update tests that assert mission-01 loader/runtime shape

## Out Of Scope

- migrating mission-02 or mission-03
- removing objective-profile fallback

## Acceptance Criteria

- [x] mission-01 executes via runtime config.
- [x] mission-01 learner-visible behavior is unchanged.
- [x] `./mvnw clean verify` passes.
