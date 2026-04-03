# External Integrations

**Analysis Date:** 2026-04-03

## APIs & External Services

**Payment Processing:**

- PaymentSense - Chip & PIN card payment terminal
  - SDK/Client: `com.unicenta.plugins.Application` (from `unicenta-plugins` 1.1 JAR)
  - Implementation: `src/main/java/com/unicenta/pos/payment/PaymentGatewayPaymentSense.java`
  - Flow: Amount sent via `Application.paymentSenseTransaction()`, polls `AppContext.getIsProcessing()` every second, timeout 180s
  - Result data: transaction ID, auth code, card scheme name, payment method
  - Config property: `payment.gateway=PaymentSense`

- External Gateway - Generic external payment processing
  - Implementation: `src/main/java/com/unicenta/pos/payment/PaymentGatewayExt.java`
  - Config property: `payment.gateway=external` (default)
  - Factory: `src/main/java/com/unicenta/pos/payment/PaymentGatewayFac.java`

**Plugin/Metrics System:**

- uniCenta Plugins - Commercial plugin framework
  - Dependency: `com.unicenta:unicenta-plugins` 1.1
  - Metrics reporting: `com.unicenta.plugins.metrics.Metrics` class
  - Called on startup in `src/main/java/com/unicenta/pos/forms/StartPOS.java` (line 131-137) via background thread
  - Sends device hostname + app version to uniCenta

**Openbravo ERP:**

- SOAP-based ERP integration for data synchronization
  - Dependency: `uk.co.pos_apps:openbravo` 1.0-SNAPSHOT
  - SOAP client: Apache Axis 1.4 (`axis:axis`, `axis-jaxrpc`, `axis-wsdl4j`)
  - Transfer module: `src/main/java/com/unicenta/pos/transfer/Transfer.java`
  - Config UI: `src/main/java/com/unicenta/pos/transfer/TransferPanel.java`

## Data Storage

**Databases:**

- MariaDB (primary/recommended)
  - Driver: `org.mariadb.jdbc:mariadb-java-client` 2.7.0
  - Session class: `src/main/java/com/unicenta/data/loader/SessionDBMariaDB.java`
  - Schema creation: `src/main/resources/com/unicenta/pos/scripts/MySQL-create.sql`
  - Upgrade scripts: `MySQL-upgrade-4.5.sql` through `MySQL-upgrade-4.5.4.sql`, `MySQL-upgrade_master.sql`, `MariaDB-upgrade_master.sql`
  - Stored procedures: `MySQL-create-sp.sql`
  - Foreign keys: `MySQL-FKeys.sql`, `MySQL-dropFKeys.sql`
  - Connection URL pattern: `jdbc:mysql://host:port/` + schema + options
  - Config: `db.URL`, `db.schema`, `db.options` (e.g., `?zeroDateTimeBehavior=convertToNull`)

- MySQL
  - Driver: `mysql:mysql-connector-java` 5.1.39
  - Session class: `src/main/java/com/unicenta/data/loader/SessionDBMySQL.java`
  - Shares schema scripts with MariaDB

- PostgreSQL
  - Driver: `org.postgresql:postgresql` 9.4.1208
  - Session class: `src/main/java/com/unicenta/data/loader/SessionDBPostgreSQL.java`
  - Schema creation: `src/main/resources/com/unicenta/pos/scripts/PostgreSQL-create.sql`

- Apache Derby (embedded, default for fresh installs)
  - Driver: `org.apache.derby:derby` 10.14.2.0
  - Session class: `src/main/java/com/unicenta/data/loader/SessionDBDerby.java`
  - Schema creation: `src/main/resources/com/unicenta/pos/scripts/Derby-create.sql`
  - Default URL: `jdbc:derby:[user.home]/.unicenta/unicentaopos-database;create=true`

- SQLite (experimental)
  - Driver: `org.xerial:sqlite-jdbc` 3.7.2
  - Session class: `src/main/java/com/unicenta/data/loader/SessionDBSQLite.java`
  - Schema creation: `src/main/resources/com/unicenta/pos/scripts/SQLite-create.sql`
  - Commented out in config UI (`JPanelConfigDatabase.java` line 61)

