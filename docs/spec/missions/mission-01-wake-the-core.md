# Mission 01: Wake The CORE

## Purpose

This document defines the first playable mission in Code Colony.

Its purpose is to establish the player's initial understanding of the game world, the coding interface, and the act of controlling a CORE unit through Java code.

This mission should inherit its broader narrative framing from `docs/spec/story-spec.md` and only define the story details specific to this opening recovery slice.

## Mission Summary

The player has restored enough access to open a maintenance room with standby power. Inside the room, one CORE unit remains docked at a charging station. The same room also contains a repair station that will be used in later recovery work.

The player must establish a connection to the CORE unit.

## Learning Objective

Primary learning objective:

- calling a method to trigger an action

Supporting learning objective:

- understanding that control begins by connecting to a robot

This mission should teach that code can trigger a direct and visible change in the world.

## Player Fantasy

The player should feel like they have just regained enough system access to wake their first machine.

The emotional tone should be:

- calm
- technical
- slightly mysterious
- optimistic

This is not a danger-driven opening. It is a restoration-driven opening.

## Mission Narrative

Suggested briefing:

> Standby power is active in Maintenance Room B-1049. One CORE unit remains docked, but its condition is still unknown. Re-establish a control link and bring the unit online.

Mission text should be short and should not overwhelm the learner with story details.

For the first playable version, the briefing should present:

- a mission heading
- a short local context paragraph
- the objective to connect to the docked CORE
- a beginner-friendly hint showing `CORE.connect();`

The briefing should open as a modal when the mission page loads and should be reopenable through a visible `Briefing` button on the mission screen.

Mission 01 may also provide optional voiced playback inside the briefing modal, provided that:

- the written briefing text remains visible
- the player can proceed without listening
- the audio is a rendering of the approved Mission 01 briefing text rather than a separate source of truth
- closing the briefing stops any current playback

## Mission Layout

The mission takes place in a single small maintenance room.

The room should be represented as a 3x3 top-down grid for clarity.

### Layout Goals

- The player should be able to understand the room immediately.
- Left, right, up, and down should map directly to visible directions on screen.
- The docking station and repair station should be visually distinct.
- There should be no need for scrolling, panning, or camera movement.

## Map

Initial proposed layout:

```text
+---+---+---+
| . | . | . |
+---+---+---+
| D | . | S |
+---+---+---+
| . | . | . |
+---+---+---+
```

Legend:

- `D` = docking station tile, currently occupied by the CORE
- `C` = CORE unit starting position
- `S` = repair station
- `.` = walkable floor tile

Interpretation:

- The CORE unit starts on the docking station in the center-left area of the room.
- The repair station is reachable by moving right from the start position.
- The room is intentionally small and highly readable.

The final visual presentation may be richer than the ASCII map, but the underlying logic should remain simple.

## Visual Direction

The room should communicate a maintenance-bay fantasy.

Key visual elements:

- a charging dock or magnetic cradle
- a visible cable, clamp, or light indicating the CORE is plugged in
- a docking station tile that can later be used with `charge()`
- a repair station tile that can later be used with `repair()`
- industrial floor tiles with a clear walkable path

The first mission should use a top-down view, not an isometric view, so directional commands remain easy to understand.

For the first visual pass, the room should prefer a pixel-art style tile presentation over plain bordered content boxes.

This should mean:

- square tiles with crisp edges
- an industrial floor pattern that reads as a game space rather than a form layout
- a small authored-looking CORE silhouette on its dock
- a repair station tile that looks distinct from ordinary floor space
- labels that support readability without overwhelming the artwork

## Screen Layout

The intended mission screen layout for this mission is:

- a header spans the top of the page
- the main area shows the 3x3 top-down room view
- the lower area shows the code entry surface
- a side area beside the room view shows the CORE status panel

For this mission, the command reference should be integrated with the code area so the learner can see the available commands while writing code.

This layout should support a clear reading order:

1. see the room
2. write the code
3. watch the robot state and room state change

## CORE Status Panel

This mission should introduce a visible status panel for the active CORE unit.

The panel should show at least:

- active CORE identifier
- connection state
- battery state
- health state
- whether the unit is docked or active
- current position when relevant

Suggested initial state:

- `Status: Offline`
- no battery telemetry shown
- no health telemetry shown
- no dock or position telemetry shown

After the learner executes `CORE.connect()`, the panel should visibly change state.

