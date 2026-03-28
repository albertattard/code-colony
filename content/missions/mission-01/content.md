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

## Headline
Wake CORE-01 with one line of code

## Explanation
Java is built around *classes*, which you can think of as blueprints for creating things in code. For example, a class could represent a car.

If we wanted to describe a car in Java, we could write:

```
class Car {
}
```

At the moment, this class is empty — it simply defines that a “Car” exists.

Classes can contain *fields* and *methods*.

* *Fields* store information about something, such as the colour of a car or how fast it is travelling.
* *Methods* define what something can *do*, such as driving.

For now, we will focus on *methods*, as they are what you will be using in this mission. We will come back to fields later.

```
class Car {
  void drive() {
  }
}
```

The `drive()` method is empty for now. In a real program, this is where we would write the instructions that make the car move.

Now let’s look at the code you will use in this game:

```
Core.connect();
```

Here’s how to understand it:

* `Core` is a *class* (a blueprint).
* `connect()` is a *method* (an action).

So this line means: *“Ask the Core system to connect.”*

Here is what that class might look like:

```
class Core {
  static Core connect() {
  }
}
```

You may notice a couple of unfamiliar things, such as the word `static` and the fact that this method does not use `void`. Don’t worry about these yet — they will be explained later. For now, just focus on what the method *does*.

In this game, you are only writing *individual lines of code*. You are not creating full classes or methods yourself. This keeps things simple while you learn the basics.

Behind the scenes, your code is placed inside something like this:

```
class PlayerProgram {
  void run() {
  }
}
```

For example, if you write:

```
IO.println("Hello CORE!");
```

the game runs it like this:

```
class PlayerProgram {
  void run() {
    IO.println("Hello CORE!");
  }
}
```

One final detail: in English, we end sentences with a full stop.
In Java, we end statements with a *semicolon (`;`)*.

That is why every line of code you write must end with one.

---

If you would like a step-by-step introduction to objects and method calls, continue here:
[Creating Objects and Calling Methods](https://learn.java/learning/tutorials/creatingobjectsandcallingmethods/)
