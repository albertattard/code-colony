# Product Spec

## Working Title

Code Colony

## Product Summary

Code Colony is a programming-driven strategy game in which the player acts as a remote engineer investigating a silent off-world colony. The player restores systems and uncovers the colony's fate by programming CORE maintenance units in Java.

The game is designed first as a teaching tool for young learners who are curious about programming, using mission outcomes to make Java concepts concrete.

The intended player experience is fully browser-based. Players do not install the game, use an IDE, or manage a local Java setup. They write Java code inside the game interface and run it directly from the browser.

## Product Goals

The initial product should:

1. Make coding feel like commanding machines in a real world.
2. Teach Java through short, structured missions.
3. Give immediate feedback when student code succeeds or fails.
4. Create enough narrative tension to motivate continued play.
5. Be practical to run in a classroom or workshop environment.

## Non-Goals For The First Version

The first version should not aim to:

- Deliver a large open world
- Support multiplayer
- Provide advanced graphics or animation
- Teach all of Java
- Include free-form modding or unrestricted scripting

## Target Users

Primary users:

- Kids and early teens interested in programming
- Learners who are new to Java or have only minimal prior exposure

Secondary users:

- Teachers, workshop leaders, and parents who want a structured introduction to Java

## Core Product Pillars

### 1. Code Drives Action

Player progress should come from writing or modifying Java code, not from reflexes or complex menus.

### 2. Small Missions, Clear Wins

Each mission should have a focused objective, a visible environment, and understandable success conditions.

### 3. Feedback Must Be Immediate

Learners should be able to run their code quickly, observe what their CORE unit did, and try again.

### 4. Mystery Supports Motivation

The colony's story should provide context and momentum, but never obscure what the learner is expected to do next.

### 5. Classroom-Friendly Delivery

Setup, reset, and replay should be simple enough for guided sessions.

Browser access should be enough for a learner to start playing.

## MVP Definition

The first playable milestone should include:

- A browser-based game interface
- A built-in code entry area with a terminal-like feel
- One programmable CORE unit
- One mission with a clear objective
- A small learner-editable Java code surface
- Simulation output that explains robot actions and mission result

If this vertical slice is not engaging and understandable, the project should improve it before expanding scope.

## Core Loop

The intended player loop is:

1. Read the mission brief
2. Inspect the available commands and current code area
3. Enter or modify Java code for the CORE unit
4. Click run
5. Observe the robot's behavior on screen
6. Read the resulting feedback and mission outcome
7. Revise the code and try again
8. Unlock the next piece of the story

## Player Fantasy

The player should feel like:

- a remote engineer
- working with incomplete system access
- issuing precise instructions to repair critical systems
- uncovering the fate of a failed colony through disciplined technical work

## Tone

The tone should be:

- mysterious
- focused
- optimistic rather than horror-driven
- technical without becoming cold or inaccessible

## Delivery Strategy

Recommended phased approach:

1. Validate the browser-based coding loop and mission feedback flow
2. Build a small multi-mission educational prototype
3. Expand the interface and presentation once the student API and mission design are stable

## Success Criteria

The MVP is successful if:

- A beginner can understand what code they are meant to enter or edit
- A learner can see how their Java changes affect robot behavior
- The mission can be solved through reasoning, not guesswork
- The story provides motivation to continue
- A teacher can run the experience with low setup overhead
- A learner can play through a browser without using an IDE or local Java installation

## Constraints

- The learner-facing programming surface must stay small.
- The browser experience must hide local build and environment complexity from the learner.
- Mission complexity must not outpace the learner's Java knowledge.
- Engine design must allow additional missions without rewriting the student API.

## Interaction Model

The game should present a coding interface that feels like a guided terminal or command console.

Within that interface, the learner should be able to:

- read the current mission objective
- enter or edit small Java code snippets
- run the code with a clear action button
- watch the resulting robot actions play out on screen
- receive feedback explaining success, failure, or mistakes

The learner should not need to think about compilation, project setup, files, or IDE workflows.

## Delivery Model

The target product is a browser-based game.

The implementation should support:

- browser-based code entry
- browser-based mission and simulation display
- Java-based execution of learner code behind the scenes
- immediate feedback after each run

How the backend executes learner code is an architectural concern to be defined separately, but the player-facing experience should remain simple and self-contained in the browser.

## Open Questions

- Exact target age range
- Whether the first classroom format is self-paced or instructor-led
- How the in-browser code editor should balance simplicity against realism
- How much narrative text is appropriate per mission
- Whether early missions should use snippets, templates, or a fuller editable class view
