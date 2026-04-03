# Architecture

**Analysis Date:** 2026-04-03

## Pattern Overview

**Overall:** Layered MVC with Bean Factory pattern for dependency injection. The application separates concerns into UI, business logic, and data access layers with support for multiple database backends via abstract factory patterns.

**Key Characteristics:**
- **Layered Architecture**: UI (Swing/JavaFX) → Business Logic (DataLogic*) → Data Access (Loader/Model) → Database
- **Plugin/Factory Pattern**: `BeanFactory` implementations for dynamic instance creation and database-specific implementations
- **Multi-Database Support**: Abstract database layers with platform-specific implementations (MySQL, MariaDB, PostgreSQL, Derby, SQLite)
- **Domain Models**: Info* classes (`TicketInfo`, `ProductInfo`, `CustomerInfo`) represent core business entities with serialization
- **Data Transfer Objects**: Separation between UI representation (Info classes) and database serialization (Row/Field model)

## Layers

**Presentation Layer (UI):**
- Purpose: Touch-friendly JavaFX and Swing interfaces, forms, dialogs, and visual components
- Location: `src/main/java/com/unicenta/pos/forms/`, `src/main/java/com/unicenta/pos/sales/`, `src/main/java/com/unicenta/beans/`
- Contains: Form panels (JPanel subclasses), filters, editors, main application window (`StartPOS`)
- Depends on: Business Logic (DataLogic*), UI components (beans/), Format utilities
- Used by: Application entry point, user interactions

**Business Logic Layer:**
- Purpose: Orchestrates business processes, enforces rules, coordinates between UI and data access
- Location: `src/main/java/com/unicenta/pos/forms/DataLogic*.java` (e.g., `DataLogicSales`, `DataLogicOrders`)
- Contains: Core business services that manage tickets, payments, inventory, customers
- Depends on: Data Access layer (Loader, Session), Domain models (Info* classes), Configuration (AppConfig)
- Used by: UI components, Forms layer

**Data Access Layer:**
- Purpose: SQL generation, prepared statements, database operations, session management
- Location: `src/main/java/com/unicenta/data/loader/`, `src/main/java/com/unicenta/data/model/`
- Contains: `Session`, `PreparedSentence`, `Row`, `Field`, `Table`, `Datas` type system, serializers
- Depends on: JDBC, database drivers (MariaDB, MySQL, PostgreSQL, Derby, SQLite)
- Used by: DataLogic* classes, business services

**Database Abstraction Layer:**
- Purpose: Hide database-specific differences behind factory pattern
- Location: `src/main/java/com/unicenta/data/loader/SessionDB*.java`
- Contains: Database-specific connection wrappers, sequence generators, SQL dialect handling
- Depends on: Database drivers
- Used by: Session initialization

**Core Domain Models:**
- Purpose: Represent business concepts and data structures
- Location: `src/main/java/com/unicenta/pos/ticket/`, `src/main/java/com/unicenta/pos/` (Info* classes)
- Contains: `TicketInfo`, `TicketLineInfo`, `ProductInfo`, `CustomerInfo`, `PaymentInfo`, `TaxInfo`
- Depends on: Format utilities, basic exceptions
- Used by: All layers

**Utilities & Support:**
- Purpose: Cross-cutting concerns, shared functionality
- Location: `src/main/java/com/unicenta/format/`, `src/main/java/com/unicenta/basic/`, `src/main/java/com/unicenta/pos/util/`
- Contains: Formatting, localization (`AppLocal`), configuration (`AppConfig`), exception handling
- Used by: All layers

## Data Flow

**Ticket Creation & Sale Flow:**

1. User interacts with sales UI (`JTicketsBag`, `JProductLineEdit`)
2. UI calls DataLogicSales methods to fetch products, apply taxes, calculate totals
3. DataLogicSales executes SQL via PreparedSentence objects created from Row definitions
4. Session executes JDBC queries with appropriate database-specific dialect
5. Results serialized via RowSerializerRead back to domain models
6. TicketInfo constructed with TicketLineInfo items and payments
7. Ticket persisted via DataLogicSales.saveSale() → Row.getInsertSentence() → JDBC
8. PaymentInfo processed, cash drawer signals, receipts printed via JasperReports

**Data Loading:**

