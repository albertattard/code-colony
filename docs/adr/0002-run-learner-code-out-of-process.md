# ADR 0002: Run Learner Code Out Of Process

## Status

Accepted

## Context

Code Colony teaches Java by having the learner write code that controls a CORE unit through the browser. The current placeholder flow accepts text input and checks for expected string patterns, but that is not sufficient for the intended product.

The game needs to execute real Java code so learners receive authentic compile errors, runtime feedback, and mission results based on actual program behavior. At the same time, learner code must not execute inside the same JVM process as the web server because that would couple untrusted or faulty learner code directly to the running application.

This decision needs to support these goals:

- keep the learner experience grounded in real Java
- provide compile-time and runtime feedback that matches the submitted program
- protect the web application from learner code failures as much as practical
- allow mission validation to be based on observed behavior rather than string matching
- leave room for stronger isolation later if deployment requirements grow

The project is still at an early stage, so the first decision should establish a sound execution boundary without prematurely designing the full sandboxing model.

## Decision

Learner-submitted Java code will be compiled and executed out of process.

For the initial approach:

- the server will write learner submissions to generated Java source files
- learner submissions will be compiled as Java code rather than interpreted through string matching
- compilation will happen outside the web server process
- execution will happen in a separate JVM process from the web application
- the execution environment will expose only the intended student-facing API and mission runtime hooks
- mission outcomes will be validated from execution results and recorded actions, not from matching submitted text

The web application remains responsible for orchestration only:

- accepting the submission
- preparing the execution request
- invoking compilation and execution
- collecting compile errors, runtime failures, logs, and mission results
- returning structured feedback to the browser

This ADR does not define the final sandboxing mechanism. Stronger isolation, such as containerization or OS-level controls, may be introduced later in a superseding decision.

## Consequences

- Learners receive feedback based on real Java compilation and execution.
- The architecture moves closer to the actual educational goals of the game.
- The web server is less exposed to learner code failures because execution is separated from the application JVM.
- Mission validation can evolve around behavior and world state instead of fragile string matching.
- The implementation becomes more complex because it must manage source generation, compilation, process execution, timeouts, and result collection.
- Running learner code on the same host still carries security and resource risks, even when it is out of process.
- A later ADR will likely be needed to define stronger sandboxing, execution limits, and operational controls.
