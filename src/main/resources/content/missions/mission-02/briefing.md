# Mission 02: Charge The CORE

## Summary
CORE-01 is online, but its battery is fully depleted. The next step is to restore power while the unit remains docked.

## Objective
Charge CORE-01 to full power.

## Briefing
Congratulations, engineer. CORE-01 remains online from your previous recovery step and is responding to remote commands. Diagnostics still show the battery is completely depleted. Until power is restored, the unit will not be able to move beyond basic standby operations.

CORE-01 remains docked in Maintenance Room B-1049, where the charging station is still functional. For this run, start by calling `Core.connect()` to re-establish your control session and obtain a handle to the unit in code. Keep that returned CORE in a variable, then issue repeated `charge()` commands until the battery reaches full capacity.

Start by writing code like `var core = Core.connect();` and then call `core.charge();` several times. Each successful call will restore one segment of battery power. Your objective is to fully charge CORE-01 so it is ready for the next stage of recovery.

## Available Commands
- `Core.connect()` | Re-establishes control for this run and returns the available CORE unit.
- `core.charge()` | Restores one battery segment while the CORE is on the docking station.

## Hints
- CORE-01 remains online from Mission 01.
- At the start of each run, call `Core.connect()` to re-establish control and get a CORE reference.
- Each successful `core.charge();` call fills one power segment. Mission 02 needs 5 / 5.

## Initial Run Headline
Awaiting Run

## Initial Run Summary
CORE-01 remains online. Re-establish control for this run with `Core.connect()`, then charge it to full power.

## Initial Run Events
- CORE-01 is still docked in Maintenance Room B-1049 and remains online from the previous recovery step.
- The docking station can restore one power segment per successful charge command.
- Mission 02 is complete when the battery reaches {batteryCapacity} / {batteryCapacity}.

## Initial Run Feedback
- Start this run with `Core.connect()` so you can control CORE-01 in code.
- Rewrite the carried code so you keep the returned Core in a variable.
- Call `core.charge();` enough times to fill all five battery segments.

## Initial Run Status Note
CORE-01 remains online from Mission 01. Re-establish control for this run to operate the unit.
