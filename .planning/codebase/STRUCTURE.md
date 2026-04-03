# Project Structure

**Analysis Date:** 2026-04-03

## Directory Layout

```
unicenta-opos/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/unicenta/
│   │   │   │   ├── basic/                          # Core exception classes
│   │   │   │   ├── beans/                          # Reusable Swing components
│   │   │   │   ├── data/                           # Data access layer
│   │   │   │   │   ├── gui/                        # Data-related UI dialogs
│   │   │   │   │   ├── loader/                     # Session, SQL builders, batch execution
│   │   │   │   │   ├── model/                      # Table/Column/Row metadata classes
│   │   │   │   │   └── user/                       # Browsable data interfaces
│   │   │   │   ├── editor/                         # Generic data editing UI
│   │   │   │   ├── format/                         # Formatting & i18n utilities
│   │   │   │   ├── orderpop/                       # Order popup module
│   │   │   │   └── pos/                            # Main POS application
│   │   │   │       ├── admin/                      # Administration/setup
│   │   │   │       ├── catalog/                    # Product catalog & selection UI
│   │   │   │       ├── comm/                       # Communication/networking
│   │   │   │       ├── config/                     # Configuration forms
│   │   │   │       ├── customers/                  # Customer management
│   │   │   │       ├── epm/                        # Employee Presence Management
│   │   │   │       ├── forms/                      # Main application frames & DataLogic classes
│   │   │   │       ├── imports/                    # Data import utilities
│   │   │   │       ├── instance/                   # Single-instance enforcement via RMI
│   │   │   │       ├── inventory/                  # Stock/inventory management
│   │   │   │       ├── mant/                       # Maintenance/backup tools
│   │   │   │       ├── panels/                     # Reusable UI panels
│   │   │   │       ├── payment/                    # Payment processing & methods
│   │   │   │       ├── printer/                    # Ticket printing abstraction
│   │   │   │       │   ├── escpos/                 # ESC/POS protocol support
│   │   │   │       │   ├── javapos/                # JavaPOS support
│   │   │   │       │   ├── printer/                # Physical printer implementations
│   │   │   │       │   ├── screen/                 # Screen/display output
│   │   │   │       │   └── ticket/                 # Ticket formatting & parsing
│   │   │   │       ├── reports/                    # Reporting module
│   │   │   │       ├── resets/                     # System resets/cleanup
│   │   │   │       ├── sales/                      # Sales workflows
│   │   │   │       │   ├── restaurant/             # Restaurant-specific sales UI
│   │   │   │       │   ├── shared/                 # Shared sales components
│   │   │   │       │   └── simple/                 # Simple sales mode
│   │   │   │       ├── scale/                      # Weight scale device integration
│   │   │   │       ├── scanpal2/                   # Barcode scanner integration
│   │   │   │       ├── scripting/                  # Script execution engine
│   │   │   │       ├── suppliers/                  # Supplier management
│   │   │   │       ├── ticket/                     # Ticket/transaction models
│   │   │   │       ├── transfer/                   # Data transfer/sync
│   │   │   │       ├── util/                       # Utility classes & helpers
│   │   │   │       └── voucher/                    # Voucher/prepaid module
│   │   │   ├── net/proteanit/                      # Third-party SQL utilities
│   │   │   └── org/usb4java/                       # USB device integration
│   │   └── resources/
│   │       ├── com/unicenta/
│   │       │   ├── pos/
│   │       │   │   ├── config/                     # Config bundles
│   │       │   │   ├── customers/                  # Customer UI resources
│   │       │   │   └── ...                         # Other module resources
│   │       │   └── images/                         # Application images/icons
│   │       ├── fxml/                               # JavaFX FXML files
│   │       ├── styles/                             # CSS stylesheets
│   │       ├── pos_messages*.properties            # Locale-specific UI messages
│   │       ├── data_messages*.properties           # Locale-specific data messages
│   │       ├── erp_messages*.properties            # Locale-specific ERP messages
│   │       ├── beans_messages*.properties          # Locale-specific bean messages
│   │       └── logback.groovy                      # Logging configuration
│   └── test/
│       └── java/
│           └── com/unicenta/                       # Unit test classes mirroring src structure
├── pom.xml                                         # Maven build configuration
├── target/                                         # Build output (generated)
└── .planning/codebase/                             # GSD planning documents
```

