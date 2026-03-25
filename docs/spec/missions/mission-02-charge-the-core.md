# Mission 02: Charge The CORE

## Purpose

This document defines the second playable mission in Code Colony.

Its purpose is to build directly on Mission 01 by teaching the learner how to store the result of `CORE.connect()` in a variable and call an instance method repeatedly to change robot state.

## Mission Summary

CORE-01 is now online, but telemetry confirms that the unit's battery is fully depleted and the chassis is still damaged. The immediate priority is to restore power so the robot can perform at least basic movement in later missions.

The player must charge the docked CORE to full capacity.

## Learning Objective

Primary learning objective:

- storing a returned object in a local variable

Supporting learning objective:

- calling an instance method multiple times to produce repeated state changes

This mission should teach that `CORE.connect()` returns a usable object and that repeated method calls can accumulate visible results.

## Player Fantasy

The player should feel like they are moving from first contact into hands-on recovery work.

The emotional tone should be:

- calm
- methodical
- technical
- quietly encouraging

## Mission Narrative

Suggested briefing:

> CORE-01 is responding, but its battery is empty. The unit remains docked in Maintenance Room B-1049, and the charging station is active. Use Java commands to connect to the CORE, keep the returned unit reference, and restore power until the battery is full.

For the first playable version, the briefing should present:

- a mission heading
- a short local context paragraph
- the objective to fully charge CORE-01
- a beginner-friendly hint showing `var core = CORE.connect();` and `core.charge();`

This briefing should open as a modal when the Mission 02 page loads and should be reopenable through the visible `Briefing` button.

## Mission Layout

Mission 02 should reuse the Mission 01 screen layout.

This means:

- the same header structure
- the same 3x3 maintenance room view
- the same code panel placement
- the same CORE status panel placement
- the same feedback and output panels

The goal is to preserve visual continuity while the programming task becomes slightly more advanced.

## Map

Mission 02 should reuse the same maintenance room and station layout as Mission 01.

This means:

- CORE-01 starts on the docking station tile
- the docking station is the tile from which charging is possible
- the repair station remains visible for later missions but is not part of the Mission 02 objective

## CORE Status Panel

At the start of Mission 02, the player knows from Mission 01 that CORE-01 exists, but the mission still begins from a fresh simulation run when code is executed.

The status panel should communicate:

- `Status: Offline` before the learner reconnects
- no battery or health telemetry while offline

After a successful `CORE.connect()` call, the status panel should reveal:

- `Status: Online`
- `Battery: 0/5`
- `Health: 1/5`
- `Dock: Connected`
- `Position: B1`

Each successful `core.charge()` call on the docking station should increase the battery by one segment.

The battery display should stop at `5/5`. There is no overcharging state in this mission.

## Available Commands

Required commands:

- `CORE.connect()`
- `core.charge()`

The command reference should explain that:

- `CORE.connect()` returns a `CORE`
- `core.charge()` restores one battery segment when the CORE is on the docking station

## Programming Model

This mission introduces a new code shape:

```java
var core = CORE.connect();
core.charge();
```

The learner must now keep the connected CORE in a variable so they can call methods on that specific instance.

Mission 02 should preload the learner's exact last successful Mission 01 code as the starting point for the editor.

This means the game should preserve the learner's successful submitted code exactly as entered, including any extra harmless lines such as `System.out.println(...)`, rather than replacing it with a normalized starter snippet.

The editor should become writable again for this mission, because the learner is extending their previous solution.

## Starter Code

Recommended carried-forward starting point when the learner finished Mission 01 with only the essential solution:

```java
CORE.connect();
```

The mission briefing and command reference should guide the learner toward changing that into a variable-based form.

## Charge Semantics

For Mission 02:

- `core.charge()` succeeds only when the CORE is on the docking station tile
- each successful call restores exactly one battery segment
- charging is capped at five segments
- calling `core.charge()` after the battery is full does not fail the mission and does not increase the battery above five

The first successful charge should give the CORE enough power for one future move, but movement is not part of this mission's objective.

## Success Conditions

The mission succeeds when:

- the player connects to CORE-01
- the player charges the CORE to full battery

This should be validated from simulator-observed execution behavior.

A successful run should therefore show:

- one successful connect action
- five total successful charge actions
- battery level reaching `5/5`

Extra calls to `core.charge()` after the battery is full should not prevent success.

## Failure Conditions

The mission run fails when:

- the learner code does not compile
- the learner finishes execution without connecting to the CORE
- the learner finishes execution without reaching full battery
- the learner calls `core.charge()` before storing or obtaining a CORE instance

These failures should produce readable feedback rather than abrupt punishment.

## Feedback Expectations

### Compile Feedback

If the learner writes code that does not compile, the feedback should point to the visible code area and help them correct the variable or method call syntax.

### Action Feedback

The learner should see step-by-step updates such as:

- `Connected to CORE-01`
- `Charged CORE-01 to 1/5`
- `Charged CORE-01 to 2/5`
- `Charged CORE-01 to 3/5`
- `Charged CORE-01 to 4/5`
- `Charged CORE-01 to 5/5`

If the learner charges after full battery, feedback may explain that the battery is already full, but this should not count as mission failure.

### Mission Feedback

The learner should see a short mission summary such as:

- `CORE-01 battery restored to full capacity.`

If the mission fails, the feedback should explain how much battery was restored and what remains incomplete.

## UI Expectations

The mission screen should show:

- the mission briefing as a modal that opens on first load
- a persistent `Briefing` button that reopens the mission briefing
- the carried-forward learner code from Mission 01
- the status panel visibly filling one battery segment per successful `charge()` call

After Mission 02 succeeds, the page may use the same completed-state pattern introduced in Mission 01:

- read-only learner code
- hidden `Run` and `Reset`
- a visible `Next` action

The exact destination of `Next` should be defined by the Mission 03 spec.
