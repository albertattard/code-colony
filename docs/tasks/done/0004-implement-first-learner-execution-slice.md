# Task 0004: Implement First Learner Execution Slice

## Status

Done

## Summary

Replace the placeholder string-matching run flow with a first end-to-end slice that compiles and executes learner Java code out of process for Mission 01, where the learner is only expected to call `Core.connect();`.

## Motivation

Code Colony is meant to teach Java through actual programming, not through text pattern matching. The current placeholder run endpoint is useful for UI iteration, but it does not yet deliver real compile feedback, runtime behavior, or mission validation based on submitted code.

This task should establish the first thin vertical slice of the execution model described in ADR 0002 so the product can move from a mock interaction to real learner code handling.

For Mission 01, success should depend on observing the correct API behavior at runtime, not on parsing the submitted source text.

## Related Documents

- `docs/spec/student-api-spec.md`
- `docs/spec/gameplay-spec.md`
- `docs/spec/missions/mission-01-wake-the-core.md`
- `docs/adr/0002-run-learner-code-out-of-process.md`

## Scope

This task should cover:

- accepting learner code for Mission 01 as real Java source
- writing the submission to generated source files in a temporary working area
- compiling the learner code out of process
- executing the compiled learner code in a separate JVM process
- capturing compile errors and runtime failures in a structured form
- exposing the Mission 01 student-facing entry point `Core.connect()`
- attaching a run-scoped mission simulator inside the worker JVM
- routing learner API calls to the simulator as commands
- recording mission-relevant events produced by the simulator for the Mission 01 flow
- validating Mission 01 by confirming that `Core.connect()` was called exactly once during execution
- treating repeated `Core.connect()` calls as an error for Mission 01
- returning learner-facing feedback through the existing browser flow

## Out Of Scope

This task should not include:

- a full sandboxing solution
- container-based isolation
- support for multiple simultaneous mission types
- persistence of learner submissions
- a broad execution API for future missions beyond what Mission 01 needs

## Acceptance Criteria

- [x] A Mission 01 submission is compiled as Java code rather than checked through string matching.
- [x] Learner code executes outside the web server JVM.
- [x] Compile failures are shown as learner-facing feedback.
- [x] Runtime failures are shown as learner-facing feedback.
- [x] Mission success is determined from observed execution behavior.
- [x] A Mission 01 run that calls `Core.connect();` once changes the visible CORE status to online.
- [x] A Mission 01 run that does not call `Core.connect();` leaves the CORE offline and shows learner-facing feedback.
- [x] A Mission 01 run that calls `Core.connect()` more than once is treated as an error and shows learner-facing feedback.
- [x] The existing browser flow continues to work for Mission 01.
- [x] Automated tests cover the main success and failure paths.

## Notes

This task should keep the execution model intentionally narrow. The goal is to prove the architecture with one mission and one student-facing API slice before expanding the runtime and sandbox model.

The first slice should not yet require storing the connected CORE in a variable. Support for instance-based actions such as `move()` and rotation belongs to a later task once the initial connection flow is working.

The intended shape is:

- generated learner code calls the static `Core` API
- the static API sends commands to the run's mission simulator
- the simulator updates state and records mission events
- validation and UI feedback are derived from the resulting state and event stream
