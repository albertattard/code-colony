# Code Colony Agent Guide

## Purpose

Code Colony is a Java-first programming strategy game for young learners. The project has two equal goals:

1. Build a compelling game about restoring a silent off-world colony.
2. Teach Java through mission-driven play, with code as the primary input mechanism.

All project decisions should protect both goals. If a choice improves the game but makes the learning experience worse, or improves the lesson but makes the game dull, revisit the design.

## Working Style

This project uses iterative spec-driven development.

1. Write or update the relevant spec before implementation.
2. Create or update the relevant task in `docs/tasks/backlog/`, `docs/tasks/in-progress/`, or `docs/tasks/done/`.
3. Build a small vertical slice.
4. Test whether the slice is fun, understandable, and technically sound.
5. Refine the spec and task based on what was learned.

Avoid speculative implementation. Prefer concrete, testable scope over broad frameworks.

## Product Priorities

When tradeoffs appear, use this order:

1. Clarity for beginner learners
2. Fast feedback from running code
3. Small, understandable mission scope
4. Stable student-facing APIs
5. Expandable game architecture
6. Visual polish

## Audience Assumptions

Current working assumptions:

- Learners are kids or early teens interested in programming.
- The initial version should support beginners with little or no Java experience.
- Missions should introduce one or two ideas at a time.

If later specs refine the age group or prerequisite knowledge, update this file and the learning spec together.

## Project Boundaries

The codebase should separate three concerns clearly:

- Engine code: simulation, world rules, validation, mission execution
- Mission code: objectives, maps, scripted events, story content
- Student code: the limited surface learners edit to control a CORE unit

Student-facing code must remain small, readable, and safe to change without requiring knowledge of the engine internals.

## Technical Direction

Until a later spec says otherwise:

- Use Java as both the implementation language and the learner language.
- Build for a browser-based player experience.
- Prefer backend-rendered web delivery when it keeps the stack simpler.
- Keep external dependencies light.
- Favor simple build and run steps for contributors and simple browser access for learners.

## Educational Rules

- One mission should focus on one main concept, with at most one supporting concept.
- Avoid introducing abstractions before the learner needs them.
- Examples should use clear names and short methods.
- Failure states should teach. Error messages and simulation output should explain what happened and why.
- Mission success should depend on code behavior the learner can understand from the available lesson material.

## Spec Set

Current spec documents:

- `docs/spec/README.md`

Before implementing a feature, identify which existing spec governs it. If none exists, write the missing spec first unless the change is trivial.

## Documentation Rules

- Keep specs concise and decision-oriented.
- Treat approved specs as the source of truth for established product, gameplay, and learner-facing behavior.
- Mark assumptions explicitly.
- Separate confirmed decisions from open questions.
- If behavior is discovered or clarified during development, update the relevant spec before treating that behavior as established.
- Do not leave mission progression, carried learner state, or learner-facing rules implicit in code when they affect gameplay or teaching.
- Update the relevant spec in the same change as any behavior-changing implementation.
- If implementation and spec differ, treat the implementation as wrong until the spec is intentionally updated.
- Before proposing implementation steps for non-trivial behavior changes, start with a `Spec impact` check and state explicitly whether a spec update is required.
- If a spec update is required, propose or apply that spec change first, then outline implementation steps aligned to the updated spec.

## Architectural Decisions

Architectural and technical decisions should be recorded as ADRs under `docs/adr/`.

- Create a new ADR when making a meaningful technical, architectural, or workflow decision.
- Use Michael Nygard's ADR format with these sections: `Status`, `Context`, `Decision`, and `Consequences`.
- Keep ADRs short and focused on the decision, its context, and its consequences.
- Prefer immutable ADRs: supersede an older ADR with a new one instead of rewriting history.
- Link implementation work and tasks back to the relevant ADRs when applicable.

## Task Tracking

Work should be tracked with Markdown task files under `docs/tasks/`.

Task directories:

- `docs/tasks/in-progress/` for work currently being executed
- `docs/tasks/backlog/` for planned work that is not started yet
- `docs/tasks/done/` for completed tasks kept as project history

- Use task files to describe concrete units of work.
- Link each task to the relevant spec documents. If no spec exists, create or update the spec before starting the task unless the change is trivial.
- Do not start meaningful implementation work until the task is clear enough to execute.
- Update task files as work progresses rather than treating them as disposable notes.
- Each task should define the expected behavior or outcome clearly enough that it can be verified after implementation.

## Trigger Phrases

### `commit changes`

When asked to `commit changes`, use this workflow:

1. Update the related task files and move completed tasks to `docs/tasks/done/` when applicable.
2. Run verification appropriate to the scope of change.
   For code changes, run `./mvnw clean verify`.
   For docs-only or content-only changes that do not affect executable behavior, run a lighter validation pass if practical and note what was checked.
   If required verification fails, stop and fix the issue before committing unless the user explicitly approves committing a known failing state.
3. Prepare a two-part commit message:
   - a short subject starting with a present-tense verb
   - a longer body describing the business reason for the change
