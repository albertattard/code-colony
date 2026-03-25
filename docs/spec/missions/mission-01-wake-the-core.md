# Mission 01: Wake The CORE

## Purpose

This document defines the first playable mission in Code Colony.

Its purpose is to establish the player's initial understanding of the game world, the coding interface, and the act of controlling a CORE unit through Java code.

This mission should inherit its broader narrative framing from `docs/spec/story-spec.md` and only define the story details specific to this opening recovery slice.

## Mission Summary

The player has restored enough access to open a maintenance room with standby power. Inside the room, one CORE unit is still plugged into a charging dock and fully charged. A damaged relay cabinet in the same room blocks wider access to the colony network.

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

> Standby power is active in Maintenance Room A1. One CORE unit remains docked and fully charged. Re-establish a control link and bring the unit online.

Mission text should be short and should not overwhelm the learner with story details.

## Mission Layout

The mission takes place in a single small maintenance room.

The room should be represented as a 3x3 top-down grid for clarity.

### Layout Goals

- The player should be able to understand the room immediately.
- Left, right, up, and down should map directly to visible directions on screen.
- The docking station and damaged relay should be visually distinct.
- There should be no need for scrolling, panning, or camera movement.

## Map

Initial proposed layout:

```text
+---+---+---+
| W | W | W |
+---+---+---+
| D | C | R |
+---+---+---+
| W | . | W |
+---+---+---+
```

Legend:

- `W` = wall or room machinery edge
- `D` = docking station
- `C` = CORE unit starting position
- `R` = damaged relay cabinet
- `.` = walkable floor tile

Interpretation:

- The CORE unit starts docked in the center-left area of the room.
- The relay cabinet is reachable by moving right from the start position.
- The room is intentionally small and highly readable.

The final visual presentation may be richer than the ASCII map, but the underlying logic should remain simple.

## Visual Direction

The room should communicate a maintenance-bay fantasy.

Key visual elements:

- a charging dock or magnetic cradle
- a visible cable, clamp, or light indicating the CORE is plugged in
- a fully charged status light on the dock
- a damaged relay cabinet with warning lights or sparks
- industrial floor tiles with a clear walkable path

The first mission should use a top-down view, not an isometric view, so directional commands remain easy to understand.

## Screen Layout

The intended mission screen layout for this mission is:

- a header spans the top of the page
- the main area shows the 3x3 top-down room view
- the lower-left area shows the code entry surface
- the lower-right area shows the CORE status panel

For this mission, the command reference should be integrated with the code area so the learner can see the available commands while writing code.

This layout should support a clear reading order:

1. see the room
2. write the code
3. watch the robot state and room state change

## CORE Status Panel

This mission should introduce a visible status panel for the active CORE unit.

The panel should show at least:

- connection state
- battery state
- whether the unit is docked or active
- current position when relevant

Suggested initial state:

- `Status: Offline`
- `Battery: Full`
- `Dock: Connected`

After the learner executes `CORE.connect()`, the panel should visibly change state.

Suggested connected state:

- `Status: Online`
- `Battery: Full`
- `Dock: Released` or `Dock: Ready`

This change is important because it teaches that `CORE.connect()` is not just syntax. It changes the world and the robot state.

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
- the dock indicator changes state
- a short success message confirms that the control link was established

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

## UI Expectations

The mission screen should show:

- the mission briefing
- the available commands/manual
- the top-down 3x3 room view as the main visual panel
- the code entry area below the map
- the CORE status panel on the side
- simulation or action playback
- mission feedback

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
