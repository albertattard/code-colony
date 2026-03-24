# ADR 0001: Choose A Browser-Based Java Stack For The MVP

## Status

Accepted

## Context

Code Colony is intended to be played by learners through the browser. Players should not need to install Java, use an IDE, or build the game locally. They should enter Java code into a provided interface, run it, watch the resulting actions play out on screen, and receive feedback about mission success or failure.

The main reason for choosing a browser-based approach is to reduce the entry barrier for players. If all a learner needs is a browser, the game becomes easier to access in classrooms, workshops, and at home.

This creates a few immediate architectural constraints:

- the player experience must be browser-based
- the learner programming language remains Java
- code execution must happen behind the scenes, not on the learner's machine
- the initial stack should stay as simple as possible while the game design is still evolving

At this stage, there is no need for a database. Persistence requirements are not yet defined strongly enough to justify adding one.

## Decision

For the MVP, Code Colony will use:

- Java 25 as the primary implementation language
- Spring Boot as the backend framework
- Thymeleaf for server-side HTML rendering
- HTMX for incremental browser interactions
- no database initially

The game will be delivered as a browser-based application. Learners will interact with a browser UI that includes a code entry area, a run action, visual mission feedback, and simulation results.

The backend will remain responsible for:

- mission logic
- learner code handling and execution orchestration
- validating mission outcomes
- returning feedback to the browser

## Consequences

- The player entry barrier is reduced because all they need is a browser.
- The player experience stays simple: open a browser and play.
- Java remains both the implementation language and the learner language.
- The MVP avoids the extra complexity of a SPA frontend and a database.
- Rich client-side interactivity may be harder than with a SPA-based frontend.
- A separate decision is still required for safe compilation and execution of learner-submitted Java.
- If the UI later becomes highly dynamic, the frontend approach may need to evolve.
