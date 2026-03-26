# Mission 02 Explain

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