## Directory Purposes

**`src/main/java/com/unicenta/`:**
- Root package for all Java source code

**`src/main/java/com/unicenta/basic/`:**
- Purpose: Core exception class
- Key files: `BasicException.java` (base exception for all application errors)

**`src/main/java/com/unicenta/beans/`:**
- Purpose: Reusable Swing UI components and dialogs
- Key files: `JPasswordDialog.java`, `JNumberKeys.java`, `JCalendarPanel.java`, `JGuestsPop.java`
- Usage: Included in forms throughout the application

**`src/main/java/com/unicenta/data/`:**
- Purpose: Database abstraction and data access layer
- Sub-packages:
  - `loader/`: JDBC connection management (`Session.java`), SQL builders (`BatchSentence.java`)
  - `model/`: Table/column metadata (`Table.java`, `Column.java`, `Row.java`)
  - `gui/`: Data-related dialogs (`JMessageDialog.java`)
  - `user/`: Abstract interfaces for browsable data (`BrowsableData.java`, `DocumentLoader.java`)

**`src/main/java/com/unicenta/format/`:**
- Purpose: Locale-aware formatting
- Key files: `Formats.java` (static methods for numbers, currency, dates, times)
- Usage: Called at startup via `StartPOS`, used by all UI components for display

**`src/main/java/com/unicenta/pos/forms/`:**
- Purpose: Application entry point and main UI frames
- Key files:
  - `StartPOS.java` (main() entry point)
  - `AppConfig.java` (property file loading, 399 lines)
  - `AppProperties.java` (property storage)
  - `AppLocal.java` (i18n message lookup)
  - `JRootFrame.java` (main windowed frame, 120+ lines)
  - `JRootApp.java` (main application logic panel, 100+ lines)
  - `JRootKiosk.java` (fullscreen mode variant)
  - `DataLogicSales.java`, `DataLogicSystem.java`, `DataLogicCustomers.java`, etc. (business logic)

**`src/main/java/com/unicenta/pos/ticket/`:**
- Purpose: Ticket/transaction domain models
- Key files: `TicketInfo.java` (main ticket model, implements `Externalizable`)
- Domain objects: `TicketLineInfo.java`, `TicketTaxInfo.java`, `CardInfo.java`, `CategoryInfo.java`
- Usage: Persistent representation of sales transactions

**`src/main/java/com/unicenta/pos/payment/`:**
- Purpose: Payment processing and payment method abstractions
- Base: `PaymentInfo.java` (abstract)
- Implementations: `PaymentInfoCash.java`, `PaymentInfoFree.java`, `PaymentInfoMagcard.java`, `VoucherPaymentInfo.java`, `PaymentInfoTicket.java`
- UI: `JPaymentSelect.java`, `JPaymentCashPos.java`, `JPaymentFree.java`, `JPaymentDebt.java`

**`src/main/java/com/unicenta/pos/customers/`:**
- Purpose: Customer management
- Key files: `CustomerInfo.java`, `CustomerInfoExt.java` (domain models)
- DataLogic: `DataLogicCustomers.java`
- UI: `CustomersPanel.java`, `CustomersView.java`, `JCustomerFinder.java`, `JDialogNewCustomer.java`

**`src/main/java/com/unicenta/pos/catalog/`:**
- Purpose: Product catalog browsing and selection
- Key files: `JCatalog.java` (main catalog UI), `JProductsSelector.java` (product picker)
- Domain: `CategoryInfo.java`, `CategoryStock.java`

