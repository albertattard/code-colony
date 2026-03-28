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

## Initial Run Headline
Awaiting Run

## Initial Run Summary
Enter `Core.connect();` and click Run to bring CORE-01 online.

## Initial Run Events
- CORE-01 is docked in Maintenance Room B-1049.
- The control link is offline.
- Docking station is located at {dockPosition}.
- Repair station is located at {repairPosition}.
- Running code will update the CORE status and feedback panels.

## Initial Run Feedback
- Mission 01 expects a single method call: `Core.connect();`
- The first successful run should bring CORE-01 online.

## Initial Run Status Note
No telemetry available while offline.
