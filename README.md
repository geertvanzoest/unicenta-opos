# uniCenta oPOS — Open Source Point of Sale

[![Java](https://img.shields.io/badge/java-11-orange?logo=openjdk)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/maven-build-blue?logo=apachemaven)](https://maven.apache.org/)
[![JavaFX](https://img.shields.io/badge/javafx-11-green?logo=java)](https://openjfx.io/)
[![License: GPL v3](https://img.shields.io/badge/license-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

Commercial-grade open source kassasysteem voor retail en horeca. Gebruikt door duizenden retailers, bars, cafés, restaurants en winkels wereldwijd.

> **Fork status**: dit is een fork van [uniCenta oPOS](https://sourceforge.net/projects/unicentaopos/) (versie 5.0) voor eigen aanpassingen door [HSC Jeka](https://www.hscjeka.nl/).

## Overzicht

uniCenta oPOS is een enterprise-level Point of Sale systeem met een touch-friendly interface, gebouwd in Java 11 met JavaFX. Het ondersteunt meerdere databases, betaalmethoden en is volledig configureerbaar voor zowel retail als horeca-omgevingen.

## Kernfunctionaliteit

| Module | Omschrijving |
| ------ | ------------ |
| **Verkoop** | Touch-friendly verkoopscherm, bonnen, kortingen, retouren |
| **Voorraadbeheer** | Producten, categorieën, attributen, bundles, leveranciers |
| **Klanten** | Klantenbeheer, loyaliteit, schuldbeheer, prepaid |
| **Medewerkers** | Rollen, rechten, kassaafsluiting per medewerker |
| **Rapportage** | JasperReports-gebaseerde rapporten (verkoop, voorraad, BTW) |
| **Restaurant-modus** | Tafelbeheer, gasten, Remote Order Display, keukenbonnen |
| **Betalingen** | Contant, pin, vouchers, multi-payment, WooCommerce |
| **API's** | Customer API, Order API, Product API, WooCommerce-integratie |

## Tech stack

| Laag | Technologie |
| ---- | ----------- |
| **Taal** | Java 11 |
| **UI** | JavaFX 11, Swing, FlatLaf (modern look & feel) |
| **Build** | Maven |
| **Database** | MariaDB, MySQL, PostgreSQL, Apache Derby, SQLite |
| **Rapportage** | JasperReports 6.4 |
| **Grafieken** | JFreeChart |
| **Barcodes** | Barcode4J (EAN, UPC) |
| **Hardware** | JavaPOS (printers, kassalades, weegschalen, scanners) |
| **Serieel** | RxTx (seriële poorten) |
| **Logging** | Logback/SLF4J |

## Ondersteunde platformen

- Windows
- Linux (Debian/Ubuntu)
- macOS

## Aan de slag

### Vereisten

- Java 11 (JDK)
- Maven 3.6+
- MariaDB of MySQL (schema `unicentaopos` aanmaken)

### Bouwen

```bash
git clone git@github.com:geertvanzoest/unicenta-opos.git
cd unicenta-opos
mvn clean package
```

### Starten

```bash
java -jar ./target/unicentaopos.jar
```

Bij de eerste start wordt de database-configuratie wizard getoond.

## Projectstructuur

```text
src/
├── main/
│   ├── java/com/unicenta/
│   │   ├── pos/           # POS kernmodules (sales, forms, panels)
│   │   ├── basic/         # Basisklassen en utilities
│   │   ├── beans/         # UI-componenten (JavaBeans)
│   │   ├── data/          # Databasetoegang en datamodellen
│   │   ├── editor/        # Editors voor producten, klanten, etc.
│   │   ├── format/        # Formattering en conversie
│   │   └── orderpop/      # Remote Order Display
│   └── resources/
│       └── com/unicenta/  # Rapporten, templates, lokalisaties
├── other/
│   ├── Configs/           # Betalingsconfiguraties
│   ├── Templates/         # Bonsjablonen (US-varianten)
│   ├── license/           # Licentiebestand
│   └── Windows|Linux|Mac_OS_X/  # Platform-specifieke libraries
├── scripts/               # Database scripts
└── test/                  # Unit tests
```

## Upstream

- **Website**: <https://unicenta.com>
- **Broncode (origineel)**: <https://sourceforge.net/projects/unicentaopos/>
- **Changelog**: <https://unicenta.com/support/changelog-unicenta-opos/>

## Licentie

Dit project is gelicentieerd onder de [GNU General Public License v3.0](LICENSE) — zie het LICENSE bestand voor details.

Copyright 2009-2023 uniCenta. Fork-aanpassingen door HSC Jeka.
