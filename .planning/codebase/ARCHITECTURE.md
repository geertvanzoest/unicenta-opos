# Architecture

**Analysis Date:** 2026-04-03

## Pattern Overview

**Overall:** Layered monolith with Service Locator / BeanFactory pattern, MVC-style UI, and a custom data access layer.

**Key Characteristics:**
- Three-tier desktop application: Presentation (Swing/JavaFX), Business Logic (DataLogic services), Data Access (Session/Sentence/SQL)
- Service locator pattern via `BeanFactory` for dependency resolution at runtime (class names as strings)
- Script-driven menu system using BeanShell for dynamic UI composition
- Multi-database support via `SessionDB` strategy interface with per-engine implementations
- Domain objects ("Info" classes) used as DTOs between all layers
- 619 Java source files, 172 NetBeans `.form` files (form designer generated code)

## Application Lifecycle

**Startup sequence (`StartPOS.main()`):**

```
StartPOS.main()
  1. InstanceQuery check (RMI) -> prevent duplicate instances
  2. AppConfig(args).load() -> read .properties file from user home
  3. Locale.setDefault() -> set language/country from config
  4. Formats.setXxxPattern() -> configure number/date/currency formatting
  5. UIManager.setLookAndFeel() -> set Swing L&F (FlatLaf themes)
  6. TicketInfo.setHostname() -> machine identifier
  7. Metrics POST -> telemetry to unicenta (background thread)
  8. JRootFrame.initFrame() or JRootKiosk.initFrame() -> based on "machine.screenmode"
```

**Application initialization (`JRootApp.initApp()`):**

```
JRootApp.initApp(props)
  1. AppViewConnection.createSession(props) -> JDBC connection via Session
  2. getBean("com.unicenta.pos.forms.DataLogicSystem") -> service locator
  3. readDataBaseVersion() -> check DB schema version
  4. If version mismatch: run SQL migration script (BatchSentenceResource)
  5. Load active cash register state from DB
  6. DeviceTicket, TicketParser, DeviceScale, DeviceScanner init
  7. Timer(250ms) -> clock/date display refresh
  8. Show login panel (m_jPanelLogin)
```

**User login flow:**

```
User login (password or iButton)
  1. DataLogicSystem.findPeopleByCard() or password check
  2. AppUser.fillPermissions(DataLogicSystem) -> load role XML
  3. JPrincipalApp created with JRootApp + AppUser
  4. ScriptEngine.eval(Menu.Root) -> BeanShell builds menu
  5. Menu items reference class names -> lazy-loaded via BeanFactory
```

## Layers

**Presentation Layer:**
- Purpose: Swing/JavaFX UI panels, dialogs, forms
- Location: `src/main/java/com/unicenta/pos/` (UI classes throughout domain packages)
- Key patterns:
  - `JPanelView` interface: all switchable panels implement `getTitle()`, `activate()`, `deactivate()`, `getComponent()`
  - `JPanelTable`: abstract base for CRUD panels with `BrowsableEditableData` integration
  - `JPanelReport`: abstract base for JasperReports-based report panels
  - NetBeans form designer `.form` files paired with `.java` files
- Key files:
  - `src/main/java/com/unicenta/pos/forms/JRootApp.java` (1216 lines) - main app panel
  - `src/main/java/com/unicenta/pos/forms/JPrincipalApp.java` (649 lines) - per-user app view
  - `src/main/java/com/unicenta/pos/sales/JPanelTicket.java` (3515 lines) - sales screen
  - `src/main/java/com/unicenta/pos/sales/JPanelTicketSales.java` - sales specialization
  - `src/main/java/com/unicenta/beans/` - reusable Swing components (JNumberKeys, JCalendarPanel, etc.)

**Business Logic Layer (DataLogic services):**
- Purpose: Encapsulate SQL queries, business rules, and domain operations
- Pattern: Extend `BeanFactoryDataSingle`, initialized with `Session`, accessed via `AppView.getBean()`
- Key files:
  - `src/main/java/com/unicenta/pos/forms/DataLogicSales.java` (2827 lines) - products, tickets, taxes, stock
  - `src/main/java/com/unicenta/pos/forms/DataLogicSystem.java` (891 lines) - resources, users, cash register, orders
  - `src/main/java/com/unicenta/pos/customers/DataLogicCustomers.java` (380 lines) - customer CRUD
  - `src/main/java/com/unicenta/pos/admin/DataLogicAdmin.java` - roles, people, resources
  - `src/main/java/com/unicenta/pos/epm/DataLogicPresenceManagement.java` - employee presence
  - `src/main/java/com/unicenta/pos/suppliers/DataLogicSuppliers.java` - supplier management

