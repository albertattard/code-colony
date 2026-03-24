# Learning Spec

## Purpose

This document defines how Code Colony teaches Java. It exists to ensure that educational progression remains deliberate, beginner-friendly, and aligned with the game design.

## Learning Goals

The project should help learners:

1. Understand that code is a set of precise instructions.
2. See how Java code changes behavior in a running system.
3. Build confidence through short cycles of editing, running, and observing.
4. Learn core Java concepts through meaningful mission objectives.
5. Practice debugging by comparing expected and actual robot behavior.

## Audience Assumptions

Current working assumptions:

- Learners are beginners or near-beginners.
- They may have seen programming concepts before, but should not be assumed to know Java syntax well.
- They benefit from concrete feedback and constrained tasks.

These assumptions should be revised once the target age group and teaching context are finalized.

## Teaching Principles

- Introduce one primary concept per mission.
- Use one supporting concept only when necessary.
- Prefer concrete action words over abstract terminology.
- Show visible consequences for code decisions.
- Keep starter code short enough to read in one sitting.
- Treat mistakes as part of the lesson, not as punishment.

## Learner Experience Requirements

Each mission should provide:

- a clear objective
- a short narrative setup
- a limited set of available commands or methods
- starter code with obvious edit points
- visible output describing the robot's actions
- a clear success or failure result

Learners should not need to understand the full engine or project structure to complete a mission.

## Pedagogical Model

Code Colony should use a guided construction model:

1. Present a problem in the game world.
2. Give the learner a constrained Java surface to modify.
3. Let the learner run the result quickly.
4. Show the consequences in a readable simulation.
5. Encourage revision until the behavior matches the goal.

This should feel closer to programming a robot than writing abstract exercises.

## Planned Concept Progression

This is the current working progression, not a locked sequence.

### Stage 1: Instructions And Sequence

Learners practice:

- calling methods
- reading short code blocks in order
- understanding that instructions run step by step

Mission style:

- move to a location
- activate or repair a simple system

### Stage 2: Methods

Learners practice:

- calling existing methods with confidence
- recognizing that methods represent named actions
- reading short method-based code clearly

Mission style:

- trigger systems through specific commands
- combine several named actions to solve a task

### Stage 3: Conditions

Learners practice:

- `if` statements
- simple comparisons
- reacting to the environment

Mission style:

- repair only damaged systems
- choose between paths or actions

### Stage 4: Loops

Learners practice:

- `for` loops
- `while` loops
- repeating behavior until a task is complete

Mission style:

- explore corridors
- scan several tiles
- keep repairing until power is restored

### Stage 5: State And Objects

Learners practice:

- storing values in variables
- tracking robot state
- working with small domain objects

Mission style:

- manage battery level
- remember visited locations

### Stage 6: Collections And Simple Strategy

Learners practice:

- lists or maps at a beginner-friendly level
- simple prioritization logic
- basic algorithmic thinking

Mission style:

- choose which system to repair first
- process several alerts or scan results

## Mission Design Rules

- Each mission must identify its primary learning objective.
- Each mission must list assumed prior knowledge.
- Mission text must avoid introducing unexplained Java terms.
- Failure messages should connect to learner intent where possible.
- The mission should be solvable with the provided API and lesson scope only.

## Student Code Surface

The learner should usually interact with:

- one student-editable class
- one small set of methods from the game API
- starter comments or prompts that indicate what to change

The learner should usually not need to edit:

- engine code
- mission definitions
- build files
- low-level simulation logic

## Feedback Rules

Feedback should explain:

- what action the robot attempted
- whether the action succeeded
- why an action failed, if known
- whether the mission objective was met

Whenever possible, feedback should map directly to the learner's code choices.

## Difficulty Control

To keep missions beginner-friendly:

- limit the number of new ideas per mission
- avoid long files
- avoid deep inheritance or advanced Java features
- keep debugging signals clear
- use readable naming throughout the student API

## Teacher And Facilitator Needs

The project should eventually support:

- starter solutions
- reference solutions
- the ability to reset a mission quickly
- guidance on what each mission is intended to teach

These requirements should influence later architecture and documentation.

## Open Questions

- Exact age and reading level for the first release
- Whether early missions should hide method signatures until needed
- How much syntax should be prewritten for absolute beginners
- Whether missions should include optional challenge goals
- Whether teacher notes belong in the main repo or separate workshop materials
