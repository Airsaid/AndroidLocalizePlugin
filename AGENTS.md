# Repository Guidelines

## Project Structure & Module Organization
Core plugin code lives in `src/main/kotlin/com/airsaid/localization`, split by feature (actions, translate services, Compose UI, utilities). IDE registrations and icons reside in `src/main/resources/META-INF/plugin.xml` and `src/main/resources/icons`. Tests mirror production packages in `src/test/kotlin`. Screenshots and demos stay in `preview/`. Build inputs sit at the root (`build.gradle.kts`, `settings.gradle.kts`, `gradle/`, `gradle.properties`, `qodana.yml`, `codecov.yml`)—update them together when changing tooling or release metadata.

## Build, Test & Development Commands
- `./gradlew buildPlugin` – produce the distributable ZIP in `build/distributions/`.
- `./gradlew check` – execute unit tests, static checks, and coverage verification.
- `./gradlew runIde` – start a sandbox IDE with the plugin; use `./gradlew runIdeForUiTests` for Robot scenarios.
- `./gradlew qodanaScan` – run JetBrains Qodana and review results under `build/reports/qodana/`.
- `./gradlew publishPlugin` – push to Marketplace (requires `CERTIFICATE_CHAIN`, `PRIVATE_KEY`, `PRIVATE_KEY_PASSWORD`, `PUBLISH_TOKEN`).

## Coding Style & Naming Conventions
Follow JetBrains Kotlin style: four-space indentation, trailing commas in multiline calls, `UpperCamelCase` classes, `lowerCamelCase` members, `SCREAMING_SNAKE_CASE` constants. Compose UI components belong under `ui/` and should keep state hoisted for previewability. Translator implementations stay in `translate/impl/<provider>` with provider-specific config surfaced through `config/`. Keep resource bundle keys descriptive (`language.selector.title`) and mirror Android string identifiers when bridging.

## Testing Guidelines
JUnit 5 powers tests; suffix files with `Test` and place them in matching packages under `src/test/kotlin`. Translator suites should extend `AbstractTranslatorNetworkTest` with mocked HTTP clients to avoid external calls. Run `./gradlew test` before pushing and generate coverage with `./gradlew koverHtmlReport` when altering translation flows or caching. Refresh fixtures in `src/test/resources` if request/response formats change.

## Commit & Pull Request Guidelines
Write imperative, concise commit subjects (e.g., “Add flag emojis to language selectors”) and expand on breaking changes or migrations in the body. Every PR should describe the motivation, user impact, linked issues, and attach screenshots or recordings for UI tweaks. Confirm `./gradlew check` (and `qodanaScan` when touching inspection-sensitive code) before review. Coordinate version bumps via `gradle.properties` and document changes in `CHANGELOG.md`.

## Security & Configuration Tips
Keep translation service credentials out of Git; configure them through IDE settings or environment variables. Publishing secrets belong in local key stores or CI secrets, never in commits. Double-check `plugin.xml` edits, since misconfigured actions or services can prevent IDE startup.
