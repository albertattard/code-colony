# Content Authoring

Player-facing runtime narrative lives under `src/main/resources/content/`.

This content is separate from `docs/spec/`:

- `docs/spec/` defines what the content must communicate
- `src/main/resources/content/` stores the text shown to the player

## MVP Markdown Format

The first version supports a small Markdown subset:

- one `#` title at the top of the file
- named `##` sections
- paragraphs separated by blank lines
- unordered lists (`- item` or `* item`)
- ordered lists (`1. item`)
- fenced code blocks (```java ... ```)
- horizontal rules (`---`)
- inline emphasis using `*text*`
- inline code using `` `code` ``
- inline links using `[label](https://example.com)`

This keeps the content readable for authors, renderable in the browser, and easy to reuse for text-to-speech workflows.

## Current Layout

- `intro/` for game-level entry content
- `missions/<mission-id>/` for mission-local briefings, explain-dialog content, and future mission content
- `lore/` reserved for future logs, fragments, and other narrative material
