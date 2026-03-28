# Mission 03: Repair The CORE

## Summary
CORE-01 is charged, but diagnostics still report structural damage.

## Objective
Move CORE-01 to the repair station and repair it.

## Briefing
Power is stable now, engineer. The next task is structural recovery.

The repair station is at tile B3. CORE-01 starts at B1.

Use Java commands to connect, move across the room, and run repair on the station tile.

A working shape is:

```java
var core = Core.connect();
core.move();
core.move();
core.repair();
```

Mission success requires completing the repair, not just movement.

## Available Commands
- `Core.connect()` | Establishes a control link to the next available CORE unit and returns it.
- `core.move()` | Moves CORE-01 one tile east in this mission room.
- `core.repair()` | Repairs one health segment when CORE-01 is on the repair station tile.

## Hints
- Mission 03 expects movement from B1 to B3 before repair.
- Use `core.move();` to reach the repair station.
- Call `core.repair();` on B3 until health reaches 5 / 5.

## Initial Run Headline
Awaiting Run

## Initial Run Summary
Move CORE-01 to the repair station at {repairPosition} and repair it.

## Initial Run Events
- CORE-01 is online, charged, and docked at {dockPosition}.
- Repair station is located at {repairPosition}.
- Mission success requires reaching {repairPosition} and calling `core.repair();`.

## Initial Run Feedback
- Connect to CORE-01 and keep the returned Core in a variable.
- Use two moves to reach B3, then call `core.repair();`.

## Initial Run Status Note
CORE-01 is charged but damaged. Repair station available at {repairPosition}.
