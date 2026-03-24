# Student API Spec

## Purpose

This document defines the Java surface that learners interact with in Code Colony.

The student API must make coding feel meaningful and game-driven while remaining small enough for beginners to understand. It should hide engine complexity, support the learning progression, and work cleanly in a browser-based coding experience.

## Scope

This spec defines:

- what the learner writes
- what objects and methods are exposed to the learner
- what is hidden from the learner
- how code is run
- what feedback the learner receives

This spec does not define the backend sandbox implementation in detail.

## Design Goals

The student API should:

1. feel like programming a robot
2. keep the first missions accessible to beginners
3. support gradual introduction of Java concepts
4. provide a stable learner-facing model across missions
5. map code changes clearly to visible in-game behavior

## Core Decision

For the MVP, the learner will edit a small Java code snippet inside a provided execution context.

The learner will not manage:

- packages
- imports
- class declarations
- project files
- build configuration

The game will provide the surrounding structure behind the scenes and compile the learner's code as part of a predefined template.

## Learner Editing Model

The default learner experience should expose the body of a predefined method.

The learner should see something conceptually similar to:

```java
void run() {
    // learner writes code here
}
```

In early missions, the browser may show only the editable statements rather than the full surrounding class.

This keeps the experience focused on behavior rather than Java project structure.

## Execution Context

When the learner runs their code, the system should execute it in a predefined context that gives access to a robot control object.

For the MVP, the learner-facing model should assume a single pre-provided object:

```java
core
```

This object represents the current CORE unit and exposes the allowed mission actions and environment checks.

## API Design Principles

- Prefer clear, descriptive method names.
- Prefer explicit actions over compact abstractions in early missions.
- Expose only the methods needed for the current mission and previously learned concepts.
- Keep return types simple and readable.
- Avoid requiring the learner to understand engine types too early.

## Learner Capabilities

The student API should support these categories of interaction.

### 1. Actions

These methods cause the CORE unit to do something in the world.

Examples:

- `core.moveNorth()`
- `core.moveSouth()`
- `core.moveEast()`
- `core.moveWest()`
- `core.repair()`
- `core.activate()`
- `core.scan()`

Not every mission needs every action. Missions may expose only the relevant subset.

### 2. Checks

These methods let the learner inspect the current situation.

Examples:

- `core.canMoveNorth()`
- `core.canMoveEast()`
- `core.isDamagedSystemHere()`
- `core.isAtGoal()`
- `core.isPathClear()`

These methods should return simple values such as booleans or short, beginner-friendly result objects.

### 3. Information

These methods let the learner read small pieces of state when a mission requires it.

Examples:

- `core.batteryLevel()`
- `core.stepsRemaining()`
- `core.currentTileLabel()`

Early missions should use these sparingly.

## Mission Exposure Model

The game should use a layered API model:

- a small common set of CORE commands
- an optional mission-specific subset or helpers

This lets the game teach progressively without exposing the full long-term API from the start.

The browser UI should clearly show which commands are available in the current mission.

## Java Features By Stage

The student API should support the learning progression already defined in the learning spec.

### Early Missions

Early missions should optimize for:

- sequential method calls
- short `if` statements
- minimal syntax

Early missions should avoid requiring:

- class declarations
- custom object design
- advanced collections
- inheritance
- asynchronous code

### Later Missions

Later missions may gradually allow:

- local variables
- loops
- simple helper methods
- small domain objects returned by the API
- basic collections where pedagogically justified

These should be introduced only when the mission design requires them.

## Hidden From Learners

The learner should not need direct access to:

- engine internals
- rendering logic
- mission definitions
- persistence
- network access
- file system access
- thread management

The learner experience should remain focused on robot behavior.

## Error And Feedback Model

The system should return feedback in three layers.

### 1. Compile Feedback

If the learner's code does not compile, the browser should show:

- the error message in learner-friendly language where possible
- the relevant line in the editable code
- enough detail to fix the problem without exposing internal template code

### 2. Runtime Feedback

If the code runs but an action fails, the game should explain:

- what the learner tried to do
- why the action failed
- what the robot state or environment prevented

### 3. Mission Feedback

After execution, the game should explain:

- whether the mission objective was achieved
- what the robot did
- what remains to be solved if the objective failed

## Stability Rules

- The learner-facing object name should remain stable unless there is a strong reason to change it.
- Previously learned commands should remain available unless a mission intentionally constrains them.
- Breaking changes to the student API should be rare and documented explicitly.

## Example Learner Experience

An early mission may present code like:

```java
core.moveEast();
core.moveEast();

if (core.isDamagedSystemHere()) {
    core.repair();
}
```

The learner writes code, clicks run, watches the CORE unit act on screen, and receives feedback about whether the repair objective was completed.

## Consequences

- The browser experience stays simple because the learner edits only the relevant code.
- The game can teach Java concepts gradually without exposing full project structure.
- The backend remains responsible for wrapping, compiling, and executing learner code safely.
- The design favors beginner clarity over full Java freedom.

## Open Questions

- Whether the visible editing surface should always show `run()` or sometimes show only the statements inside it
- When simple helper methods should first be introduced to learners
- Whether action methods should remain directional, such as `moveNorth()`, or later evolve toward more abstract movement commands
- Whether mission-specific helper methods should be shown inline or in a separate command reference
