# Mission 02: Charge The CORE

## Summary
CORE-01 is online, but its battery is fully depleted. The next step is to restore power while the unit remains docked.

## Objective
Charge CORE-01 to full power.

## Briefing
Congratulations, engineer. Your connection to CORE-01 is stable, and the unit is now responding to remote commands. Initial diagnostics confirm that the link-up was successful, but the report also shows that the CORE's battery is completely depleted. Until power is restored, the unit will not be able to move beyond the most basic standby operations.

CORE-01 remains docked in Maintenance Room B-1049, where the charging station is still functional. To restore power, you will need to connect to the CORE again through Java, keep the returned unit in a variable, and then issue repeated `charge()` commands until the battery reaches full capacity.

Start by writing code like `var core = Core.connect();` and then call `core.charge();` several times. Each successful call will restore one segment of battery power. Your objective is to fully charge CORE-01 so it is ready for the next stage of recovery.
