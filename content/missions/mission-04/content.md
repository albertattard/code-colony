# Mission 04: Stabilize The CORE

## Summary
CORE-01 is operational again, but long-range activity requires full battery reserves. Stabilize power by topping the battery to maximum capacity.

## Objective
Charge CORE-01 from partial charge to full power.

## Briefing
Recovery operations succeeded and CORE-01 is back online with major systems intact. Current diagnostics show only partial battery reserves, which is enough for standby but not for reliable field deployment.

In this run, reconnect to CORE-01 with `Core.connect()`, store the returned unit in a variable, and continue charging until power reaches 5 / 5.

This mission reinforces session control and repeatable command execution under stable conditions.

## Available Commands
- `Core.connect()` | Re-establishes control for this run and returns the available CORE unit.
- `core.charge()` | Restores one battery segment while the CORE is on the docking station.

## Hints
- CORE-01 starts this mission at 2 / 5 power.
- Call `Core.connect()` at the beginning of each run.
- Keep calling `core.charge();` until the battery reaches 5 / 5.

## Initial Run Headline
Awaiting Run

## Initial Run Summary
CORE-01 is online with partial battery reserves. Reconnect and charge to full capacity.

## Initial Run Events
- CORE-01 starts on the docking station with 2 / 5 battery.
- Each successful `core.charge();` call restores one battery segment.
- Mission 04 completes at 5 / 5 battery.

## Initial Run Feedback
- Start by reconnecting with `Core.connect()`.
- Keep the returned `Core` in a variable.
- Call `core.charge();` until the battery is full.

## Initial Run Status Note
CORE-01 is partially charged. Re-establish control and top up battery reserves.

## Headline
Stabilize Power Through Repetition

## Explanation
This mission reinforces a core loop:

1. Obtain a reference to the unit with `Core.connect()`.
2. Reuse that same reference for repeated actions.

`charge()` does not fill the battery instantly. It increases battery by one segment each time, so repeated method calls are required to reach full power.
