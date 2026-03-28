# ADR 0005: Store Runtime Content In Working Directory

## Status

Accepted

## Context

The game now uses data-driven mission files and player-facing Markdown content at runtime. Keeping runtime content under `src/main/resources/content/` ties normal content editing to the Java resource pipeline.

That makes contributor workflows harder than necessary when the goal is to tweak mission text or mission files quickly and run again.

The project needs a content location that is directly editable without recompiling.

## Decision

Store runtime game content under top-level `content/` in the project working directory.

Runtime loaders should read mission and narrative files from filesystem paths under `content/` and fail fast when required files are missing.

Remove classpath fallback for runtime content loading.

This decision supersedes ADR 0003 for runtime content location.

## Consequences

Content authors can change mission and narrative files without rebuilding Java resources.

Runtime behavior becomes explicit: the game depends on the `content/` directory being present in the working directory.

Packaging and deployment flows must include `content/` as runtime data, not as classpath resources.

Missing or malformed content files now surface as direct filesystem load failures, which keeps diagnostics clear and predictable.
