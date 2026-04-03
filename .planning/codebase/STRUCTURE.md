# Codebase Structure

**Analysis Date:** 2026-04-03

## Directory Layout

```
unicenta-opos/
├── src/
│   ├── main/
│   │   ├── java/com/unicenta/
│   │   │   ├── pos/               # POS core modules
│   │   │   ├── beans/             # Swing/JavaFX UI components
│   │   │   ├── basic/             # Base classes and utilities
│   │   │   ├── data/              # Database access and models
│   │   │   ├── editor/            # GUI editors for entities
│   │   │   ├── format/            # Formatting and conversion
│   │   │   ├── orderpop/          # Remote Order Display
│   │   │   └── plugins/           # Plugin interfaces (Application, Metrics)
│   │   └── resources/
│   │       ├── com/unicenta/      # Application resources
│   │       ├── fxml/              # JavaFX FXML layouts
│   │       ├── styles/            # CSS stylesheets
│   │       └── *_messages*.properties  # i18n messages
│   ├── test/
│   │   └── java/com/unicenta/    # Unit tests
│   ├── other/                     # Platform-specific binaries, images
│   └── scripts/                   # Database initialization scripts
├── pom.xml                        # Maven configuration
├── README.md                      # Project documentation
└── .planning/codebase/            # GSD analysis documents
```

## Directory Purposes

**`src/main/java/com/unicenta/pos/`** — POS Core Modules (47 subdirectories)
- Purpose: Main Point-of-Sale application logic
- Contains: Sales screens, admin panels, ticket management, reporting, payment processing
- Key subdirectories:
  - `forms/` — Application configuration, data logic, bean factories, window management
  - `sales/` — Sales UI components (ticket bags, payment editors, product selection)
  - `ticket/` — Ticket and transaction models (TicketInfo, TicketLineInfo, PaymentInfo)
  - `catalog/` — Product catalog, categories, stock management
  - `inventory/` — Inventory operations, stock adjustments, stock diaries
  - `customers/` — Customer management, loyalty, debt tracking
  - `payment/` — Payment methods (cash, card, vouchers, WooCommerce)
  - `admin/` — Administrative functions and panels
  - `reports/` — JasperReports generation and report rendering
  - `printer/` — Receipt/ticket printing, hardware integration (JavaPOS, ESC-POS)
  - `mant/` — Maintenance operations, database management
  - `suppliers/` — Supplier management and ordering
  - `panels/` — Reusable UI panels (money handling, product finder, ticket finder)

**`src/main/java/com/unicenta/data/`** — Database Access Layer
- Purpose: SQL abstraction, JDBC operations, data serialization
- Contains: Session management, prepared statements, type mapping, database-specific implementations
- Key subdirectories:
  - `loader/` — SQL builders, sentence objects (PreparedSentence, SentenceList), serializers, database implementations (SessionDB*)
  - `model/` — Schema definitions (Row, Field, Column, Table, PrimaryKey)
  - `user/` — UI integration for data loading (ListProvider, SaveProvider)
  - `gui/` — Data-binding UI components (renderers, cell editors)

**`src/main/java/com/unicenta/beans/`** — UI Components
- Purpose: Reusable Swing UI beans and components
- Contains: Custom JPanel/JComponent implementations, property editors, renderers
- Note: Separate from domain models; these are purely UI presentation classes

**`src/main/java/com/unicenta/basic/`** — Base Classes
- Purpose: Foundation exceptions and utilities
- Contains: `BasicException` (checked exception base class)

**`src/main/java/com/unicenta/format/`** — Formatting & Localization
- Purpose: Date, currency, number formatting, locale management
- Contains: `Formats` utility class with format methods, `AppLocal` for i18n

**`src/main/resources/`** — Application Resources
- Purpose: Externalized strings, templates, reports, configuration
- Key subdirectories:
  - `com/unicenta/images/` — Application icons and graphics
  - `com/unicenta/pos/templates/` — Receipt/ticket templates (XML-based, 50+ templates)
  - `com/unicenta/reports/` — JasperReports definitions (250+ report files)
  - `com/unicenta/pos/scripts/` — Database initialization SQL scripts
  - Root `.properties` files — Localization bundles (pos_messages_*.properties, data_messages_*.properties, erp_messages_*.properties)

**`src/scripts/`** — Database Scripts
- Purpose: Database initialization and configuration
- Contains: `configure.sh`, `start.sh` (shell scripts for deployment)

**`src/test/`** — Unit Tests
- Purpose: Test coverage for core components
- Key test files:
  - `TicketInfoTest.java` — TicketInfo serialization/deserialization
  - `StaticSentenceTest.java` — SQL building and sentence execution
  - `PackageScanTest.java` — Plugin discovery validation

## Key File Locations

**Entry Points:**
- `src/main/java/com/unicenta/pos/forms/StartPOS.java` — Main application entry point, instance registration, theme setup
- `src/main/java/com/unicenta/pos/forms/AppConfig.java` — Configuration loading from ~/.unicentaopos.properties

**Configuration:**
- `pom.xml` — Maven build configuration (Java 11, dependencies: JavaFX 11, MariaDB JDBC, JasperReports, Logback)
- `src/main/resources/logback.groovy` — Logging configuration

