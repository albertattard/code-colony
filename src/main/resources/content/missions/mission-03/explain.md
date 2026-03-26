# Mission 03 Explain

## Headline
Repair CORE-01 step by step

## Explanation
In this mission, you are combining actions in sequence:

1. Connect to CORE-01.
2. Move from B1 to B2.
3. Move from B2 to B3.
4. Repair on the station tile.

A simple version looks like this:

```java
var core = Core.connect();
core.move();
core.move();
core.repair();
```

Think of `move()` like giving one "step" command each time.
One call moves one tile.

`repair()` only works at the repair station.
In this room, that station is at B3.

> **Why does repair fail sometimes?**
>
> Usually because CORE-01 is not on the repair station yet. Move to B3 first, then call `repair()`.

As always, end each Java statement with a semicolon (`;`).

---

If you want a beginner-friendly guide to Java method calls, continue here:
[Creating Objects and Calling Methods](https://learn.java/learning/tutorials/creatingobjectsandcallingmethods/)
