# Gameplay Spec

## Purpose

This document defines how Code Colony is played.

It connects the product vision, the teaching model, and the student programming surface into a concrete gameplay loop. Its purpose is to specify what the player sees, what happens when they run code, how the simulation responds, and how mission success or failure is communicated.

## Scope

This spec defines:

- mission flow
- browser mission layout
- code run cycle
- simulation behavior
- mission success and failure handling
- retry and reset behavior
- gameplay feedback shown to the player

This spec does not define the technical sandbox implementation in detail.

## Design Goals

The gameplay loop should:

1. make code feel like direct control over a robot
2. give fast, readable feedback after each run
3. keep each mission small and understandable
4. reinforce the learning objective through visible outcomes
5. maintain enough narrative tension to keep players curious

## Core Gameplay Loop

The intended mission loop is:

1. The player reads the mission briefing.
2. The player reviews the available commands and current code area.
3. The player enters or updates Java code.
4. The player clicks `Run`.
5. The system validates and executes the code.
6. The CORE unit performs actions on screen.
7. The system reports what happened and whether the objective was completed.
8. The player revises the code or proceeds to the next unlocked step.

This loop should stay short. A learner should be able to move from code change to visible outcome quickly.

## Mission Structure

Each mission should include:

- a short narrative briefing
- one clear primary objective
- an optional secondary objective or challenge goal
- a visible environment or map
- a small set of available commands
- a learner-editable code area
- a feedback area for compile, runtime, and mission results

Each mission must identify its primary learning concept and should not depend on hidden rules that the player cannot infer from the UI or mission text.

## Browser Mission Layout

The mission screen should include these core areas.

For the early missions, the layout should prioritize readability over visual density:

- a header spans the top of the mission screen
- the main area emphasizes the map and simulation
- the lower area holds the code entry surface
- a side area beside the lower section shows CORE status information

This layout should help the learner connect code, robot state, and movement on the map without competing visual priorities.

### 1. Mission Briefing Panel

This panel should show:

- mission title
- short narrative context
- primary objective
- optional hints or reminders

The briefing should be concise and readable in one sitting.

For early missions, this panel may be presented as a header rather than as a separate boxed sidebar.

### 2. Command Reference Panel

This panel should show:

- the commands available in the current mission
- short explanations of what each command does
- any mission-specific constraints

The command reference should reflect only what the learner can actually use in that mission.

In early missions, this reference may sit inside the code area rather than occupying a separate major panel.

### 3. Code Panel

This panel should provide:

- the editable Java snippet area
- a clear `Run` action
- a clear `Reset` action

The code surface should remain focused on learner-written logic, not project structure.

### 4. Simulation Panel

This panel should show:

- the current map or mission area
- the CORE unit position
- important systems, obstacles, and goal objects
- visible action results as the run plays out

The simulation should make robot behavior legible at a glance.

In early missions, this should be the dominant panel in the layout.

### 5. Status Panel

This panel should show the current visible state of the active CORE unit.

Examples:

- whether the CORE unit is offline, connected, or active
- battery level
- connection state
- mission-relevant status such as docked or undocked

Status changes caused by learner code should be visible here.

For example:

- before `CORE.connect()`, the CORE status may show `Offline` or `Standby`
- after `CORE.connect()`, the status should visibly change to `Online`

This panel should help the learner understand that commands affect not only position but also robot state.

### 6. Feedback Panel

This panel should show:

- compiler feedback
- action logs
- runtime errors
- mission success or failure summary

This panel is part of the learning experience, not just a debugging aid.

## Run Cycle

When the player clicks `Run`, the system should perform the following sequence:

1. Validate the submitted code.
2. Compile the learner code within the provided execution context.
3. If compilation fails, stop and show compile feedback.
4. If compilation succeeds, start a fresh mission run.
5. Execute the learner code.
6. Resolve robot actions in sequence.
7. Update the simulation view as actions occur.
8. Stop when execution completes, the objective is reached, or a failure/limit condition is triggered.
9. Present a mission result summary.

