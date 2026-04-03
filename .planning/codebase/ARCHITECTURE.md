# Architecture

**Analysis Date:** 2026-04-03

## Pattern Overview

**Overall:** Layered MVC with a separated Data Access Layer

**Key Characteristics:**
- Three-tier architecture: Presentation (Swing/JavaFX UI), Business Logic (DataLogic), and Data Access (Session/SQL)
- Swing-based forms with JavaFX support for modern UI elements
- Domain objects (Info classes) for data representation
- Database-agnostic design supporting multiple databases (MariaDB, MySQL, PostgreSQL, Derby, SQLite)
- Module-based organization with functional domains (Sales, Payments, Inventory, Customers, etc.)

## Layers

**Presentation Layer (UI):**
- Purpose: Handle user interface rendering and user interactions
- Location: `src/main/java/com/unicenta/pos/forms/`, `src/main/java/com/unicenta/pos/panels/`, `src/main/java/com/unicenta/beans/`
- Contains: JFrame subclasses, JPanels, custom Swing components, forms (.form files), dialog windows
- Depends on: Domain objects (Info classes), DataLogic classes, Beans components
- Used by: Application startup and user event handling
- Example entry points: `JRootFrame.java`, `JRootApp.java`

**Business Logic / Domain Layer:**
- Purpose: Encapsulate POS business rules and workflows
- Location: `src/main/java/com/unicenta/pos/` (domain-specific subdirectories)
- Contains: DataLogic* classes, domain models (TicketInfo, PaymentInfo, CategoryInfo, etc.), business orchestration
- Depends on: Data Access Layer (Session), utility classes
- Used by: Presentation Layer, orchestrates data flow
- Example classes: `DataLogicSales.java`, `DataLogicSystem.java`, `DataLogicCustomers.java`

**Data Access Layer:**
- Purpose: Manage database connectivity, transactions, and SQL execution
- Location: `src/main/java/com/unicenta/data/loader/`, `src/main/java/com/unicenta/data/model/`
- Contains: Session (connection management), SQL builders, table/column metadata, prepared statement executors
- Depends on: JDBC, database drivers
- Used by: DataLogic classes, domain models
- Core classes: `Session.java`, `SessionDB.java`, `BatchSentence.java`, `Table.java`, `Row.java`

**Domain Models (Info Objects):**
- Purpose: Represent business entities with serialization support
- Location: Throughout domain packages (e.g., `com/unicenta/pos/ticket/`, `com/unicenta/pos/payment/`)
- Contains: Classes implementing `SerializableRead`, `Externalizable` (e.g., TicketInfo, PaymentInfo, CustomerInfoExt)
- Pattern: Usually immutable or semi-immutable, support persistence
- Example classes: `TicketInfo.java`, `PaymentInfo.java`, `CategoryInfo.java`, `CustomerInfoExt.java`

**Support Layers:**

- **Formatting & Internationalization:** `src/main/java/com/unicenta/format/` - Locale-aware formatting for numbers, currency, dates
- **Utilities:** `src/main/java/com/unicenta/pos/util/` - Encryption, encoding, string manipulation, hardware utilities
- **Exception Handling:** `com/unicenta/basic/BasicException.java` - Base exception class for application errors
- **Device Integration:** `src/main/java/com/unicenta/pos/printer/`, `src/main/java/com/unicenta/pos/scale/`, `src/main/java/com/unicenta/pos/scanpal2/` - Hardware device abstraction

## Data Flow

**Ticket Creation & Sales Flow:**

1. User selects product from catalog (`JCatalog.java` → `JProductsSelector.java`)
2. Catalog lookup via `DataLogicSales.loadCategories()` queries database through `Session`
3. Product added to current `TicketInfo` (in-memory cart)
4. `TicketInfo` accumulates line items (`TicketLineInfo` list) and payment information
5. Payment selection via `JPaymentSelect.java` creates `PaymentInfo` subclass instances
6. `DataLogicSales.saveTicket()` persists ticket and lines to database via `Session` and `BatchSentence`
7. `TicketParser.java` formats ticket data for printer output via `DeviceTicket`

**Data Persistence Pattern:**

1. Domain objects (TicketInfo, PaymentInfo) are passed to DataLogic methods
2. DataLogic extracts data and builds SQL via `BatchSentence` (prepared statement wrapper)
3. `Session.execute()` runs batched SQL statements in transaction
4. Results mapped back to domain objects via `DataRead` interface
5. Transaction commits/rolls back via `Session.commit()` / `Session.rollback()`

**Configuration & Initialization:**

1. `StartPOS.main()` → `AppConfig.load()` reads properties from config files
2. `Formats` static methods configured with locale/pattern settings
3. `JRootApp.initApp()` initializes database connection via `Session`
4. `DataLogicSystem` loads system configuration and active cash state
5. Application state maintained in `JRootApp` instance variables

**State Management:**

- **Active Cash State:** `JRootApp` maintains `m_sActiveCashIndex`, `m_dActiveCashDateStart`, etc.
- **Session State:** `Session` object holds active database connection, transaction state
- **Current Ticket:** `JRootApp` holds current `TicketInfo` being edited
- **UI State:** Each panel/form maintains component state via Swing patterns

