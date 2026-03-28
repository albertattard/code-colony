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

When the learner runs their code, the system should execute it in a predefined context that gives access to the student-facing `CORE` API.

Within the game world, `CORE` means `Colony Operations and Repair Engineer`.

Each learner run should execute in its own worker JVM process.

In early missions, the learner should not need to construct objects manually. The game should expose a static entry point:

```java
Core.connect()
```

This method establishes a connection to a mission-defined CORE unit and returns a learner-facing `CORE` instance for later interaction when the mission requires it.

Internally, the static `Core` API may delegate to a run-scoped mission simulator attached for that worker JVM. The learner should not need to see or understand that runtime structure.

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

Entry point examples:

- `Core.connect()`
- `Core.connect(1)`

Instance action examples:

- `core.move()`
- `core.rotateClockwise()`
- `core.rotateCounterClockwise()`
- `core.charge()`
- `core.repair()`
- `core.activate()`
- `core.scan()`

Not every mission needs every action. Missions may expose only the relevant subset.

### 2. Checks

These methods let the learner inspect the current situation.

Examples:

- `core.canMove()`
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

## Early Mission Progression Rules

The first missions should reuse the same learner-facing `Core` type while gradually changing how the learner uses it.

Expected progression:

- Mission 01 introduces `Core.connect();` as a standalone static call
- Mission 02 introduces storing the returned `CORE` in a local variable and calling an instance method on it
- Mission 03 introduces sequential instance actions with `core.move()` and station-dependent `core.repair()`

This means Mission 02 should explicitly teach that `Core.connect()` returns a `CORE` instance that can be reused for later actions.

## Mission Exposure Model

The game should use a layered API model:

- a small common set of `Core` entry points
- a small common set of instance commands on connected CORE objects
- an optional mission-specific subset or helpers

This lets the game teach progressively without exposing the full long-term API from the start.

The browser UI should clearly show which commands are available in the current mission.

Later missions may introduce:

- `var core = Core.connect();`
- `Core.connect(int number)` to connect to a specific mission-defined CORE
- instance methods such as `move()`, `rotateClockwise()`, `rotateCounterClockwise()`, `charge()`, and `repair()`

Internally, later instance methods may issue commands against the run's mission simulator, which then applies world rules and records mission events. This is an implementation detail and should not complicate the learner-facing API.

### Station-Dependent Actions

Some instance actions should depend on the CORE unit's current position in the mission space.

For early planned station interactions:

- `core.charge()` should succeed only when the CORE is on a docking station tile
- `core.repair()` should succeed only when the CORE is on a repair station tile

For early battery behavior:

- `core.charge()` should restore one battery segment per successful call
- charging should stop at the unit's maximum capacity
- calling `core.charge()` while already full should leave the battery unchanged and should not be treated as an error

For early repair behavior:

- `core.repair()` should restore one health segment per successful call
- repairing should stop at the unit's maximum health
- calling `core.repair()` while already fully repaired should leave health unchanged and should not be treated as an error

These actions should not happen automatically when the CORE enters the relevant tile. The learner must call the method explicitly.

If the learner calls a station-dependent action from the wrong location, runtime feedback should explain that the required station is not present on the current tile.

## Java Features By Stage

The student API should support the learning progression already defined in the learning spec.

### Early Missions

Early missions should optimize for:

- sequential method calls
- short `if` statements
- minimal syntax
- a single obvious entry point into the student API

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

Mission feedback should be based on observed execution behavior, such as commands accepted by the simulator and events produced during the run, rather than on source-text matching.

## Stability Rules

- The learner-facing object name should remain stable unless there is a strong reason to change it.
- Previously learned commands should remain available unless a mission intentionally constrains them.
- Breaking changes to the student API should be rare and documented explicitly.

## Example Learner Experience

An early mission may present code like:

```java
Core.connect();
```

A following mission may build on that code like:

```java
var core = Core.connect();
core.charge();
```

Later missions may present code like:

```java
var core = Core.connect();
core.rotateClockwise();
core.move();
```

or:

```java
var core = Core.connect();
core.move();
core.charge();
```

The learner writes code, clicks run, watches the CORE unit act on screen, and receives feedback about whether the mission objective was completed.

## Consequences

- The browser experience stays simple because the learner edits only the relevant code.
- The game can teach Java concepts gradually without exposing full project structure.
- The backend remains responsible for wrapping, compiling, and executing learner code safely.
- Each learner run can keep a static entry point such as `Core.connect()` without leaking state across runs because execution happens in a fresh worker JVM.
- The design favors beginner clarity over full Java freedom.

## Open Questions

- Whether the visible editing surface should always show `run()` or sometimes show only the statements inside it
- When simple helper methods should first be introduced to learners
- Whether mission-specific helper methods should be shown inline or in a separate command reference