**Data Access Layer:**
- Purpose: JDBC abstraction, SQL building, serialization, multi-database support
- Location: `src/main/java/com/unicenta/data/loader/`
- Key classes:
  - `Session.java` - JDBC connection wrapper with transaction management
  - `SessionDB.java` (interface) - database-specific SQL dialect
  - `SessionDBMariaDB.java`, `SessionDBMySQL.java`, `SessionDBPostgreSQL.java`, `SessionDBDerby.java`, `SessionDBSQLite.java` - per-engine implementations
  - `PreparedSentence.java` - parameterized SQL execution
  - `StaticSentence.java` - static SQL execution
  - `BatchSentence.java` / `BatchSentenceResource.java` - batch SQL from resource files
  - `TableDefinition.java` - table metadata for CRUD generation
  - `Datas.java` - type enum (STRING, INT, DOUBLE, TIMESTAMP, etc.)
  - `SerializerRead.java` / `SerializerWrite.java` - result set mapping interfaces

**Domain Model Layer (Info objects):**
- Purpose: Value objects / DTOs carrying business data between layers
- Pattern: Implement `SerializableRead` and/or `Externalizable`, named `*Info`
- Key classes:
  - `src/main/java/com/unicenta/pos/ticket/TicketInfo.java` (772 lines) - receipt/transaction
  - `src/main/java/com/unicenta/pos/ticket/TicketLineInfo.java` - line items
  - `src/main/java/com/unicenta/pos/ticket/TicketTaxInfo.java` - tax calculations
  - `src/main/java/com/unicenta/pos/payment/PaymentInfo.java` (abstract) - payment base
  - `src/main/java/com/unicenta/pos/payment/PaymentInfoCash.java` - cash payment
  - `src/main/java/com/unicenta/pos/payment/PaymentInfoFree.java` - free/no-charge payment
  - `src/main/java/com/unicenta/pos/payment/PaymentInfoMagcard.java` - card payment
  - `src/main/java/com/unicenta/pos/payment/VoucherPaymentInfo.java` - voucher payment
  - `src/main/java/com/unicenta/pos/customers/CustomerInfoExt.java` - extended customer
  - `src/main/java/com/unicenta/pos/ticket/ProductInfoExt.java` - extended product
  - `src/main/java/com/unicenta/pos/ticket/CategoryInfo.java` - product category

## Service Locator Pattern (BeanFactory)

The application uses a custom service locator pattern for dependency resolution.

**Interface hierarchy:**
```
BeanFactory (interface)          -> getBean(): Object
  BeanFactoryApp (interface)     -> init(AppView): void
    BeanFactoryDataSingle        -> init(Session): void  (abstract base for DataLogic*)
    BeanFactoryObj               -> wraps arbitrary objects
    BeanFactoryScript            -> script-based factories
    BeanFactoryCache             -> caching wrapper
    BeanFactoryData              -> multi-bean data factory
```

**Resolution flow:**
```java
// In JPrincipalApp or any panel:
DataLogicSales dlSales = (DataLogicSales) m_appview.getBean("com.unicenta.pos.forms.DataLogicSales");
```

1. `JRootApp.getBean(className)` checks `m_aBeanFactories` cache (HashMap)
2. If not cached: `Class.forName(className).newInstance()` -> creates instance
3. If instance is `BeanFactoryApp`: calls `init(this)` passing the AppView
4. `BeanFactoryDataSingle.init(AppView)` -> calls `init(app.getSession())`
5. DataLogic subclass initializes PreparedSentences with Session reference
6. Result cached and returned

**Old class mapping:** `JRootApp` maintains `m_oldclasses` HashMap for backwards compatibility (maps old class names to current ones).

## Script-Driven Menu System

The navigation menu is defined in BeanShell scripts, not Java code.

**Menu definition:** `src/main/resources/com/unicenta/pos/templates/Menu.Root.txt`

**Execution flow:**
1. `JPrincipalApp` constructor loads `Menu.Root` resource text
2. `ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL)` creates interpreter
3. Script accesses `ScriptMenu` API: `menu.addGroup()`, `group.addPanel()`, `group.addExecution()`
4. `addPanel(icon, key, className)` creates `MenuPanelAction` with class name string
5. On click: `MenuPanelAction.actionPerformed()` -> `AppUserView.showTask(className)`
6. `JPrincipalApp.showTask()` lazy-loads panel via BeanFactory using class name

**Menu structure:**
- Main: Sales, TicketEdit, CustomersPayment, Payments, CloseMoney
- Backoffice: Customers, Suppliers, Stock Management, Sales Management, Maintenance, Employees
- Utilities: CSV Import, Transfer, Tool Reports
- System: Configuration, Printer, CheckIn/CheckOut