Each run should start from a clean mission state unless the game explicitly introduces persistent state later.

Mission results should be derived from the observed runtime behavior of the learner program, including simulator state changes and recorded execution events, rather than from matching source text patterns.

## Simulation Model

For the MVP, the simulation should behave as a deterministic action sequence.

This means:

- the same code in the same mission state should produce the same result
- actions should resolve in the order the learner's code triggers them
- mission outcomes should not depend on hidden randomness

This is important for both teaching clarity and debugging.

## Action Resolution

The simulation should resolve each action one step at a time.

For example:

- a movement command updates the CORE unit's position if the path is valid
- a repair command affects the current tile or object if repair is possible
- a scan command reveals information and records the result in feedback

If an action cannot be completed, the game should report that clearly.

Examples:

- the path is blocked
- there is nothing to repair here
- the target system is already active

## On-Screen Playback

The player should see the robot's behavior play out on screen after clicking `Run`.

The playback should:

- show actions in order
- be quick enough to keep iteration fast
- be slow enough to make cause and effect understandable

For the MVP, the playback can be lightweight and step-based rather than highly animated.

Visible status changes should update as part of the same playback cycle when appropriate.

## Mission Outcome Rules

Each mission must define:

- success conditions
- failure conditions, if any
- whether partial progress matters

Mission outcome evaluation should be based on what the learner program actually caused in the simulation.

### Success

A mission succeeds when the required objective has been completed according to the mission rules.

Examples:

- the damaged relay has been repaired
- the CORE unit reached the control terminal and activated it
- all required systems in the mission have been restored

### Failure

A mission run fails when:

- the learner code does not compile
- a runtime limit or mission limit is reached
- the learner finishes execution without meeting the objective

An individual invalid action should not necessarily end the whole run unless the mission explicitly requires that behavior.

## Mission Limits

The game may apply mission limits where useful for design clarity.

Examples:

- maximum number of executed actions
- battery limit
- step budget

These should be visible to the player whenever they matter.

## Feedback Model

Feedback should be grouped into three types.

### 1. Compile Feedback

Shown when the learner's Java does not compile.

It should:

- point to the relevant learner-visible line
- avoid exposing internal wrapper code
- help the learner fix syntax or API misuse

### 2. Action Feedback

Shown during or after execution.

It should:

- list the actions attempted
- indicate whether each action succeeded
- explain clearly when an action failed

### 3. Mission Feedback

Shown at the end of the run.

It should:

- state whether the mission succeeded
- summarize what the CORE unit achieved
- indicate what remains incomplete if the mission failed

## Visibility Rules

The player should see enough of the mission area to reason about the task.

For the MVP:

- relevant obstacles should be visible
- relevant targets should be visible
- the starting position should be visible

Fog of war or limited visibility may be introduced later, but should not obscure the learning objective in early missions.

## Retry And Reset

The player must be able to retry quickly.

The mission UI should support:

- rerunning updated code immediately
- resetting the code to the mission starter version
- resetting the simulation to the mission start state

Retry should feel cheap and encouraged.

## Progression

When a mission is completed, the game should:

- clearly acknowledge success
- reveal the next piece of story or system recovery
- unlock the next mission or next mission step

Mission progression should reward the learner without interrupting the coding rhythm for too long.

## MVP Gameplay Assumptions

For the first playable version:

- a single CORE unit is active
- one mission is available
- the player runs code manually through a `Run` action
- simulation playback is sequential and deterministic
- the mission starts from a clean state on every run

These constraints are intentional and should remain in place until the basic loop is proven fun and teachable.

## Consequences

- Frontend and backend work now have a shared contract for what a mission run means.
- The game loop is defined as code entry, run, playback, and feedback rather than free-form interaction.
- Deterministic playback supports teaching and debugging.
- The system must provide fast resets and clear logging for each run.

## Open Questions

- Whether the first UI should show action playback live or show a completed state with a step log
- Whether invalid actions should always allow execution to continue
- Whether optional challenge goals should appear from the first mission or later
- How much story text should be shown between missions without slowing the play loop
