# Mission Definition Spec

## Purpose

Define a data-driven mission model where mission behavior is configured from mission files rather than mission-specific Java service classes.

## Scope

This spec defines:

- mission file set and responsibilities
- mission behavior configuration model
- generic mission runtime expectations
- mission discovery and registration expectations

This spec does not define:

- low-level sandbox/compiler internals
- visual redesign of mission UI panels

## Mission Directory Contract

Each mission must be represented by one directory:

- `content/missions/mission-##/`

Mission order, routing slug, and mission inclusion should be defined in:

- `content/missions/missions.yaml`

Required files:

- `content.md` for learner-facing narrative, hints, explain content, and initial run copy
- `map.yaml` for tiles/spawns/initial unit state
- `mission.yaml` for mission behavior configuration and completion rules

Goal: adding a new mission should primarily require adding one new directory with these files and no mission-specific Java classes.

## Mission Manifest Contract

`content/missions/missions.yaml` should define ordered mission entries.

Each entry should provide:

- `name`: URL-safe mission slug used by routing/presentation layers
- `content`: mission content directory id (for example `mission-01`)
- `enabled`: whether the mission is active (default `true`)

The mission runtime should treat manifest order as canonical mission order.
Disabled missions should be excluded from the active mission set.

### No-Code Mission Addition Rule

When a new mission uses already-supported mechanics and objective kinds, adding that mission must require only:

- one new directory under `content/missions/`
- `content.md`
- `map.yaml`
- `mission.yaml`

No mission-specific Java service, worker, simulator, validator, or controller wiring should be required for that case.

Java code changes are still required when introducing new mechanics or runtime concepts, for example:

- new dock/station types
- enemies or new active entity categories
- new learner actions or command semantics
- new objective/evaluation kinds not supported by existing runtime behavior

## File Responsibilities

### `map.yaml`

Owns:

- world layout
- legend and tile types
- entity spawns
- initial runtime state (battery, health, position, connection state)

Must not own:

- mission objective logic
- command surface rules
- progression rules

### `content.md`

Owns:

- summary/objective/briefing
- command reference copy
- mission hints
- initial run feedback copy
- explain headline and explain body

### `mission.yaml`

Owns behavior configuration:

- mission id and metadata (for routing and progression)
- allowed command set for the mission UI
- optional runtime command set (`allowedRuntimeCommands`) for simulation command gating; when omitted, runtime allows all supported commands
- objective definition (for example: connect once, battery target, health target, required position)
- execution-facing learner copy needed by runtime bootstrap (for example initial status note templates)
- validation-facing learner copy needed during runtime result evaluation (for example mission-specific runtime guidance text)
- mission validation outcome copy for supported branches (for example success and incomplete summary/feedback templates)
- validation-facing status panel note templates and mission-specific dock/position label rules
- validation-facing runtime retry hint shown after runtime failures
- per-mission action rules (for example overcharge behavior, charge/repair semantics if mission-specific)
- completion/unlock behavior (next mission id)

#### `runtime` Contract

`mission.yaml` may define a `runtime` section that declares how the generic execution pipeline should bootstrap and invoke mission execution.

Purpose:

- remove hardcoded objective-kind execution profile branching from Java
- keep runtime wiring declarative and mission-local

`runtime` fields:

- `worker`: symbolic worker id (for example `generic-mission-worker`)
- `simulator`: symbolic simulator id (for example `generic-mission-simulator`)
- `initialStatus`: initial status assembly settings
- `args`: ordered execution argument definitions passed to the worker

`initialStatus` fields:

- `mode`: required; one of:
- `withoutTelemetry`: starts from map spawn but hides battery/health telemetry
- `withTelemetry`: starts from map spawn and includes telemetry fields
- `state`: optional explicit initial state label (for example `Connected`)
- `position`: optional initial position template
- `noteTemplate`: required learner-facing note template

`args` contract:

- `args` is an ordered list, not a mapping
- argument order is source-of-truth and must be preserved exactly
- each entry must define:
- `name`: logical argument name
- `value`: template or literal string

Example:

```yaml
runtime:
  worker: generic-mission-worker
  simulator: generic-mission-simulator
  initialStatus:
    mode: withTelemetry
    state: Connected
    position: "{coreSpawn.at}"
    noteTemplate: "Move to {repairPosition} and repair structural damage."
  args:
    - name: objectiveKind
      value: "{objective.kind}"
    - name: validationPayload
      value: "{validationPayload.base64}"
    - name: startPosition
      value: "{coreSpawn.at}"
    - name: repairPosition
      value: "{map.firstByType:repair}"
```

Placeholder rules:

- placeholders use `{token}` format inside string values
- supported tokens are explicitly defined by runtime loader/execution docs (for example objective, validation payload, map lookups, spawn values)
- unresolved or unknown placeholders must fail fast with mission id + field path diagnostics
- map lookup placeholders (for example `{map.firstByType:repair}`) must fail fast when the requested type is missing
- placeholders are resolved before worker invocation; workers receive plain resolved strings only

Validation rules for `runtime`:

- unknown `worker`/`simulator` symbolic ids must fail fast
- `initialStatus.mode` must be one of the supported enum values
- `args` must be non-empty when `runtime` is present
- `args` entries must have non-blank `name` and `value`
- duplicate argument names are invalid unless explicitly documented as allowed
- all diagnostics must include mission id and failing section path (for example `runtime.args[2].value`)

## Runtime Model

Mission runtime should use mission-generic components:

- generic mission content loader
- generic mission map loader
- generic mission behavior loader (`mission.yaml`)
- generic mission service/execution pipeline that applies configured rules

Mission-specific wrappers should be treated as temporary compatibility bridges and removed once migration is complete.

## Mission Discovery

Mission availability should not require hardcoded mission-id lists where practical.

Preferred direction:

- discover missions from filesystem directories that satisfy the required file contract
- fail fast with clear diagnostics when required files are missing or invalid

## Validation Rules

Loader validation should fail fast when:

- required files are missing
- required sections/fields are missing
- rule references unknown commands/actions
- rule targets reference missing map entities/coordinates

Validation errors should be contributor-readable and include mission id and failing field/section.

## Migration Plan

Stage 1:

- introduce `mission.yaml` schema and loader
- keep existing mission Java behavior, but start reading selected config from `mission.yaml`

Stage 2:

- introduce generic objective evaluation driven by `mission.yaml`
- reduce mission-specific service/validator logic

Stage 3:

- replace mission-specific services with generic mission runtime
- remove mission-specific wrappers

## Non-Goals (Current Stage)

- supporting arbitrary scripting in mission files
- changing student API semantics
- changing map file format away from YAML
