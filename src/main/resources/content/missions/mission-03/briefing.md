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