- HSQLDB / Oracle
  - Session classes exist: `SessionDBHSQLDB.java`, `SessionDBOracle.java`
  - No JDBC drivers in `pom.xml` -- bring your own driver via `db.driverlib`

**Database Access Pattern:**

- Connection management: `src/main/java/com/unicenta/data/loader/Session.java`
  - Direct JDBC via `DriverManager.getConnection()` -- no connection pooling
  - Auto-reconnect on closed connection (outside transactions)
  - Manual transaction management: `begin()`, `commit()`, `rollback()`
  - Database dialect detection: reads `DatabaseProductName` from JDBC metadata, dispatches to `SessionDB*` implementation

- Connection creation: `src/main/java/com/unicenta/pos/forms/AppViewConnection.java`
  - Dynamic driver loading via `URLClassLoader` from `db.driverlib` path
  - Password decryption: `AltEncrypter` with key `"cypherkey" + username`
  - Multi-database support: `db.multi=true` shows database selection dialog
  - Java Web Start detection for alternative class loading

- SQL abstraction: `src/main/java/com/unicenta/data/loader/` package
  - `PreparedSentence` - Parameterized queries with `SerializerRead`/`SerializerWrite`
  - `StaticSentence` - Simple static queries
  - `BatchSentence` / `BatchSentenceResource` - Multi-statement SQL execution from resource files
  - `TableDefinition` - CRUD operations on tables
  - `Transaction` - Transactional multi-operation wrapper

- Data logic classes (business layer):
  - `src/main/java/com/unicenta/pos/forms/DataLogicSales.java` (2827 lines) - Sales, products, tickets, categories
  - `src/main/java/com/unicenta/pos/forms/DataLogicSystem.java` - Users, roles, resources, CSV imports, permissions

**File Storage:**

- Local filesystem only
- Application data: `[user.home]/.unicenta/` directory
- Log files: `[user.home]/.unicenta/unicenta-YYYY-MM-DD.log`
- Config file: `[user.home]/unicentaopos.properties`

**Caching:**

- Berkeley DB Java Edition (`com.sleepycat:je` 5.0.73) available as dependency
- No application-level caching layer detected in active use

## Authentication & Identity

**Auth Provider:**

- Custom database-backed authentication
  - Password hashing: SHA-1 via `src/main/java/com/unicenta/pos/util/Hashcypher.java`
  - Hash format: `sha1:[hex]`, `plain:[text]`, or `empty:` for no password
  - Authentication method: `Hashcypher.authenticate(password, storedHash)`
  - No salt used -- plain SHA-1 of UTF-8 password bytes

- iButton (1-Wire) authentication
  - Library: `com.dalsemi.onewire:OneWireAPI` 0.1
  - Implementation: `src/main/java/com/unicenta/pos/forms/JRootApp.java` (implements `DeviceMonitorEventListener`)
  - Hardware monitor: `src/main/java/com/unicenta/pos/util/uOWWatch.java`
  - Config property: `machine.iButton=true/false`, `machine.iButtonResponse=5` (poll interval)
  - Purpose: Physical token-based user login via Dallas/Maxim iButton devices

- Role-based access control
  - Admin module: `src/main/java/com/unicenta/pos/admin/`
  - Role templates: `src/main/resources/com/unicenta/pos/templates/Role.Administrator.xml`, `Role.Manager.xml`, `Role.Employee.xml`, `Role.Guest.xml`
  - Permissions stored in database, loaded per user session

- RMI single-instance enforcement
  - `src/main/java/com/unicenta/pos/instance/InstanceQuery.java`
  - Checks RMI registry for `AppMessage` binding to prevent multiple POS instances

## Hardware Integrations

**Receipt Printers:**

- ESC/POS thermal printers (primary)
  - Implementation: `src/main/java/com/unicenta/pos/printer/escpos/DevicePrinterESCPOS.java`
  - Supported brands/protocols:
    - Epson: `CodesEpson.java` + `UnicodeTranslatorInt.java`
    - Star: `CodesStar.java` + `UnicodeTranslatorStar.java`
    - TMU220: `CodesTMU220.java` + `UnicodeTranslatorInt.java`
    - Ithaca: `CodesIthaca.java` + `UnicodeTranslatorInt.java`
    - SurePOS (IBM): `CodesSurePOS.java` + `UnicodeTranslatorSurePOS.java`
  - Connection methods:
    - Serial port: `PrinterWritterRXTX.java` via RXTX library
    - File/pipe: `PrinterWritterFile.java`
    - Raw socket: `PrinterWritterRaw.java`
  - Config property: `machine.printer=epson:COM1` or `machine.printer=star:rxtx:/dev/ttyUSB0`

