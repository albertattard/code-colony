# ADR 0003: Store Player-Facing Narrative As Resource Content

## Status

Accepted

## Context

Code Colony uses narrative text in several places, including the game intro, mission briefings, objectives, hints, and future lore fragments. This text is part of the player experience, but it is currently at risk of being scattered across templates and Java classes.

The project also uses text-to-speech for voiced briefings. That workflow is easier when the approved written text exists in a single editable source that can be reviewed, rendered in the game, and reused to generate audio.

The project already separates design documentation under `docs/` from runtime code under `src/main/`. A similar separation is needed for player-facing narrative so that specifications remain design contracts while the game loads its actual text from application resources.

## Decision

Store player-facing narrative content as runtime resource files under `src/main/resources/content/`.

Use this content area for text that the running game presents directly to the player, including:

- intro briefings
- mission briefings
- mission objectives and hints where content-driven storage is useful
- lore fragments, logs, and similar narrative material

Keep `docs/spec/` as the canonical design source for what content must exist, what it should communicate, and what constraints it must follow. Do not treat specification files as runtime content.

Use Markdown as the default authoring format for narrative content unless a later need requires a different structured format. Early content should prefer simple paragraphs and a limited Markdown subset so the same source remains easy to render in the browser and reuse for text-to-speech workflows.

Use a content structure organized by gameplay surface, for example:

- `src/main/resources/content/intro/briefing.md`
- `src/main/resources/content/missions/mission-01/content.md`
- `src/main/resources/content/missions/mission-01/objective.md`
- `src/main/resources/content/lore/...`

## Consequences

Narrative text becomes easier to edit, review, version, and reuse without changing Java or HTML files for every wording update.

Text-to-speech generation can operate from the same approved source text that the game renders, reducing duplication and drift between spoken and written versions.

The application will need a content-loading path and a small rendering strategy for Markdown or the chosen subset. This adds some implementation work, but it keeps content architecture cleaner as the game grows.

Specifications remain concise and decision-oriented because they define intent and constraints rather than storing every final in-game paragraph.
