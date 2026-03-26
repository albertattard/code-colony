# Mission 03: Repair The CORE

## Purpose

This document defines the third playable mission in Code Colony.

Its purpose is to teach sequential command composition by moving CORE-01 to the repair station and executing repair.

## Mission Summary

CORE-01 is now powered, but structural diagnostics still report damage. The player must navigate CORE-01 from the dock to the repair station and restore full health.

## Learning Objective

Primary learning objective:

- chaining multiple instance method calls in mission order

Supporting learning objective:

- understanding location-dependent actions (`repair()` only works at the repair station)

## Mission Narrative

Suggested briefing:

> Power is stable, but CORE-01 is still damaged. Move the unit from B1 to repair station B3 and complete structural repair.

## Mission Layout

Mission 03 reuses the same room and panel layout as Mission 02.

## Available Commands

Required commands:

- `Core.connect()`
- `core.move()`
- `core.repair()`

Previously learned command still accepted:

- `core.charge()`

Mission 03 should keep `core.charge()` valid so Mission 02 carried code can run without failing, even though charging is not part of the Mission 03 objective.

## Programming Model

Intended learner shape:

```java
var core = Core.connect();
core.move();
core.move();
core.repair();
```

Mission 03 should preload the learner's last successful Mission 02 code as a starting point when available.

## Battery And Movement Semantics

Mission 03 starts with CORE-01 at full battery (`5/5`) and damaged health (`1/5`).

For this mission:

- each successful `core.move()` call consumes exactly one battery segment
- battery should not drop below zero
- `core.charge()` remains capped at `5/5`
- `core.charge()` only succeeds while CORE-01 is on docking station tile `B1`

## Success Conditions

The mission succeeds when:

- one successful connect action occurs
- CORE-01 reaches tile B3
- `core.repair()` executes successfully at the repair station
- health reaches `5/5`

## Failure Conditions

The mission fails when:

- code does not compile
- no control reference is obtained
- repair is attempted away from B3
- run finishes without successful repair

## UI Expectations

After Mission 03 succeeds:

- code becomes read-only
- `Run` and `Reset` are hidden
- `Explain` remains visible
- `Next` may remain a disabled placeholder until Mission 04 exists