- System printer (Java Print Service)
  - Implementation: `src/main/java/com/unicenta/pos/printer/printer/DevicePrinterPrinter.java`
  - Paper sizes configurable: receipt (72mm x 200mm default), standard (A4 default)
  - Config properties: `paper.receipt.*`, `paper.standard.*`, `machine.printername`
  - Config property: `machine.printer=printer:(Default),receipt`

- JavaPOS printers
  - Implementation: `src/main/java/com/unicenta/pos/printer/javapos/DevicePrinterJavaPOS.java`
  - Config property: `machine.printer=javapos:DeviceName`

- Plain text printer
  - Implementation: `src/main/java/com/unicenta/pos/printer/escpos/DevicePrinterPlain.java`
  - Config property: `machine.printer=plain:COM1`

- Screen printer (development/preview)
  - Implementation: `src/main/java/com/unicenta/pos/printer/screen/DevicePrinterPanel.java`
  - Config property: `machine.printer=screen` (default)

- Up to 6 printers configurable: `machine.printer`, `machine.printer.2` through `machine.printer.6`

**Customer Displays:**

- ESC/POS serial displays: `DeviceDisplayESCPOS.java`, `DeviceDisplaySurePOS.java`, `DeviceDisplaySerial.java`
- JavaPOS displays: `DeviceDisplayJavaPOS.java`
- On-screen display: `DeviceDisplayPanel.java` (default), `DeviceDisplayWindow.java`
- Config property: `machine.display=screen|window|epson|surepos|ld200|javapos`

**Fiscal Printers:**

- JavaPOS fiscal printer: `src/main/java/com/unicenta/pos/printer/javapos/DeviceFiscalPrinterJavaPOS.java`
- Config property: `machine.fiscalprinter=javapos:DeviceName`

**Weighing Scales:**

- Module: `src/main/java/com/unicenta/pos/scale/`
- Supported models:
  - AcomPC100: `ScaleAcomPC100.java`
  - Avery Berkel 6720: `ScaleAvery.java`
  - Casio PD1: `ScaleCasioPD1.java`
  - CAS PDII: `ScaleCASPDII.java`
  - Samsung ESP: `ScaleSamsungEsp.java`
  - MTIND221: `ScaleMTIND221.java`
  - Dialog (generic serial): `ScaleComm.java`
  - On-screen (dev): `ScaleDialog.java`
  - Fake (test): `ScaleFake.java`
- Factory: `src/main/java/com/unicenta/pos/scale/DeviceScale.java`
- All serial scales use RXTX for port communication
- Config property: `machine.scale=caspdii:COM3` or `machine.scale=screen`

**Magnetic Card Readers:**

- Module: `src/main/java/com/unicenta/pos/payment/`
- Implementations:
  - `MagCardReaderGeneric.java` - Standard card swipe readers
  - `MagCardReaderIntelligent.java` - Readers with encryption
- Factory: `MagCardReaderFac.java`
- Config property: `payment.magcardreader`

**Barcode Scanners:**

- Module: `src/main/java/com/unicenta/pos/scanpal2/`
- Scanner communication: `DeviceScannerComm.java` via RXTX serial
- Factory: `DeviceScannerFactory.java`
- Config property: `machine.scanner`

**Serial/USB Communication Layer:**

- RXTX 2.2 - Serial and parallel port communication
  - Java wrapper: `org.bidib.jbidib.org.qbang.rxtx:rxtxcomm` 2.2
  - Native libraries per platform:
    - Linux: `src/other/Linux/x86_64-unknown-linux-gnu/librxtxSerial64.so`, `librxtxParallel64.so`
    - Windows: `src/other/Windows/i368-mingw32/rxtxSerial.dll`, `rxtxSerial64.dll`, `rxtxParallel.dll`, `rxtxParallel64.dll`
    - macOS: `src/other/Mac_OS_X/librxtxSerial.jnilib`, `librxtxSerial64.jnilib`
  - Used by: printers (ESC/POS), scales, scanners, card readers