## Key Abstractions

**Domain Objects (Info Classes):**
- Purpose: Immutable representations of business entities
- Pattern: Implement `SerializableRead`, `Externalizable` for persistence
- Examples: `TicketInfo.java`, `PaymentInfo.java` (base), `PaymentInfoCash.java`, `PaymentInfoFree.java`, `PaymentInfoMagcard.java`
- Location: Scattered across functional packages (ticket, payment, customers, etc.)

**DataLogic Classes:**
- Purpose: Business logic facades for specific domains
- Pattern: Singleton-like usage, dependency injection via constructor
- Access pattern: `DataLogic*.getXXX()`, `DataLogic*.saveXXX()`, `DataLogic*.deleteXXX()`
- Examples: `DataLogicSales.java`, `DataLogicCustomers.java`, `DataLogicAdmin.java`

**Database Abstraction (Session/Table/Row/Column):**
- Purpose: Provide database-agnostic SQL construction and execution
- Pattern: Metadata-driven (Table/Column describe schema), SQL builders handle differences across DB engines
- Classes: `Session.java` (connection), `Table.java` (schema), `Column.java` (field), `Row.java` (record), `BatchSentence.java` (prepared statement)

**Payment Strategy (PaymentInfo Hierarchy):**
- Purpose: Support multiple payment methods extensibly
- Base: `PaymentInfo.java` (abstract concept)
- Implementations: `PaymentInfoCash.java`, `PaymentInfoFree.java`, `PaymentInfoMagcard.java`, `VoucherPaymentInfo.java`, `PaymentInfoTicket.java`
- Pattern: Strategy pattern, used in `PaymentInfoList.java`

**Printer Abstraction (Device Pattern):**
- Purpose: Support multiple printer types without coupling UI to printer details
- Base: `DevicePrinter.java` (interface/null implementation), `DeviceTicket.java`
- Implementations: JavaPOS, ESC/POS, screen display
- Location: `src/main/java/com/unicenta/pos/printer/`

**Scale Abstraction:**
- Purpose: Integrate weight scales for product measurement
- Base: `DeviceScale.java`
- Examples: `ScaleCASPDII.java`, `ScaleAvery.java`, `ScaleComm.java`
- Location: `src/main/java/com/unicenta/pos/scale/`

## Entry Points

**Application Startup:**
- Location: `com/unicenta/pos/forms/StartPOS.java`
- Triggers: JVM process start (`java -jar unicentaopos.jar`)
- Responsibilities:
  - Register application instance (single instance check via RMI)
  - Load configuration via `AppConfig`
  - Set Locale and Formats
  - Select UI Look & Feel
  - Create `JRootFrame` (windowed) or `JRootKiosk` (fullscreen)
  - Start metrics reporting thread

**UI Initialization:**
- Location: `com/unicenta/pos/forms/JRootFrame.java` and `JRootApp.java`
- Triggers: After config loaded
- Responsibilities:
  - Instantiate `JRootApp` (main application panel)
  - Initialize database connection via `Session`
  - Register RMI instance manager for single-instance enforcement
  - Load all DataLogic modules
  - Display main UI frame

**Database Initialization:**
- Location: `com/unicenta/data/loader/Session.java`
- Triggers: On application startup via `JRootApp.initApp()`
- Responsibilities:
  - Create JDBC connection to configured database
  - Manage transactions (begin/commit/rollback)
  - Provide `SessionDB` utility for metadata queries

## Error Handling

**Strategy:** Exception propagation with basic recovery

**Patterns:**
- `BasicException` is base class for application exceptions
- `BasicException(String msg)` for error messages
- Try-catch in main workflow methods with logging via Logback (Slf4j)
- UI errors displayed via `JMessageDialog.java` (modal dialogs)
- Database errors trigger transaction rollback via `Session.rollback()`
- Hardware errors (printer, scale) handled via null implementations (`DevicePrinterNull.java`, etc.)

**Logging:** Configured via `logback.groovy` in resources, uses `@Slf4j` Lombok annotation

## Cross-Cutting Concerns

**Logging:** 
- Framework: Logback via SLF4J
- Configuration: `src/main/resources/logback.groovy`
- Usage: `@Slf4j` annotation on classes, `log.info()`, `log.error()` calls

**Validation:**
- Pattern: Domain objects validate state (e.g., TicketInfo checks for required fields)
- No centralized validation framework; validation embedded in business logic methods
- Example: `DataLogicSales.validateTicket()` patterns

**Authentication:**
- Pattern: User login via `JRootApp`, stored in `UserInfo` objects
- Password verification against database via `DataLogicAdmin.getUserInfo()`
- Session maintains current user context

**Transactions:**
- Pattern: Explicit transaction management via `Session.begin()`, `commit()`, `rollback()`
- Auto-commit disabled; transactions must be explicitly controlled by DataLogic methods
- `BatchSentence` batches multiple statements, executed in single transaction

**Internationalization:**
- Pattern: Property files per language (e.g., `pos_messages_nl.properties`, `pos_messages_en_US.properties`)
- Loading: `AppLocal.getString()` static methods resolve messages from bundles
- Locale set at startup via `Locale.setDefault()` from config

---

*Architecture analysis: 2026-04-03*