Suggested connected state:

- `Status: Online`
- `Battery: 0/5`
- `Health: 1/5`
- `Dock: Connected`
- `Position: B1`

Battery and health should be rendered as five-segment bars once the CORE is connected.

This change is important because it teaches that `CORE.connect()` is not just syntax. It changes the world and reveals the robot's condition.

Later missions in this same room may also introduce station-dependent actions:

- the docking station allows charging only when the CORE is on that tile and the learner calls `core.charge()`
- the repair station allows repair only when the CORE is on that tile and the learner calls `core.repair()`

These effects should not happen automatically just because the CORE enters the tile.

Later missions may consume one battery segment for energy-using actions such as moving or rotating, but Mission 01 should only reveal the drained state rather than use it mechanically.

## Available Commands

This mission should expose only a very small set of commands.

Required commands:

- `CORE.connect()`

The command list shown in the UI should prioritize only what the player actually needs to solve the mission.

## Programming Model

This mission introduces the idea that the player must first establish a connection to the robot.

The intended learner code shape is:

```java
CORE.connect();
```

This mission should teach that a single line of Java can trigger a meaningful state change in the world.

Running `CORE.connect()` should also produce a visible status transition in the UI from offline to online.

The learner does not need to store the result of `CORE.connect()` in a variable in this mission.

## Starter Code

Recommended starter code for the first version:

```java
CORE.connect();
```

Alternative starter code for a more guided version:

```java
CORE.connect();
```

This mission should stay fully guided and minimal.

## Success Conditions

The mission succeeds when:

- the player establishes a connection to the CORE unit

The connection should visibly change the room state.

This should be validated from simulator-observed execution behavior. A successful run records one successful connect action and no duplicate-connect error.

Examples:

- the CORE status changes from offline to online
- the battery and health bars become visible
- a short success message confirms that the control link was established

After Mission 01 succeeds, the mission page should switch into a completed state:

- the code console remains visible
- the learner code becomes read-only
- `Run` and `Reset` are hidden
- a `Next` action is shown

That action should move the player into the Mission 02 handoff flow, which prepares the next task of charging the depleted CORE.

Extra learner code that does not interfere with the mission objective, such as `System.out.println(...)`, should not prevent success in Mission 01.

## Failure Conditions

The mission run fails when:

- the learner code does not compile
- the learner finishes execution without connecting to the CORE unit
- the learner attempts to connect more than once

This should be validated from execution results rather than by checking the submitted source text directly.

Invalid actions should not immediately end the run unless the resulting behavior becomes impossible to complete.

Examples:

- not calling `CORE.connect()`
- calling `CORE.connect()` more than once

These should produce readable feedback rather than abrupt punishment.

## Feedback Expectations

### Compile Feedback

The learner should see compiler feedback that points only to the visible code area.

### Action Feedback

The learner should see a step-by-step explanation such as:

- `Connected to CORE-01`

If the learner attempts to connect more than once, the feedback should explain that the CORE is already connected.

### Mission Feedback

The learner should see a short mission summary such as:

- `Control link established. CORE-01 is online.`

If the mission fails, the learner should see what remains incomplete.

### Program Output

If the learner prints output during Mission 01, the UI should show it in a separate `Program Output` panel rather than mixing it into mission feedback.

For example, if the learner submits:

```java
CORE.connect();
System.out.println("Hello!!");
```

then Mission 01 should still succeed, and the text `Hello!!` should appear under learner `stdout`.

## UI Expectations

The mission screen should show:

- the mission briefing as a modal that opens on first load
- a persistent `Briefing` button that reopens the mission briefing
- the available commands/manual
- the top-down 3x3 room view as the main visual panel
- the code entry area below the map
- the CORE status panel on the side
- simulation or action playback
- mission feedback

The initial Mission 01 briefing should be intentionally small and should emphasize that the player only needs to connect to the CORE unit.

The map should remain visible while the player edits and runs code.

## Design Constraints

- The room must stay small enough to understand at a glance.
- The mission must be solvable with a single method call.
- The visuals must support directional reasoning.
- The first mission should introduce control, not complexity.

## Open Questions

- Whether the first mission should require the learner to type `CORE.connect()` or whether that line should already be present in starter code
- Whether `moveRight()` alone is enough for the first mission or whether all four movement commands should be visible from the start
- Whether the relay should be adjacent to the dock or require two movement steps for a slightly stronger sense of travel
