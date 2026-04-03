# uniCenta oPOS — Project Context

## Over dit project

Fork van uniCenta oPOS (open source POS-systeem) voor HSC Jeka, een honkbal/softbalvereniging in Breda.

- **Taal**: Java 11
- **UI**: JavaFX 11 + Swing + FlatLaf
- **Build**: Maven (`mvn clean package`)
- **Database**: MariaDB (primair), MySQL, PostgreSQL, Derby, SQLite
- **Licentie**: GPL v3
- **Entry point**: `com.unicenta.pos.forms.StartPOS`

## Conventies

- Communicatie en documentatie in het Nederlands
- Commit messages in het Nederlands
- Code en variabelenamen in het Engels (bestaande conventie volgen)
- Geen onnodige refactoring van upstream code

## Bouwen

```bash
mvn clean package
java -jar ./target/unicentaopos.jar
```

## Testen

```bash
mvn test                          # Alle tests
mvn test -Dtest=ClassName         # Specifieke testklasse
open target/site/jacoco/index.html # Coverage rapport (na mvn test)
```

- JUnit 4 + Mockito 4.2 + JaCoCo coverage
- Tests in `src/test/java/com/unicenta/` — spiegelt main structuur
- Codecov badge + CI check op elke PR

## Structuur

- `src/main/java/com/unicenta/pos/` — POS kernmodules
- `src/main/java/com/unicenta/data/` — Data-access layer
- `src/main/java/com/unicenta/beans/` — UI-componenten
- `src/main/resources/` — Rapporten, templates, lokalisaties
- `src/scripts/` — Start- en configuratiescripts (bash/bat)
- `.planning/codebase/` — Architectuur- en codebasis-documentatie

## CI/CD

- GitHub Actions: `ci.yml` (build + test + Codecov), `semgrep.yml` (SAST)
- Branch protection op `main` — altijd via PR
- CodeRabbit automatische code review op PRs

## Gotchas

- App vereist database (MariaDB/MySQL) om te starten — zonder DB geen UI
- `mockito-junit-jupiter` staat in pom.xml maar is ongebruikt (alle tests JUnit 4)
- Logging config is `logback.groovy` (Groovy DSL), niet XML
- `src/scripts/` zijn start scripts, niet SQL database scripts