**Permission control:** Role XML files (`Role.Administrator.xml`, `Role.Employee.xml`, etc.) define which menu items each role can access. `AppUser.fillPermissions()` parses role XML via SAX parser.

## Data Flow

**Sales transaction (ticket creation):**

```
1. JPanelTicketSales.activate()
   -> JCatalog.loadCatalog() -> DataLogicSales.getCategories() -> Session/SQL -> DB
   
2. User selects product from JCatalog
   -> CatalogListener.actionPerformed(ProductInfoExt)
   -> JPanelTicket.buttonTransition(ProductInfoExt)
   -> TicketInfo.addLine(TicketLineInfo)
   
3. User adds more items (repeat step 2)
   -> TicketInfo accumulates TicketLineInfo list
   -> TaxesLogic calculates tax per line
   
4. User initiates payment
   -> JPanelTicket opens JPaymentSelect dialog
   -> JPaymentSelectReceipt or JPaymentSelectRefund
   -> User selects payment type -> creates PaymentInfo subclass
   
5. Payment completed
   -> JPanelTicket.closeTicket()
   -> DataLogicSales.saveTicket(TicketInfo) -> INSERT into receipts, ticketlines, payments tables
   -> TicketParser.printTicket() -> DeviceTicket -> printer
```

**CRUD operations (e.g., Customers):**

```
1. CustomersPanel.activate()
   -> BrowsableEditableData loads list via ListProviderCreator
   -> DataLogicCustomers queries via TableDefinition
   -> Results displayed in JListNavigator
   
2. User selects customer
   -> CustomersView.writeValueEdit(customer) populates form fields
   
3. User edits fields, clicks Save
   -> DirtyManager detects changes
   -> SaveProvider.updateData() -> DataLogicCustomers -> Session.execute() -> DB UPDATE
```

**Report generation:**

```
1. User opens report from menu
   -> JPanelReport.activate() -> load .jrxml from resources
   -> JasperDesign compiled to JasperReport
   
2. User sets parameters (date range, filters)
   -> JParamsDatesInterval or other JParams* components
   
3. User clicks "Execute"
   -> JPanelReport fills report with JDBC Connection from Session
   -> JRViewer400 displays rendered report
```

**State Management:**
- **Active Cash:** `JRootApp` stores `m_sActiveCashIndex`, `m_iActiveCashSequence`, `m_dActiveCashDateStart/End` -- represents current cash register session
- **Current Ticket:** `JPanelTicket.m_oTicket` (TicketInfo) -- in-memory ticket being edited
- **Database Session:** `JRootApp.session` (Session) -- single JDBC connection, shared across all DataLogic instances
- **User Context:** `JPrincipalApp.m_appuser` (AppUser) -- current logged-in user with permissions
- **Properties:** `JRootApp.m_propsdb` (Properties) -- per-host settings stored in DB resources table

## Key Abstractions

**PaymentInfo hierarchy (Strategy pattern):**
```
PaymentInfo (abstract)
  PaymentInfoCash      -> cash with change calculation
  PaymentInfoFree      -> zero-amount / no-charge
  PaymentInfoMagcard   -> credit/debit card
  PaymentInfoMagcardRefund -> card refund
  PaymentInfoTicket    -> ticket-based payment (debt)
  VoucherPaymentInfo   -> prepaid voucher
```
- All implementations: `getName()`, `getTotal()`, `getPaid()`, `getChange()`, `getTendered()`, `copyPayment()`
- Used in `TicketInfo.payments` (List<PaymentInfo>)

**Printer abstraction (Null Object + Strategy):**
```
DevicePrinter (interface)
  DevicePrinterNull           -> no-op implementation
  DevicePrinterESCPOS         -> ESC/POS thermal printers
  DevicePrinterJavaPOS        -> JavaPOS standard
  DevicePrinterPrinter        -> Java PrintService (system printers)
  DevicePrinterPanel          -> on-screen ticket display
```
- `DeviceTicket` orchestrates up to 6 printer outputs (configurable)
- `TicketParser` processes XML ticket templates with embedded Velocity/BeanShell

**Scale abstraction:**
```
DeviceScale (factory)
  ScaleFake, ScaleComm, ScaleCASPDII, ScaleAvery, ScaleMTIND221, etc.
```

