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

## Headline
Charge CORE-01 one step at a time

## Explanation
In Mission 01, you brought CORE-01 online. In this mission, your goal is to **charge its battery**.

Think of it like charging a handheld device. Before you can charge a device, you need access to it. If I ask you to charge my phone, I first have to give you the phone.

In Java, you get access to objects using **variables**. Consider the following line:

```java
var core1 = Core.connect();
```

This can be hard to digest, so let’s break it down. The word `var` tell Java that we are creating a variable. This needs to be following by the name of the variable, which is `core1` in our case.

> **What’s the difference between `Core` (with the capitol ‘C’) and `core` (with the small ‘c’)?**
>
> `Core` represents the name of the class. The `Core` class represents the CORE unit in Java. In the hints, we have `core.charge()`, where `core` is written with the small ‘c’. The `core` is the variable name while `Core` is the name of the class. This is a very common mistake programmers make. Here we are using `core1` as the variable name. You can pick any name you like as long as this is a valid variable name. To keep it simple, limit yourself to letters and numbers only. The variable can contain numbers but it cannot start with one.

The `connect()` method connects to a CORE unit and returns it. The variable `core1` stores that unit so you can use it in your code. You can think of `core1` as the specific CORE you are controlling. To charge it, you call:

```java
core1.charge();
```

This calls the `charge()` method on that CORE and increases its battery by one bar. If you want to charge the CORE by two bars, then you need to call it twice.

```java
core1.charge();
core1.charge();
```

> **Can we call `charge()` on the `Core` class instead?**
>
> No. Think about it this way: if you charge one phone, do all phones become charged? Of course not. You must charge each device individually. The same idea applies here.

So far, you have connected to one CORE. Later in the game, you will work with more than one. For example:

```java
var core1 = Core.connect();
var core2 = Core.connect();
```

Here, you now have access to two different CORE units. If `core2` has no battery, do you need to charge `core1` as well?  No, you only charge the one that needs it:

```java
core2.charge();
```

---

If you would like a beginner-friendly walkthrough of objects and method calls, continue here:
[Creating Objects and Calling Methods](https://learn.java/learning/tutorials/creatingobjectsandcallingmethods/)