4. Commit only the files related to the current task.
5. Exclude unrelated pending changes from the commit.
6. Show the commit message.
7. Push upstream automatically after committing.
   In sandboxed environments, request elevated permission before running `git push` when required by the sandbox.

### `build`

When asked to `build`, use this workflow:

1. Build and verify the project with `./mvnw clean verify`.
2. Start the application with `./mvnw spring-boot:run` only when the user wants a running local instance.
   Treat this as an interactive, long-running process and do not leave it running unless requested.

### `generate audio for <source>`

When asked to `generate audio for <source>`, use this workflow:

1. Resolve `<source>` to a player-facing Markdown briefing under `src/main/resources/content/`.
2. Invoke `tools/tts.sh` with that Markdown file as input.
3. Save the generated audio under `src/main/resources/static/audio/briefings/` using a sensible default file name derived from the source.
   If the user explicitly provides an output path, use that instead.
4. Report which source file was used and where the audio file was saved.

Examples:

- `generate audio for intro`
- `generate audio for mission-01`

## Implementation Rules

- Do not expose engine complexity to learners unless the lesson requires it.
- Prefer deterministic mission behavior where possible.
- Build for observability: logs, turn summaries, and visible state changes should help learners debug their code.
- Write tests around engine behavior and mission rules, especially where student code interacts with the system.
- Tag browser end-to-end tests with `@Tag("e2e")`.
- Keep `./mvnw test` focused on fast unit and integration feedback by excluding `e2e` tests.
- Run browser end-to-end tests with `./mvnw verify`.
- As part of the normal change flow, run `./mvnw clean verify` before finalizing work.
- In sandboxed environments, end-to-end tests that start an embedded server may require elevated permissions for port binding.

## Code Standards

- Keep methods small and focused on one responsibility.
- Prefer clear domain names over generic names such as `data`, `manager`, or `util`.
- Keep web controllers thin and move game logic out of the web layer.
- Prefer composition over deep inheritance.
- Prefer immutable data and value objects where practical.
- Treat error messages and feedback text as part of the product, not incidental implementation detail.
- Prefer `final` for variables, parameters, and fields unless mutation is required.
- For intentionally unused exception variables in catch blocks, use `_` instead of names like `ignored` (for example `catch (IOException _)`).

## Refactoring Heuristics

Prefer small, behavior-preserving refactors over large rewrites.

Default expectation: one refactor stage per commit unless the user asks otherwise.

### Value Objects

When two or more fields repeatedly represent the same concept shape (for example `xLevel`/`xCapacity` pairs such as battery, health, shield), prefer introducing a dedicated value object rather than duplicating primitives.

Examples:

- `batteryLevel` + `batteryCapacity`
- `healthLevel` + `healthCapacity`

Preferred approach:

1. Create a small immutable value object (for example `StatusMeter`).
2. Move validation/invariants into that object (`capacity > 0`, `0 <= level <= capacity`).
3. Use it in records/DTOs instead of repeated primitive pairs.
4. Keep names domain-oriented and beginner-readable.

### Incremental Migration

When refactoring, use staged migrations:

1. Introduce the new structure with temporary compatibility bridges when needed.
2. Migrate call sites in small, verifiable batches.
3. Remove temporary bridges only after migrations are complete and tests pass.

This applies broadly (package moves, naming cleanups, controller/service reshaping, API surface tightening), not only model extraction.

When introducing a new model/type, treat it as a specific case of staged migration:

1. Introduce the new type and keep compatibility bridges (for example overloaded constructors/getters).
2. Migrate call sites incrementally.
3. Remove compatibility bridges in a dedicated follow-up step.

### Duplication And Consolidation

During implementation and review, actively look for near-identical classes or functions across missions or layers.

Trigger pattern:

- same control-flow structure
- same error handling and I/O flow
- differences mostly limited to type names, mission IDs, or small constants

When this pattern is found:

1. Call it out explicitly in the review or summary.
2. Propose an incremental consolidation plan.
3. Prefer extracting shared logic into a reusable component and keeping thin mission-specific wrappers.

Default expectation: when duplication is detected, propose consolidation before adding new duplicate code.

## Review Rules

Changes should be reviewed against both product and teaching goals:

- Does this make the learner experience clearer or more confusing?
- Does this preserve a small, stable student-facing API?
- Does this introduce unnecessary technical complexity?
- Does this fit the current specs?
- If implementation and spec differ, require either a spec update or a rollback before approving.
- All automated tests must pass before approving a change unless a failing state is explicitly agreed upon.

For each changed model/record:

- Check for repeated primitive clusters.
- If found, either extract a value object or document why extraction is deferred.

For staged refactors:

- Confirm the current stage goal is small and isolated.
- Verify behavior is unchanged for that stage.
- If compatibility bridges remain, create a follow-up task to remove them.
- Avoid big-bang cleanup in the same change unless explicitly requested.

For changed areas:

- Check whether two or more classes have substantially similar structure.
- Check whether differences can be represented as parameters, strategy, or lambda.
- If yes, either extract shared logic in the current change or create a follow-up task and document why extraction is deferred.

If the answer to any of these is unclear, stop and update the specs before continuing.