**Core Logic:**
- `src/main/java/com/unicenta/pos/forms/DataLogicSales.java` — Sales transactions, tickets, payments (107KB, largest business logic file)
- `src/main/java/com/unicenta/pos/forms/DataLogicOrders.java` — Order management
- `src/main/java/com/unicenta/pos/ticket/TicketInfo.java` — Ticket domain model with serialization
- `src/main/java/com/unicenta/data/loader/Session.java` — Database session management and connection pooling
- `src/main/java/com/unicenta/data/loader/PreparedSentence.java` — SQL statement preparation and execution

**Testing:**
- `src/test/java/com/unicenta/pos/ticket/TicketInfoTest.java` — Ticket serialization tests
- `src/test/java/com/unicenta/data/loader/StaticSentenceTest.java` — SQL generation tests

**Localization:**
- `src/main/resources/pos_messages.properties` — Main UI message strings (55KB)
- `src/main/resources/data_messages.properties` — Data layer messages (3KB)
- Locale-specific variants: `pos_messages_nl.properties`, `pos_messages_es.properties`, `pos_messages_fr.properties`, etc.

## Naming Conventions

**Files:**
- `*Info.java` — Domain model/DTO classes (e.g., `TicketInfo`, `ProductInfo`, `CustomerInfo`)
- `*Edit.java` — Form/dialog editors (e.g., `ProductInfoEdit`, `JPanelTable`)
- `*Filter.java` — Product/category filter panels
- `DataLogic*.java` — Business logic service classes
- `SessionDB*.java` — Database-specific implementations (SessionDBMySQL, SessionDBPostgreSQL, etc.)
- `Serializer*.java` — Type-specific data serializers
- `JPanelXxx.java` — Main UI panels (Swing/AWT naming convention)
- `JxxxBag.java` — Container panels for managing collections (e.g., `JTicketsBag`)
- `*Finder.java` — Search/lookup dialogs

**Directories:**
- `pos/` — POS domain modules use functional names (sales, inventory, customers, admin, reports)
- `data/loader/` — Database abstraction utilities (PreparedSentence, Session, Serializers)
- `forms/` — Application-level classes (AppConfig, DataLogic*, BeanFactory implementations)

**Classes:**
- Use CamelCase for public classes
- `Info` suffix for domain models/DTOs
- `Impl` suffix for implementation classes
- Avoid prefixes; use package structure for organization

## Where to Add New Code

**New Feature (e.g., new sales module):**
- Primary code: `src/main/java/com/unicenta/pos/{feature-name}/`
- Business logic: `src/main/java/com/unicenta/pos/forms/DataLogic{Feature}.java`
- Domain models: `src/main/java/com/unicenta/pos/{feature-name}/xxx{Info}.java`
- UI panels: `src/main/java/com/unicenta/pos/{feature-name}/JPanel{Feature}.java`
- Tests: `src/test/java/com/unicenta/pos/{feature-name}/`
- Resources: `src/main/resources/com/unicenta/pos/{feature-name}/`

**New UI Component/Panel:**
- Implementation: `src/main/java/com/unicenta/pos/sales/JPanel{ComponentName}.java` (for sales screens) or appropriate module
- Form file: `src/main/java/com/unicenta/pos/sales/JPanel{ComponentName}.form` (NetBeans GUI builder format)
- Messages: Add keys to `src/main/resources/pos_messages.properties` and locale variants

**Shared Utilities:**
- Format utilities: `src/main/java/com/unicenta/format/`
- Basic exceptions: `src/main/java/com/unicenta/basic/`
- UI components: `src/main/java/com/unicenta/beans/`
- General utilities: Create in appropriate existing module or `pos/util/`

**Database Operations:**
- New Row/Field definitions: Extend in DataLogic* class
- New serializers: `src/main/java/com/unicenta/data/loader/SerializerRead*.java`
- New sentence types: `src/main/java/com/unicenta/data/loader/Sentence*.java`

**Internationalization:**
- UI strings: Add key=value to `src/main/resources/pos_messages.properties`
- Translations: Duplicate to `pos_messages_{locale}.properties` (e.g., `pos_messages_nl.properties`)
- Messages property key naming: Camel case, hierarchical with dots (e.g., `label.product.name`, `button.save`)

**Reports:**
- JasperReports definitions: `src/main/resources/com/unicenta/reports/{name}.jrxml`
- Band definitions/scripts: Referenced from jrxml files
- Business script logic: `src/main/java/com/unicenta/pos/reports/` (if custom report generators needed)

## Special Directories

**`src/other/`:**
- Purpose: Non-source build artifacts and assets
- Generated: Populated during development, contains platform-specific native libraries (Linux binaries, Windows DLLs, macOS dylibs)
- Committed: Some base images committed; generated binaries typically .gitignore'd

**`src/test/java/com/unicenta/data/loader/`:**
- Purpose: Data access layer tests
- Contains: SQL generation tests, serializer tests, sentence execution tests
- Testing strategy: Unit tests with mocked database; integration tests use embedded Derby

**`src/main/resources/com/unicenta/pos/scripts/`:**
- Purpose: SQL database initialization and migration scripts
- Contains: Schema creation, initial data, database upgrade scripts per version
- Executed during: First application startup or database wizard

---

*Structure analysis: 2026-04-03*
