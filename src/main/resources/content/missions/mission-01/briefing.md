# Mission 01: Wake The CORE

## Summary
Standby power is active in Maintenance Room B-1049. Re-establish a control link and bring the docked CORE online.

## Objective
Call `Core.connect();` to bring CORE-01 online.

## Briefing
Standby power has returned to Maintenance Room B-1049, where a dormant CORE unit remains docked and unresponsive. Its exact condition is still unknown because no control link has been established. The only way to communicate with the unit is by issuing Java commands through the terminal. Connect to the CORE by calling `Core.connect();` so you can assess its status and begin restoring it to operation.

## Available Commands
- `Core.connect()` | Establishes a control link to the next available CORE unit.

## Hints
- Mission 01 expects a single method call.
- You do not need a variable yet.
- When `Core.connect();` works, the status panel should change from Offline to Online and reveal the CORE's condition.