**`src/main/java/com/unicenta/pos/printer/`:**
- Purpose: Ticket printing abstraction and device integration
- Base: `DevicePrinter.java`, `DeviceTicket.java` (interfaces/abstraction)
- Core: `TicketParser.java` (formats tickets for printing)
- Implementations:
  - `printer/`: Physical printer implementations
  - `escpos/`: ESC/POS thermal printer protocol
  - `javapos/`: JavaPOS standard (legacy)
  - `screen/`: On-screen display
  - `ticket/`: Ticket-specific formatting

**`src/main/java/com/unicenta/pos/scale/`:**
- Purpose: Weight scale device integration
- Classes: `DeviceScale.java` (base), `ScaleCASPDII.java`, `ScaleAvery.java`, `ScaleComm.java`

**`src/main/java/com/unicenta/pos/scanpal2/`:**
- Purpose: Barcode scanner integration
- Classes: `DeviceScanner.java`, `DeviceScannerFactory.java`

**`src/main/java/com/unicenta/pos/sales/`:**
- Purpose: Sales workflows and related UI
- Sub-packages:
  - `simple/`: Simple/retail sales mode
  - `restaurant/`: Restaurant table management and ordering
  - `shared/`: Shared sales components
- Key files: `MenuActionListener.java`, `TaxesException.java`

**`src/main/java/com/unicenta/pos/admin/`:**
- Purpose: Administrative tasks and setup
- DataLogic: `DataLogicAdmin.java`

**`src/main/java/com/unicenta/pos/config/`:**
- Purpose: Application configuration UI
- Key files: `JFrmConfig.java` (configuration dialog)

**`src/main/java/com/unicenta/pos/util/`:**
- Purpose: Utility classes and helpers
- Key files: 
  - `Base64Encoder.java` (encoding)
  - `AltEncrypter.java` (encryption)
  - `StringUtils.java` (string manipulation)
  - `OSValidator.java` (OS detection)
  - `SessionKeepAlive.java` (connection pool management)
  - `SelectPrinter.java` (printer selection)

**`src/main/java/com/unicenta/pos/instance/`:**
- Purpose: Single-instance enforcement via RMI
- Key files: `InstanceManager.java`, `InstanceQuery.java`, `AppMessage.java`

**`src/main/resources/`:**
- Purpose: Non-code resources
- Subdirectories:
  - `com/unicenta/images/`: Application icons, logo (`favicon.png`)
  - `com/unicenta/pos/`: Module-specific resource bundles
  - `fxml/`: JavaFX FXML layout files
  - `styles/`: CSS stylesheets for UI theming
- Message files: `*_messages*.properties` (14+ languages supported)
- Logging: `logback.groovy` (Logback configuration in Groovy DSL)

**`src/test/java/`:**
- Purpose: Unit tests
- Structure mirrors `src/main/java/`
- Test packages:
  - `com/unicenta/pos/ticket/` (TicketInfo tests)
  - `com/unicenta/pos/payment/` (PaymentInfo tests)
  - `com/unicenta/pos/customers/` (CustomerInfo tests)
  - `com/unicenta/pos/util/` (Utility tests)
  - `com/unicenta/format/` (Formatting tests)
  - `com/unicenta/data/loader/` (Data access tests)

## Key File Locations

**Entry Points:**
- `src/main/java/com/unicenta/pos/forms/StartPOS.java`: Application main() method

**Configuration:**
- `pom.xml`: Maven build and dependency configuration
- `src/main/resources/logback.groovy`: Logging configuration
- `src/main/java/com/unicenta/pos/forms/AppConfig.java`: Property file handling (reads `.properties` files)

**Core Logic:**
- `src/main/java/com/unicenta/pos/forms/JRootApp.java`: Main application orchestrator
- `src/main/java/com/unicenta/pos/forms/DataLogicSales.java`: Sales business logic
- `src/main/java/com/unicenta/pos/forms/DataLogicSystem.java`: System-level logic
- `src/main/java/com/unicenta/data/loader/Session.java`: Database connection management