**Database abstraction (SessionDB strategy):**
```
SessionDB (interface)
  SessionDBMariaDB    -> MariaDB-specific SQL dialect
  SessionDBMySQL      -> MySQL dialect
  SessionDBPostgreSQL -> PostgreSQL dialect
  SessionDBDerby      -> Derby (embedded) dialect
  SessionDBSQLite     -> SQLite dialect
  SessionDBHSQLDB     -> HSQLDB dialect
  SessionDBOracle     -> Oracle dialect
  SessionDBGeneric    -> fallback
```
- Each implementation provides: `TRUE()`, `FALSE()`, `INTEGER_NULL()`, `CHAR_NULL()`, `getName()`, `getSequenceSentence()`
- `Session.getDiff()` auto-detects database type from JDBC URL

**SQL execution (Sentence pattern):**
```
BaseSentence (abstract)
  PreparedSentence    -> parameterized queries with SerializerRead/Write
  StaticSentence      -> static SQL strings
  BatchSentence       -> multiple statements from file/resource
```
- `SentenceList` / `SentenceFind` / `SentenceExec` are typed wrappers
- `SerializerRead` maps ResultSet rows to objects
- `SerializerWrite` maps objects to PreparedStatement parameters

## Entry Points

**`StartPOS.main()`:**
- Location: `src/main/java/com/unicenta/pos/forms/StartPOS.java`
- Triggers: JVM start via `java -jar unicentaopos.jar`
- Responsibilities: instance check, config load, L&F setup, frame creation

**`JRootFrame.initFrame()`:**
- Location: `src/main/java/com/unicenta/pos/forms/JRootFrame.java`
- Triggers: After StartPOS loads config (windowed mode)
- Responsibilities: create JRootApp, register RMI instance, show frame or show JFrmConfig if DB fails

**`JRootKiosk.initFrame()`:**
- Location: `src/main/java/com/unicenta/pos/forms/JRootKiosk.java`
- Triggers: After StartPOS loads config (fullscreen mode)
- Responsibilities: same as JRootFrame but in kiosk/fullscreen mode

**`JRootApp.initApp()`:**
- Location: `src/main/java/com/unicenta/pos/forms/JRootApp.java`
- Triggers: Called by JRootFrame/JRootKiosk
- Responsibilities: DB connection, schema migration, cash register init, device init

**`JFrmConfig`:**
- Location: `src/main/java/com/unicenta/pos/config/JFrmConfig.java`
- Triggers: When DB connection fails on startup
- Responsibilities: configuration wizard for database, printers, locale, etc.

## Error Handling

**Strategy:** Exception wrapping with `BasicException`, UI dialogs for user feedback, null-object pattern for hardware failures.

**Patterns:**
- `BasicException` wraps all application-level errors (extends `Exception`)
- `JMessageDialog.showMessage()` displays errors/warnings in modal dialogs with `MessageInf` severity levels (`SGN_DANGER`, `SGN_WARNING`, `SGN_NOTICE`, `SGN_SUCCESS`)
- Database errors: `Session.rollback()` on failure, `BasicException` thrown to caller
- Hardware failures: Null implementations absorb errors (e.g., `DevicePrinterNull`)
- Many catch blocks swallow exceptions silently (legacy pattern) -- `catch (Exception e) {}` throughout codebase
- Logging: `@Slf4j` (Lombok) with `log.error(e.getMessage())` -- often without stack trace

## Cross-Cutting Concerns

**Logging:**
- Framework: SLF4J + Logback
- Configuration: `src/main/resources/logback.groovy` (Groovy DSL)
- Usage: `@Slf4j` Lombok annotation, `log.info()`, `log.error()`, `log.debug()`
- Many legacy `System.out.println()` calls still present in codebase

**Validation:**
- No centralized validation framework
- Business rules embedded in DataLogic methods and UI event handlers
- `FormatsValidate.java` and `FormatsConstrain.java` for basic format validation

**Authentication:**
- User login via `JRootApp` login panel
- Password hashed with SHA-1/MD5 via `Hashcypher.java`
- iButton hardware authentication supported via OneWire protocol
- Role-based permissions loaded from XML resources (`Role.*.xml` templates)
- Permission check: `AppUser.hasPermission(className)` before showing panels

**Internationalization:**
- Property bundles: `pos_messages*.properties`, `data_messages*.properties`, `erp_messages*.properties`, `beans_messages*.properties`
- 14+ languages: en_US, nl, de, es, fr, it, pt, pt_BR, hr, el, ar, da, et, al_SQ
- Access: `AppLocal.getIntString(key)` static method
- Locale set at startup from config properties
- Report-specific bundles: `customers_messages*.properties`, `sales_messages*.properties`, etc.

**Scripting:**
- BeanShell for menu definitions and business rule customization
- Velocity for ticket templates
- `ScriptFactory.getScriptEngine(type)` creates appropriate engine
- Scripts can access application context via `eng.put("key", object)`

---

*Architecture analysis: 2026-04-03*
