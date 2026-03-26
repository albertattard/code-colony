# Mission 02 Explain

## Headline
Charge CORE-01 Step By Step

## Explanation
Mission 02 is like plugging in a device and then pressing charge again and again until the battery is full.

Your code shape is:

`var core = Core.connect();`

`core.charge();`

`Core.connect()` gives you a CORE object to work with.

`var core = ...` stores it in a variable called `core`, so you can keep talking to that same unit.

Then `core.charge()` calls a method on that object and adds one charge step.

In plain words: connect once, then call `charge()` enough times to fill the battery meter.

If you want a beginner-friendly walkthrough on objects and method calls, continue here: [Creating Objects and Calling Methods](https://learn.java/learning/tutorials/creatingobjectsandcallingmethods/).
