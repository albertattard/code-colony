# Task 0050: Mission Runtime Full Data-Driven Migration

## Summary

Migrate mission runtime so adding a new mission normally requires only creating `content/missions/<mission-id>/` files and no Java code changes.

## Target Outcome

For a new mission (for example `mission-04`), contributors should only add:

- `content/missions/mission-04/content.md`
- `content/missions/mission-04/map.yaml`
- `content/missions/mission-04/mission.yaml`

No mission-specific Java service/worker/simulator/validator classes should be required unless introducing new mechanics (for example new dock types, enemies, or new actions).

## Spec Impact

This is a multi-stage behavior and architecture migration.

Before each implementation stage:

- update relevant specs under `docs/spec/`
- create a stage task under `docs/tasks/in-progress/`
- keep this epic updated with progress and links

## Related Specs And ADRs

- `docs/spec/mission-definition-spec.md`
- `docs/spec/gameplay-spec.md`
- `docs/adr/0004-adopt-data-driven-mission-definition.md`
- `docs/adr/0005-store-runtime-content-in-working-directory.md`

## Staged Plan

### Stage 0: Lock Contract

- define and confirm mission directory contract and runtime expectations
- document what still requires code changes (new mechanics only)

Success criteria:

- specs explicitly describe no-code mission addition path
- scope boundaries are clear for mechanics vs mission configuration

### Stage 1: Dynamic Mission Discovery

- replace hardcoded mission lists with filesystem discovery from `content/missions/`
- fail fast when required mission files are missing

Success criteria:

- mission ids are discovered dynamically
- no hardcoded `mission-01..03` catalog required for normal mission loading

### Stage 2: Generic Mission Page Service

- introduce one generic mission page/content service
- remove mission-specific page assembly logic from controller wiring

Success criteria:

- mission pages are resolved by mission id through generic service
- mission-specific page service duplication is removed

### Stage 3: Generic Execution Pipeline (Compatibility Stage)

- implement one generic execution path that reads map + behavior config
- support existing objective kinds used by missions 1-3
- keep temporary compatibility bridges only where needed

Success criteria:

- missions 1-3 run via generic execution path
- browser and integration tests remain green

### Stage 4: Remove Mission-Specific Runtime Classes

- remove mission-specific service/execution/worker/simulator/validator classes
- keep only mission-generic runtime components

Success criteria:

- no per-mission runtime classes are required for missions 1-3
- codebase has one runtime pipeline for mission execution

### Stage 5: Config-Complete Mission Behavior

- move remaining behavior knobs into `mission.yaml`
- validate config with clear diagnostics

Success criteria:

- per-mission behavior differences are represented in config
- adding a mission with known mechanics requires no Java changes

Current next slices (capture before refactor):

1. Spec update: add `runtime` contract to `mission.yaml` (`worker`, `simulator`, `initialStatus`, `args`, placeholder rules).
2. Loader update: parse and validate `runtime` config without changing execution behavior yet.
3. Dual-path execution: `MissionExecutionFacade` prefers YAML `runtime`; fallback to hardcoded objective profile map when missing.
4. Incremental migration: move mission-01 runtime profile to YAML and verify.
5. Incremental migration: move mission-02 runtime profile to YAML and verify.
6. Incremental migration: move mission-03 runtime profile to YAML and verify.
7. Bridge removal: delete hardcoded `PROFILE_BY_OBJECTIVE_KIND` after all active missions have YAML runtime profiles.

Checkpoint criteria for this sequence:

- each slice has its own task in `docs/tasks/in-progress/`
- each slice runs `./mvnw clean verify`
- each slice preserves mission behavior (no learner-visible regression unless explicitly planned)

### Stage 6: Prove No-Code Mission Addition

- add a new mission directory (for example mission-04) with no Java changes
- ensure mission is discoverable, playable, and test-covered

Success criteria:

- mission-04 works end-to-end using only files under `content/missions/mission-04/`
- no runtime code changes required for that mission

## Risks

- regression risk in mission progression and completion behavior
- parser/validation gaps may produce unclear contributor errors
- migration may temporarily duplicate logic if compatibility bridges are not removed promptly

## Mitigations

- keep one stage per task and one focused behavior change per commit
- run `./mvnw clean verify` at each stage
- add regression tests as each stage lands
- create explicit follow-up tasks for any temporary bridge

## Progress Tracking

- [x] Stage 0 complete
- [x] Stage 1 complete
- [x] Stage 2 complete
- [x] Stage 3 complete
- [ ] Stage 4 complete
- [ ] Stage 5 complete
- [ ] Stage 6 complete
