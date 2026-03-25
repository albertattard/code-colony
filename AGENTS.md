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
- Mark assumptions explicitly.
- Separate confirmed decisions from open questions.
- Update the relevant spec in the same change as any behavior-changing implementation.

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
- Link each task to the relevant spec documents when applicable.
- Do not start meaningful implementation work until the task is clear enough to execute.
- Update task files as work progresses rather than treating them as disposable notes.

## Trigger Phrases

### `commit changes`

When asked to `commit changes`, use this workflow:

1. Update the related task files and move completed tasks to `docs/tasks/done/` when applicable.
2. Compile the project and run tests when the repository contains code.
   If the build or tests fail, stop and fix the issue before committing unless the user explicitly approves committing a known failing state.
3. Prepare a two-part commit message:
   - a short subject starting with a present-tense verb
   - a longer body describing the business reason for the change
4. Commit only the files related to the current task.
5. Exclude unrelated pending changes from the commit.
6. Show the commit message.
7. Ask for confirmation before pushing upstream.

### `build`

When asked to `build`, use this workflow:

1. Build and verify the project with `./mvnw clean verify`.
2. Start the application with `./mvnw spring-boot:run` as a long-running process.

## Implementation Rules

- Do not expose engine complexity to learners unless the lesson requires it.
- Prefer deterministic mission behavior where possible.
- Build for observability: logs, turn summaries, and visible state changes should help learners debug their code.
- Write tests around engine behavior and mission rules, especially where student code interacts with the system.
- Tag browser end-to-end tests with `@Tag("e2e")`.
- Keep `mvn test` focused on fast unit and integration feedback by excluding `e2e` tests.
- Run browser end-to-end tests with `mvn verify`.
- Prefer `final` for variables, parameters, and fields unless mutation is required.

## Code Standards

- Keep methods small and focused on one responsibility.
- Prefer clear domain names over generic names such as `data`, `manager`, or `util`.
- Keep web controllers thin and move game logic out of the web layer.
- Prefer composition over deep inheritance.
- Prefer immutable data and value objects where practical.
- Treat error messages and feedback text as part of the product, not incidental implementation detail.

## Review Rules

Changes should be reviewed against both product and teaching goals:

- Does this make the learner experience clearer or more confusing?
- Does this preserve a small, stable student-facing API?
- Does this introduce unnecessary technical complexity?
- Does this fit the current specs?

If the answer to any of these is unclear, stop and update the specs before continuing.