**Domain Models:**
- `src/main/java/com/unicenta/pos/ticket/TicketInfo.java`: Transaction/receipt
- `src/main/java/com/unicenta/pos/payment/PaymentInfo.java`: Payment base class
- `src/main/java/com/unicenta/pos/customers/CustomerInfo.java`: Customer data
- `src/main/java/com/unicenta/pos/ticket/CategoryInfo.java`: Product category

**Testing:**
- `src/test/java/com/unicenta/pos/ticket/`: Ticket-related tests
- `src/test/java/com/unicenta/pos/payment/`: Payment tests
- `src/test/java/com/unicenta/pos/util/`: Utility tests

**UI Dialogs & Panels:**
- `src/main/java/com/unicenta/pos/customers/JCustomerFinder.java`: Customer search dialog
- `src/main/java/com/unicenta/pos/customers/CustomersPanel.java`: Customer management panel
- `src/main/java/com/unicenta/pos/catalog/JCatalog.java`: Product catalog browser
- `src/main/java/com/unicenta/pos/payment/JPaymentSelect.java`: Payment method selector

## Naming Conventions

**Files:**
- Java source files: `ClassName.java` (PascalCase, matches class name)
- Form designer files: `ClassName.form` (paired with `.java`)
- Package structure: `com.unicenta.pos.{domain}` (lowercase, domain-specific)

**Classes:**
- Frames/dialogs: `JRootFrame`, `JRootApp`, `J*` prefix (Swing convention)
- Data models: `*Info` suffix (TicketInfo, PaymentInfo, CustomerInfo, etc.)
- Business logic: `DataLogic*` (DataLogicSales, DataLogicCustomers, DataLogicAdmin)
- Device abstraction: `Device*` (DevicePrinter, DeviceScale, DeviceScanner)
- Implementations: Specific names or `*Impl` suffix (DevicePrinterNull, DeviceDisplayImpl)
- Interfaces: `*Data`, `*Listener`, `*Creator` (BrowsableData, BrowseListener, EditorCreator)
- UI panels: `JPanel*`, `*Panel` (JPanelPayments, CustomersPanel)
- Exceptions: `*Exception` (BasicException, TaxesException)
- Utilities: `*Utils`, `*Helper` (StringUtils, OSValidator)

**Packages:**
- Functional domains: `com.unicenta.pos.{feature}` (sales, payment, customers, ticket, printer, etc.)
- Utility packages: `com.unicenta.pos.util`, `com.unicenta.format`, `com.unicenta.basic`
- Data layer: `com.unicenta.data.{concern}` (loader, model, gui, user)
- Device abstraction: Integrated into feature packages (printer/, scale/, scanpal2/)

**Methods:**
- Getters: `getXXX()` (standard JavaBean convention)
- Setters: `setXXX()` (standard JavaBean convention)
- Business logic: `loadXXX()`, `saveXXX()`, `deleteXXX()`, `validateXXX()` (DataLogic classes)
- Factory methods: `create*()`, `getInstance()` (if static factory)

**Variables & Fields:**
- Constants: `UPPER_SNAKE_CASE` (RECEIPT_NORMAL, REFUND_NOT)
- Instance fields: camelCase with `m_` prefix (legacy convention, visible in older code: `m_sId`, `m_dDate`, `m_aLines`)
- Newer code: drops prefix, uses plain camelCase (sId, dDate, aLines in less critical code)
- Prefixes observed: `m_s` (String), `m_d` (Date/Double), `m_i` (int), `m_a` (array)

## Resource Organization

**Localization:**
- Location: `src/main/resources/` (root level)
- Pattern: `{domain}_messages_{locale}.properties` (e.g., `pos_messages_nl.properties`, `pos_messages_en_US.properties`)
- Domains: `pos_`, `data_`, `beans_`, `erp_` (13+ languages supported)
- Usage: `AppLocal.getString("key")` for i18n

