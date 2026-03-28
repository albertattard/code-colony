# Task 0060: Remove Mission-Specific Page Services (Stage 4B Slice 2)

## Summary

Remove mission-specific page service classes and keep mission page assembly in one generic service.

## Why

Mission-specific page-service classes are still a blocker for mission-generic runtime. We already route controller wiring through `MissionPageFacade`, so we can remove the duplicated wrappers.

## In Scope

- move mission-01/02/03 page assembly logic into `MissionPageFacade`
- remove mission-specific page service classes
- preserve mission behavior and UI output

## Out Of Scope

- moving mission behavior rules into config
- removing mission-specific workers/simulators/validators
- changing mission text or mission mechanics

## Acceptance Criteria

- [x] `MissionPageFacade` builds mission pages for mission-01/02/03 directly.
- [x] `WakeTheCoreMissionService`, `ChargeTheCoreMissionService`, and `RepairTheCoreMissionService` are removed.
- [x] `./mvnw clean verify` passes.
