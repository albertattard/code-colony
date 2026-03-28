# Task 0049: Move Runtime Content Out Of Classpath

## Summary

Move runtime game content from `src/main/resources/content/` to top-level `content/` and remove classpath fallback loading.

## Spec Links

- `docs/spec/mission-definition-spec.md`
- `docs/spec/gameplay-spec.md`

## Why

Game content should be editable directly from the working directory without rebuilding or packaging resources.

## In Scope

- move runtime content directory to top-level `content/`
- update loaders to read only from `./content/...`
- remove classpath fallback logic
- update docs/spec references to the new location

## Out Of Scope

- hot reload / file watching
- changing mission map or narrative schemas

## Acceptance Criteria

- [x] Runtime content exists under top-level `content/`.
- [x] Narrative, map, and mission behavior loading reads from filesystem paths only.
- [x] Classpath fallback is removed.
- [x] Relevant docs/spec references point to `content/`.
- [x] `./mvnw clean verify` passes.
