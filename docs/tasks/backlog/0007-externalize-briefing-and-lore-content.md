# Task 0007: Externalize Briefing And Lore Content

## Status

Backlog

## Summary

Move player-facing intro, mission briefing, and early lore text out of Java and HTML files into resource-backed content files under `src/main/resources/content/`.

## Motivation

Briefings and lore are part of the product, but they should not require code edits for every narrative refinement. The project also uses text-to-speech, which is easier to manage when the approved written text exists in one canonical source.

Externalizing content will make narrative review simpler, reduce duplication between written and voiced briefings, and keep templates focused on presentation rather than authored copy.

## Related Documents

- `docs/adr/0003-store-player-facing-narrative-as-resource-content.md`
- `docs/spec/story-spec.md`
- `docs/spec/gameplay-spec.md`
- `docs/spec/game-intro-spec.md`
- `docs/spec/missions/mission-01-wake-the-core.md`

## Scope

This task should cover:

- defining the initial runtime content directory structure under `src/main/resources/content/`
- moving the current intro briefing into content files
- moving the current Mission 01 briefing text into content files
- choosing a small Markdown subset or equivalent rendering rules for the MVP
- loading the content through the web layer without hardcoding the final copy in templates
- keeping the written content as the source for future text-to-speech generation

## Out Of Scope

This task should not include:

- a full localization system
- a complete CMS or authoring tool
- migration of every future mission before those missions exist
- large-scale lore expansion beyond the currently implemented slices

## Acceptance Criteria

- [ ] Runtime narrative content lives under `src/main/resources/content/`.
- [ ] The intro screen renders its briefing from content files.
- [ ] Mission 01 renders its briefing from content files.
- [ ] The chosen authoring format is documented well enough to extend consistently.
- [ ] Existing automated tests continue to pass after the content move.

## Notes

Keep the first version small. The goal is to establish a clean pattern for content-backed narrative, not to build a full content pipeline.
