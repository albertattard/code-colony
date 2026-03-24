# Code Colony Agent Guide

## Purpose

Code Colony is a Java-first programming strategy game for young learners. The project has two equal goals:

1. Build a compelling game about restoring a silent off-world colony.
2. Teach Java through mission-driven play, with code as the primary input mechanism.

All project decisions should protect both goals. If a choice improves the game but makes the learning experience worse, or improves the lesson but makes the game dull, revisit the design.

## Working Style

This project uses iterative spec-driven development.

1. Write or update the relevant spec before implementation.
2. Create or update the relevant task in `docs/tasks/`.
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

- `docs/product-spec.md`
- `docs/learning-spec.md`

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

- Use task files to describe concrete units of work.
- Link each task to the relevant spec documents when applicable.
- Do not start meaningful implementation work until the task is clear enough to execute.
- Update task files as work progresses rather than treating them as disposable notes.

## Trigger Phrases

### `commit changes`

When asked to `commit changes`, use this workflow:

1. Compile the project and run tests when the repository contains code.
2. Prepare a two-part commit message:
   - a short subject starting with a present-tense verb
   - a longer body describing the business reason for the change
3. Commit only the files related to the current task.
4. Exclude unrelated pending changes from the commit.
5. Show the commit message.
6. Ask for confirmation before pushing upstream.

## Implementation Rules

- Do not expose engine complexity to learners unless the lesson requires it.
- Prefer deterministic mission behavior where possible.
- Build for observability: logs, turn summaries, and visible state changes should help learners debug their code.
- Write tests around engine behavior and mission rules, especially where student code interacts with the system.

## Review Rules

Changes should be reviewed against both product and teaching goals:

- Does this make the learner experience clearer or more confusing?
- Does this preserve a small, stable student-facing API?
- Does this introduce unnecessary technical complexity?
- Does this fit the current specs?

If the answer to any of these is unclear, stop and update the specs before continuing.
