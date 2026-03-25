# Game Intro Spec

## Purpose

This document defines the player entry screen that appears before Mission 01.

Its purpose is to give the player enough narrative and interface context to begin playing without confusion. It should explain who the player is, what Code Colony is about, how missions work, and why writing Java in the browser matters.

This screen is part of the product experience and part of the teaching experience. It should reduce uncertainty before the learner sees the first coding task.

## Scope

This spec defines:

- the purpose of the intro screen
- the content the player should see before Mission 01
- the intended player flow from intro to first mission
- the level of narrative and instruction appropriate for the opening

This spec does not define:

- mission-specific rules beyond the handoff into Mission 01
- the detailed visual design system
- save slots, profiles, or account features
- a full mission selection screen or world map

## Design Goals

The intro screen should:

1. establish the player's role as a remote engineer on a central station
2. explain the silent colony situation in a short, readable way
3. show that the game is played by entering small Java commands in the browser
4. reduce fear for beginners by making the first step feel small and achievable
5. hand off cleanly into Mission 01

## Player Questions To Answer

Before the player starts Mission 01, the intro should answer these questions:

- Who am I?
- Where am I?
- What happened to the colony?
- What is a CORE unit?
- How do I play this game?
- What will I be doing first?

If the screen does not answer these clearly, it is not doing its job.

## Intro Flow

The intended opening flow is:

1. The player lands on the intro screen.
2. The player reads a short narrative setup.
3. The player reads a short explanation of how the game works.
4. The player sees what is expected in the first mission.
5. The player clicks `Start Mission 01`.
6. The player enters the Mission 01 screen.

This flow should be short enough to read in one sitting without scrolling through a large wall of text.

## Required Content

The intro screen should include the following content blocks.

### 1. Title And Premise

This section should introduce Code Colony as a programming-driven mission game.

It should establish:

- the colony has gone silent
- the player is operating remotely from a central station
- only partial systems have been restored
- CORE units are the player's way of acting on the colony

The intro should define `CORE` as `Colony Operations and Repair Engineer` the first time the term is introduced.

The tone should be intriguing, not dramatic or frightening.

### 2. Your Role

This section should explain that the player is a remote engineer.

It should frame the player as someone who:

- restores access to damaged systems
- sends instructions to CORE maintenance robots
- investigates what caused the colony failure

This section should support the player fantasy without introducing unnecessary lore.

### 3. How Missions Work

This section should explain the interaction loop in simple language.

It should communicate that the player will:

- read a mission objective
- enter a small amount of Java code in the browser
- click `Run`
- watch the result on screen
- revise the code if needed

It should explicitly say that the player does not need an IDE or local Java setup.

### 4. First Mission Setup

This section should prepare the player for Mission 01.

It should communicate that:

- the first mission is intentionally small
- the first task is to bring a docked CORE online
- the mission expects one simple method call

This should lower the barrier for beginners before they see the mission editor.

### 5. Primary Call To Action

The intro screen should end with one clear action:

- `Start Mission 01`

There should not be multiple competing actions in the MVP.

## Content Constraints

The intro screen should be concise.

For the first version:

- prefer short paragraphs over dense lore
- avoid more than one main screen of content on a typical laptop display
- avoid introducing unexplained game terms beyond `CORE`
- avoid teaching Java syntax in depth here

The goal is orientation, not instruction overload.

## Relationship To Mission 01

The intro screen should not replace the Mission 01 briefing.

The intro screen provides game-level context.

Mission 01 still needs its own:

- mission title
- local narrative context
- objective
- hints
- command reference

The split should be:

- intro screen explains the game and the player's role
- mission screen explains the immediate task

## Non-Goals For The First Version

The intro screen should not try to become:

- a main menu with many destinations
- a lore archive
- a tutorial level separate from Mission 01
- a character customisation screen
- a mission selection map

Those may come later if the product needs them, but they are not needed for the current vertical slice.

## Success Criteria

The intro screen is successful if:

- a new player understands the premise before seeing code
- a beginner understands that the game is played through small Java commands
- the transition into Mission 01 feels natural
- the player is not overwhelmed before the first task

## Open Questions

- Whether the intro should include a static visual of the station or colony
- Whether the first screen should remember completion and later become skippable
- Whether a very short message from command or station control would improve tone
