# Task 0051: Lock No-Code Mission Contract (Stage 0)

## Summary

Define and lock the product/spec contract that adding a mission with existing mechanics requires only mission files under `content/missions/<mission-id>/`.

## Why

The migration needs a stable target before runtime refactoring.

## In Scope

- update specs to define no-code mission addition contract
- define explicit boundary for when Java changes are still required (new mechanics)
- link the contract to the migration epic

## Out Of Scope

- runtime code changes
- mission execution refactoring

## Acceptance Criteria

- [x] Specs explicitly state no-code mission addition for known mechanics.
- [x] Specs explicitly state when Java changes are required (new mechanics).
- [x] Migration epic links remain aligned.
