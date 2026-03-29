# Task 0074: Migrate mission-03 To Runtime Config (Stage 5 Slice 12)

## Summary

Migrate `mission-03` to use `mission.yaml.runtime` so all current missions execute through runtime configuration.

## Why

This completes mission-by-mission runtime migration for missions 01-03 and prepares the final bridge-removal slice.

## Spec Impact

No spec update required in this slice. Implementation follows the existing runtime contract.

## In Scope

- add runtime block to `content/missions/mission-03/mission.yaml`
- preserve mission-03 learner-visible behavior and outcomes
- update tests that currently assume mission-03 is the fallback path

## Out Of Scope

- removing objective-profile fallback from Java

## Acceptance Criteria

- [x] mission-03 executes via runtime config.
- [x] missions 01-03 are all runtime-driven.
- [x] `./mvnw clean verify` passes.
