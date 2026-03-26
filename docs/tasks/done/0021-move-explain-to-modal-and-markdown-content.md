# Task 0021: Move Explain To Modal And Markdown Content

## Summary

Move the `Explain` guidance into a modal dialog and source mission explanation text from markdown content files.

## Why

The run feedback panel should stay focused on execution feedback, while explanation content should be readable in a dedicated teaching-focused surface. Storing explanation text in markdown keeps learner-facing copy easy to update.

## In Scope

- rendering `Explain` output in a modal dialog
- adding close controls and Escape-close behavior for the explain modal
- sourcing explain content from `src/main/resources/content/missions/<mission-id>/explain.md`
- updating tests for the new explain rendering path
- updating gameplay spec and content authoring documentation

## Out Of Scope

- changing mission validation rules
- generating explanation content dynamically from learner code

## Relevant Specs

- `docs/spec/gameplay-spec.md`

## Acceptance Criteria

- [x] Clicking `Explain` opens a dialog with mission-guided explanation content.
- [x] Explain text comes from mission markdown content files.
- [x] Existing run feedback remains in the feedback panel.
- [x] Automated tests cover markdown-backed explanation loading and explain endpoint rendering.
