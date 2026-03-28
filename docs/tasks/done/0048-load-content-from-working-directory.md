# Task 0048: Load Content From Working Directory

## Summary

Load mission and narrative files from the `content/` directory in the current working directory.

## Why

This allows editing mission files without recompiling Java code.

## In Scope

- read narrative markdown from `./content/...` first
- read `map.yaml` from `./content/...` first
- read `mission.yaml` from `./content/...` first
- keep classpath fallback for compatibility
- update content docs to describe lookup order

## Out Of Scope

- introducing file watching or hot-reload
- removing classpath content support

## Acceptance Criteria

- [x] Narrative, map, and mission behavior loaders prefer `./content`.
- [x] Existing behavior remains compatible through classpath fallback.
- [x] `./mvnw clean verify` passes.