- USB4Java 1.2.0 - Direct USB device access
  - Example: `src/main/java/org/usb4java/examples/ListDevices.java`
  - API: `javax.usb:usb-api` 1.0.2

## File Format Handling

**Report Templates:**

- JasperReports (.jrxml) - 67 report templates in `src/main/resources/com/unicenta/reports/`
  - Categories: sales, inventory, products, customers, suppliers, labels, vouchers, EPM (employee performance)
  - Report engine: `src/main/java/com/unicenta/pos/reports/JPanelReport.java`
  - Custom viewer: `src/main/java/com/unicenta/pos/util/JRViewer400.java`
  - Custom AWT printer: `src/main/java/com/unicenta/pos/util/JRPrinterAWT300.java`
  - Data source: `src/main/java/com/unicenta/pos/reports/JRDataSourceBasic.java`

- BeanShell scripts (.bs) - 70 report query scripts in `src/main/resources/com/unicenta/reports/`
  - Purpose: Define SQL queries and parameters for each report
  - Engine: `src/main/java/com/unicenta/pos/scripting/ScriptEngineBeanshell.java`
  - Factory: `src/main/java/com/unicenta/pos/scripting/ScriptFactory.java`

- Report localization - Per-report `.properties` files with translations (de, fr, es, it, nl, hr, etc.)

**Print Templates:**

- XML ticket templates in `src/main/resources/com/unicenta/pos/templates/`
  - `Printer.Ticket2.xml`, `Printer.TicketLine.xml`, `Printer.TicketClose.xml`, `Printer.CloseCash.xml`
  - `Printer.ReprintTicket.xml`, `Printer.Product.xml`, `Printer.Start.xml`
  - `Ticket.Line.xml`, `Ticket.Buttons.xml`, `Ticket.TicketLineTaxesIncluded.xml`
  - `Cash.Close.xml`
  - Parsed by: `src/main/java/com/unicenta/pos/printer/TicketParser.java`

- Text templates:
  - `payment.cash.txt` - Cash payment receipt
  - `script.Keyboard.txt` - Keyboard layout definition
  - `Menu.Root.txt` - Main menu definition

**Data Import/Export:**

- CSV import: `src/main/java/com/unicenta/pos/imports/`
  - `JPanelCSVImport.java` - Products with barcode, name, prices, tax, category, supplier
  - `CustomerCSVImport.java` - Customer records
  - `StockQtyImport.java` - Stock quantity updates
  - `JPanelCSVCleardb.java` - Database clearing before import
  - Library: JavaCSV 2.0 (`com.csvreader.CsvReader`)

- Excel export: Apache POI 3.10.1 (XLS format)

- PDF output: JasperReports + iText 2.1.7 + FOP 2.1

**Barcode Generation:**

- Library: Barcode4J 2.1
- Implementation: `src/main/java/com/unicenta/pos/util/BarcodeImage.java`
- Supported formats: Codabar, Code128, Code39, EAN-13, EAN-8, UPC-A, UPC-E, Interleaved 2of5, POSTNET
- Ticket barcode rendering: `src/main/java/com/unicenta/pos/printer/ticket/PrintItemBarcode.java`

**Database Scripts:**

- 21 SQL scripts in `src/main/resources/com/unicenta/pos/scripts/`
- Per-database schema creation: `MySQL-create.sql`, `PostgreSQL-create.sql`, `Derby-create.sql`, `SQLite-create.sql`
- MySQL/MariaDB upgrade chain: `MySQL-upgrade-4.5.sql` -> `4.5.1` -> `4.5.2` -> `4.5.3` -> `4.5.4`
- Master upgrade scripts: `MySQL-upgrade_master.sql`, `MariaDB-upgrade_master.sql`
- Utility: `MySQL-clearData.sql`, `MySQL-check-tables.sql`, `MySQL-normalise-tables.sql`
- Transfer tables: `MySQL-create-transfer.sql`
- Orders table: `MySQL-orders-table.sql`

