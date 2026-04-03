# Bijdragen aan uniCenta oPOS (HSC Jeka fork)

Bedankt voor je interesse om bij te dragen! Hieronder vind je de richtlijnen.

## Aan de slag

1. Fork de repository
2. Maak een feature branch aan (`git checkout -b feature/mijn-feature`)
3. Zorg dat je wijzigingen bouwen (`mvn clean package`)
4. Commit je wijzigingen met een duidelijke message
5. Push naar je fork en open een Pull Request

## Development setup

### Vereisten

- Java 11 (JDK)
- Maven 3.6+
- MariaDB of MySQL met schema `unicentaopos`

### Bouwen en testen

```bash
mvn clean package
java -jar ./target/unicentaopos.jar
```

## Richtlijnen

### Code

- Volg de bestaande codestijl in het project
- Schrijf Javadoc voor publieke methoden
- Voeg unit tests toe voor nieuwe functionaliteit
- Zorg dat bestaande tests blijven slagen

### Commits

- Gebruik duidelijke, beschrijvende commit messages
- Eén logische wijziging per commit
- Refereer naar issue-nummers waar relevant (`#123`)

### Pull Requests

- Gebruik het PR-template
- Beschrijf wat je wijziging doet en waarom
- Houd PRs zo klein en gericht mogelijk
- Zorg dat de build slaagt voordat je een review aanvraagt

### Issues

- Gebruik de issue-templates voor bugs en feature requests
- Controleer eerst of er al een vergelijkbaar issue bestaat
- Geef zoveel mogelijk context (versie, OS, database, stappen om te reproduceren)

## Licentie

Door bij te dragen ga je ermee akkoord dat je bijdragen worden gelicentieerd onder de [GPL v3](LICENSE).
