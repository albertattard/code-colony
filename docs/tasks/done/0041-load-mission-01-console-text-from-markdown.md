# Task 0041: Load Mission 01 Console Text From Markdown

## Summary

Move Mission 01 code-panel content (hints and available command descriptions) from hardcoded Java constants to mission Markdown content.

## Why

Learner-facing copy should be editable by content changes, not Java edits. This keeps mission text iteration fast and consistent with existing briefing/explain content workflows.

## In Scope

- update spec language to state that command-reference and hint text should come from mission content files
- load Mission 01 console hints/commands from Markdown
- keep a safe runtime fallback to existing hardcoded values for Mission 01
- add tests for the new content-loading behavior

## Out Of Scope

- migrating Mission 02 and Mission 03 console text
- redesigning mission template labels such as `Code Console`

## Acceptance Criteria

- [x] Mission 01 hints in the code panel are sourced from mission Markdown content.
- [x] Mission 01 command reference entries in the code panel are sourced from mission Markdown content.
- [x] Mission 01 still renders sensible defaults if the new Markdown sections are missing.
- [x] `./mvnw clean verify` passes.