**UI Resources:**

- FXML: `src/main/resources/fxml/OrderPop.fxml` (JavaFX order popup)
- CSS: `src/main/resources/styles/orderpop.css` (JavaFX styling)
- Images: `src/main/resources/com/unicenta/images/` (application icons)
- Bonus images: `src/other/Bonus/Images/` (sample product images by category: Food, Drink, Clothing, Currency)

## Localization

**Resource Bundles:**

- 4 bundle families:
  - `pos_messages` - POS UI strings
  - `erp_messages` - ERP integration strings
  - `beans_messages` - Bean/component strings
  - `data_messages` - Data layer strings
- Loaded by: `src/main/java/com/unicenta/beans/LocaleResources.java`
- Registered in: `src/main/java/com/unicenta/pos/forms/AppLocal.java`

- Languages supported: en_US, es, es_AR, es_MX, fr, de, it, pt, pt_BR, nl, hr, et, el, al_SQ, ar, da

## Monitoring & Observability

**Error Tracking:**

- No external error tracking service (Sentry, Bugsnag, etc.)
- Errors logged via SLF4J/Logback to local files and console

**Logs:**

- Framework: Logback 1.2.2 via SLF4J
- Lombok `@Slf4j` annotation for logger injection
- File appender: `[user.home]/.unicenta/unicenta-YYYY-MM-DD.log`
- Rolling: Time-based, 5 days retention
- Console appender: stdout

## CI/CD & Deployment

**Hosting:**

- Desktop application -- deployed to client machines
- No server-side hosting (except database server)

**CI Pipeline:**

- GitHub Actions
  - `.github/workflows/ci.yml` - Build + test + coverage (Temurin JDK 11, Ubuntu)
  - `.github/workflows/semgrep.yml` - SAST security scanning
  - `.github/workflows/claude.yml` - Additional CI
- Codecov for coverage reporting
- CodeRabbit for automated PR review

**Distribution:**

- Maven FTP deployment to `ftp://repo.unicenta.org/` (via `wagon-ftp` 2.10)
- Build artifact: `target/unicentaopos.jar` + `target/lib/` + platform natives + scripts

## Environment Configuration

**Required properties (in `unicentaopos.properties`):**

- `db.engine` - Database engine name
- `db.URL` - JDBC connection URL base
- `db.schema` - Database name/schema
- `db.user` - Database username
- `db.password` - Database password (plaintext or `crypt:` prefix for encrypted)
- `db.driver` - JDBC driver class name
- `db.driverlib` - Path to JDBC driver JAR file

**Optional properties:**

- `db.multi` - Enable secondary database (`true`/`false`)
- `db1.URL`, `db1.schema`, `db1.user`, `db1.password` - Secondary database
- `payment.gateway` - Payment processor (`external` or `PaymentSense`)
- `payment.magcardreader` - Card reader type
- `machine.printer` through `machine.printer.6` - Printer configurations
- `machine.display` - Customer display
- `machine.scale` - Weight scale
- `machine.scanner` - Barcode scanner
- `machine.iButton` - Enable iButton authentication
- `machine.hostname` - Machine identifier

**Secrets:**

- Database password encrypted with `AltEncrypter` (`src/main/java/com/unicenta/pos/util/AltEncrypter.java`)
- Key derivation: `"cypherkey" + username`
- Stored in properties file with `crypt:` prefix
- No external secret management

## Webhooks & Callbacks

**Incoming:**

- None detected

**Outgoing:**

- Metrics POST on application startup (via `com.unicenta.plugins.Application.postMetrics()`)
- PaymentSense transaction callbacks (via plugin framework)

## Network Protocols

**FTP:**

- Upload utility: `src/main/java/com/unicenta/pos/util/FtpUpload.java`
- Uses `java.net.URLConnection` with `ftp://` protocol
- Purpose: License/machine file upload

**RMI (Remote Method Invocation):**

- Single-instance check: `src/main/java/com/unicenta/pos/instance/InstanceQuery.java`
- Registry lookup for `AppMessage` binding on localhost

**SOAP/HTTP:**

- Openbravo ERP integration via Apache Axis 1.4
- JAXB 2.3.1 for XML binding

---

*Integration audit: 2026-04-03*
