# Task 0073: Migrate mission-02 To Runtime Config (Stage 5 Slice 11)

## Summary

Migrate `mission-02` to use `mission.yaml.runtime` so this mission no longer relies on objective-profile fallback wiring.

## Why

After mission-01 migration, mission-02 is the next incremental step toward fully data-driven runtime wiring.

## Spec Impact

No spec update required in this slice. Implementation follows existing runtime contract.

## In Scope

- add runtime block to `content/missions/mission-02/mission.yaml`
- preserve mission-02 learner-visible behavior and results
- update tests only if assertions depend on mission-02 runtime shape

## Out Of Scope

- migrating mission-03
- removing objective-profile fallback

## Acceptance Criteria

- [x] mission-02 executes via runtime config.
- [x] mission-02 learner-visible behavior is unchanged.
- [x] `./mvnw clean verify` passes.