1. Session created with database connection details
2. Row objects define table structure (Fields with types, primary keys)
3. SentenceList/ListProvider load data from database via serializers
4. Results filtered, sorted via ComparatorCreator
5. UI renders results via Vectorer/ListCellRenderer interfaces

**State Management:**
- **Transient**: Ticket data held in `TicketInfo` object during sales session
- **Persistent**: Committed to database via PreparedSentence insert/update/delete
- **Configuration**: AppConfig loads from `.properties` file on startup
- **User Session**: AppUser tracks logged-in user, permissions, active cash drawer

## Key Abstractions

**Datas Type System:**
- Purpose: Type-safe SQL serialization mapping Java types to database columns
- Examples: `Datas.STRING`, `Datas.DOUBLE`, `Datas.TIMESTAMP`, `Datas.INT`, `Datas.IMAGE`
- Pattern: Enum-like constants with getValue/setValue methods for JDBC ResultSet/PreparedStatement

**Row/Field Model:**
- Purpose: Define table schema and generate SQL automatically
- Examples: `src/main/java/com/unicenta/data/model/Row.java`, `src/main/java/com/unicenta/data/model/Field.java`
- Pattern: Composition of Field objects specifying columns, types, search/comparable flags, formatting rules

**Sentence Pattern:**
- Purpose: Abstract SQL execution interface
- Examples: `SentenceExec`, `SentenceList`, `SentenceFind`, `PreparedSentence`
- Pattern: Each represents a single SQL operation (insert, select, update, delete, list) with type-safe parameter binding

**Serializer Pattern:**
- Purpose: Bidirectional mapping between domain objects and database rows
- Examples: `SerializerRead`, `SerializerWrite`, `RowSerializerRead`
- Pattern: Implement read(DataRead) / write(DataWrite) to transform between layers

**Factory Delegation:**
- Purpose: Dynamic instantiation of database-specific implementations
- Examples: `BeanFactoryData.init()` dynamically loads `ClassName + DatabaseName` class
- Pattern: Allows single codebase to support multiple databases without conditional logic

## Entry Points

**Main Application:**
- Location: `src/main/java/com/unicenta/pos/forms/StartPOS.java`
- Triggers: `java -jar unicentaopos.jar`
- Responsibilities: 
  - Registers application instance via RMI (prevents multiple instances)
  - Loads AppConfig from user home directory
  - Sets up UI theme (FlatLaf, Substance, or platform default)
  - Creates and shows main application window

**Database Initialization:**
- Location: `src/main/java/com/unicenta/data/loader/Session.java`
- Triggers: AppConfig.load() during startup
- Responsibilities: Establishes JDBC connection, validates database schema, selects database-specific implementation

**Sales Module Entry:**
- Location: `src/main/java/com/unicenta/pos/forms/JPanelSales.java` (implied from forms directory)
- Triggers: User navigates to sales screen
- Responsibilities: Initialize ticket bag, product catalog, payment processors

## Error Handling

**Strategy:** Checked exception bubble-up with translation to user-friendly messages

**Patterns:**
- `BasicException`: Base checked exception for data/business logic errors
- Try-catch-log-rethrow in service layers (DataLogic*)
- GUI error dialogs via `AppLocal.getIntString()` for localized messages
- SQL errors caught as `SQLException`, translated to domain exceptions with context
- Database connection failures handled in SessionDB implementations with fallback/retry logic

## Cross-Cutting Concerns

**Logging:** SLF4J with Logback backend configured in `logback.groovy`. Used via Lombok `@Slf4j` annotation for performance.

**Validation:** 
- Database-level constraints (primary keys, foreign keys, unique constraints)
- UI-level validation in form components and editors
- Business logic validation in DataLogic* classes (e.g., stock checks, payment amount validation)

**Authentication:**
- AppUser tracks logged-in operator with role/permissions
- Password hashing/verification at database level
- Permission checks in UI component initialization

**Localization:**
- Messages bundled in `.properties` files under `src/main/resources/` (pos_messages_*.properties)
- Resolved via `AppLocal.getIntString(key)` lookup
- Locale set from AppConfig (user.language, user.country, user.variant)

**Formatting:**
- Date/currency/number formats via `Formats` utility class
- Applied consistently in Field.getFormat() for display
- Database stores in ISO format, formatting happens at display boundary

---

*Architecture analysis: 2026-04-03*
