# Code Colony

Code Colony is a programming-driven strategy game about investigating and restoring a silent off-world colony.

Players take the role of a remote engineer and control CORE maintenance units by writing Java code. The game is intended as a beginner-friendly way to introduce Java to kids and other early learners through short, mission-based challenges.

## Current Status

Code Colony is in active development with a playable browser prototype and mission flow implemented.

Current implementation includes:

- Session-based mission progression in the browser
- Mission execution of learner Java snippets
- Mission feedback, status panel updates, and simulation grid playback
- Mission content and explanation markdown
- Automated unit, integration, and browser smoke test coverage

The intended product remains a browser-based game where players write Java code, run it, observe outcomes, and iterate toward mission objectives.

## Quick Start

### Prerequisites

- Java 25
- A POSIX shell environment (project uses `./mvnw`)

### Build And Verify

```bash
./mvnw clean verify
```

### Run The App

```bash
./mvnw spring-boot:run
```

Open `http://localhost:8080`.

## Development Workflow

- Use `./mvnw test` for fast unit and integration feedback.
- Use `./mvnw clean verify` for full verification, including browser-tagged `e2e` coverage.
- Follow spec-first, task-tracked changes under `docs/spec/` and `docs/tasks/`.

## Project Structure

- `src/main/java/game/codecolony/runtime/`
  Engine-level commands, events, and simulator contracts.
- `src/main/java/game/codecolony/mission/`
  Mission state models and mission implementations (organized by mission package).
- `src/main/java/game/codecolony/student/`
  Learner-facing API surface (`Core`).
- `src/main/resources/templates/`
  Server-rendered mission and layout templates.
- `src/main/resources/content/`
  Player-facing markdown content (briefings and explanations).

## Documentation

- [Agent Guide](AGENTS.md)
- [Specifications](docs/spec/README.md)
- [Architecture Decision Records](docs/adr/README.md)
- `docs/tasks/` for backlog, in-progress, and completed task documents

## Content Assets

- Runtime audio assets should live under `src/main/resources/static/audio/`.
- Briefing voice tracks should be organized by purpose, for example `src/main/resources/static/audio/briefings/intro.mp3`.
- Written briefing text remains the source of truth. Generated audio is a player-facing asset derived from that text.
- Contributor utilities such as text-to-speech helpers should live under `tools/`.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE).
