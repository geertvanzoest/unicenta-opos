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

## Structuur

- `src/main/java/com/unicenta/pos/` — POS kernmodules
- `src/main/java/com/unicenta/data/` — Data-access layer
- `src/main/java/com/unicenta/beans/` — UI-componenten
- `src/main/resources/` — Rapporten, templates, lokalisaties
- `src/scripts/` — Database scripts