**Images & Icons:**
- Location: `src/main/resources/com/unicenta/images/`
- Key files: `favicon.png` (application icon)

**UI Templates (FXML):**
- Location: `src/main/resources/fxml/`
- Files: JavaFX FXML layout files (if using JavaFX panels)

**Stylesheets:**
- Location: `src/main/resources/styles/`
- Files: CSS for theming (FlatLaf themes configured in code)

**Configuration Files:**
- Type: Property bundles in `src/main/resources/com/unicenta/pos/{module}/Bundle.properties`
- Example: `src/main/resources/com/unicenta/pos/customers/Bundle.properties`

## Where to Add New Code

**New Feature/Business Logic:**
- **Domain Model (if needed):** Create `*Info.java` in appropriate domain package (e.g., `src/main/java/com/unicenta/pos/{feature}/`)
- **Business Logic:** Add methods to `DataLogic{Feature}.java` in `src/main/java/com/unicenta/pos/forms/`
- **UI Panel:** Create `J*.java` or `*Panel.java` in `src/main/java/com/unicenta/pos/{feature}/` or `src/main/java/com/unicenta/pos/panels/`
- **Tests:** Create test class in `src/test/java/com/unicenta/pos/{feature}/` mirroring source structure

**New Component/Widget:**
- **Custom Bean:** `src/main/java/com/unicenta/beans/` for reusable components
- **Panel:** `src/main/java/com/unicenta/pos/panels/` for reusable panels (shared across features)
- **Dialog:** Extend `JFrame` or `JDialog` in feature package or `forms/` if global

**New Device Integration (Printer, Scale, Scanner):**
- **Base Class:** Create in device-specific package (`printer/`, `scale/`, `scanpal2/`)
- **Implementation:** Create implementation class (e.g., `DeviceXXXImpl.java`) in same package
- **Registration:** Wire into device factory in `JRootApp.initApp()` or device-specific factory

**Utilities & Helpers:**
- **Shared Utilities:** `src/main/java/com/unicenta/pos/util/`
- **Formatting Functions:** `src/main/java/com/unicenta/format/`
- **Data Access Helpers:** `src/main/java/com/unicenta/data/loader/` (for SQL building)

**Database Access:**
- **New DataLogic Domain:** Create `DataLogic{Domain}.java` in `src/main/java/com/unicenta/pos/forms/`
- **Pattern:** Implement methods using `Session`, `BatchSentence`, `Table` metadata
- **Domain Models:** Use `*Info` classes implementing `SerializableRead`/`Externalizable`

**Internationalization:**
- **Add Message Key:** Add key=value to `src/main/resources/pos_messages.properties` (English default)
- **Translate:** Add translations to `pos_messages_{locale}.properties` for each supported language
- **Usage:** Call `AppLocal.getString("key")` in code

## Special Directories

**`target/`:**
- Purpose: Maven build output (generated)
- Generated: Yes
- Committed: No (in .gitignore)
- Contents: Compiled classes (`target/classes/`), JAR file (`target/unicentaopos.jar`)

**`.planning/codebase/`:**
- Purpose: GSD (Geert-Specific Development) planning documents
- Generated: No (hand-written by developers)
- Committed: Yes (tracked in git)
- Contents: `ARCHITECTURE.md`, `STRUCTURE.md`, `CONVENTIONS.md`, `TESTING.md`, `CONCERNS.md`, `STACK.md`, `INTEGRATIONS.md`

**`src/main/resources/styles/`:**
- Purpose: UI theme and stylesheet definitions
- Generated: No
- Committed: Yes
- Contents: FlatLaf theme configurations, CSS files for Swing theming

**`src/main/java/net/`, `src/main/java/org/`:**
- Purpose: Third-party/vendor code
- Generated: No
- Committed: Yes
- Contents: `net.proteanit.sql.DbUtils` (SQL utilities), `org.usb4java.*` (USB device support)

---

*Structure analysis: 2026-04-03*
