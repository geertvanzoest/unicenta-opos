# External Integrations

**Analysis Date:** 2026-04-03

## Databases

**Primary Database Support:**
- MariaDB 10.x (recommended)
  - JDBC Driver: mariadb-java-client 2.7.0
  - Connection URL pattern: `jdbc:mysql://[host]:[port]/[database]`
  - Authentication: Username/password (encrypted in config if prefixed with `crypt:`)
  - Client: Raw JDBC via custom Session wrapper

- MySQL 5.x
  - JDBC Driver: mysql-connector-java 5.1.39
  - Connection URL pattern: `jdbc:mysql://[host]:[port]/[database]`
  - Client: Raw JDBC via custom Session wrapper

- PostgreSQL 9.4+
  - JDBC Driver: postgresql 9.4.1208
  - Connection URL pattern: `jdbc:postgresql://[host]:[port]/[database]`
  - Client: Raw JDBC via custom Session wrapper
  - Database-specific handling: `SessionDBPostgreSQL` in `src/main/java/com/unicenta/data/loader/SessionDBPostgreSQL.java`

- Apache Derby 10.14
  - JDBC Driver: derby 10.14.2.0
  - Embedded or server mode support
  - Database-specific handling: `SessionDBDerby`

- SQLite 3.x (emerging support, partially available)
  - JDBC Driver: sqlite-jdbc 3.7.2
  - Connection URL pattern: `jdbc:sqlite:[file-path]`

**Database Configuration:**
- Location: Application properties file (`[user.home]/unicentaopos.properties`)
- Properties:
  - `db.engine` - Database engine type selector
  - `db.URL` - Full JDBC connection URL
  - `db.user` - Database username
  - `db.password` - Database password (encrypted with AltEncrypter if prefixed with `crypt:`)
  - `db.name` - Database name
  - `db.driver` - JDBC driver class (auto-loaded)
  - `db.driverlib` - Optional path to custom JDBC driver JAR
  - `db.schema` - Database schema name (some databases)
  - `db.options` - Additional connection options

**Secondary Database Support:**
- Multi-database configuration available via `db.multi` property
- Secondary database uses same configuration pattern with `db1.*` prefix
- Located in: `src/main/java/com/unicenta/pos/config/JPanelConfigDatabase.java`

**Database Access Pattern:**
- Session wrapper: `com.unicenta.data.loader.Session`
  - Location: `src/main/java/com/unicenta/data/loader/Session.java`
  - Features: Connection pooling (manual), transaction management, auto-commit handling
  - Direct JDBC: No ORM framework used

- Sentence layer: Abstraction for SQL execution
  - Classes: `JDBCSentence`, `PreparedSentence`, `StaticSentence`
  - Location: `src/main/java/com/unicenta/data/loader/`
  - Parameterized queries: Supported via PreparedStatement

**Connection Encryption:**
- Database passwords encrypted using custom `AltEncrypter` class
- Cipher key: `cypherkey + [username]`
- Format: `crypt:[encrypted-password]`
- Decryption on load in: `src/main/java/com/unicenta/pos/config/JPanelConfigDatabase.java` (lines 130-132, 146-148)

## File I/O & Data Transfer

**CSV Import/Export:**
- CSV Reader: `CsvReader` (javacsv library 2.0)
- Module: `src/main/java/com/unicenta/pos/imports/`
  - `JPanelCSVImport.java` - Product and inventory imports
  - `CustomerCSVImport.java` - Customer data imports
  - `JPanelCSVCleardb.java` - Database clearing utilities
  - `StockQtyImport.java` - Stock quantity updates
- Supported encodings: UTF-8, custom charset selection
- File chooser: Swing JFileChooser with FileNameExtensionFilter

**Excel Generation:**
- Excel Writer: Apache POI 3.10.1
- Output format: XLS (Microsoft Excel 97-2003)
- Usage: Report export, data export features

**PDF Generation:**
- Primary: JasperReports 6.4.0 (via FOP 2.1)
- Legacy: iText 2.1.7
- XSL-FO processor: Apache FOP 2.1
- Report templates: Located in `src/main/resources/com/` and `target/Templates/`
- Viewer: Custom `JRViewer400` wrapper in `src/main/java/com/unicenta/pos/util/JRViewer400.java`
- Located in: `src/main/java/com/unicenta/pos/reports/JPanelReport.java`

**Barcode Generation:**
- Barcode4J 2.1
- Supported formats: Code128, EAN13, UPC-A, etc.
- Usage: Product labeling, ticket barcodes
- Located in: `src/main/java/com/unicenta/pos/printer/` modules

**FTP Upload:**
- Protocol: FTP
- Implementation: `src/main/java/com/unicenta/pos/util/FtpUpload.java`
- Method: URLConnection with ftp:// protocol
- Feature: Background thread for async uploads
- Endpoint: Configured hostname, username, password (see FtpUpload line 40-50)

## Payment Processing

**Payment Gateway Framework:**
- Interface: `PaymentGateway` in `src/main/java/com/unicenta/pos/payment/PaymentGateway.java`
- Implementation factory: `PaymentGatewayFac`

**PaymentSense Integration:**
- Implementation: `PaymentGatewayPaymentSense` in `src/main/java/com/unicenta/pos/payment/PaymentGatewayPaymentSense.java`
- Method: Plugin-based integration via `com.unicenta.plugins.Application.paymentSenseTransaction()`
- Features:
  - Chip & PIN support
  - Card scheme detection (Visa, Mastercard, etc.)
  - Transaction ID and auth code capture
  - Timeout handling (default 180 seconds)
- Data flow: Amount → Plugin → Wait for result → Process response

**Magnetic Card Reader Support:**
- Implementation: `MagCardReader*` classes in `src/main/java/com/unicenta/pos/payment/`
  - `MagCardReaderFac` - Factory for reader selection
  - `MagCardReaderGeneric` - Generic reader implementation
  - `MagCardReaderIntelligent` - Intelligent reader with encryption
- Hardware: Serial port or USB-connected card readers
- Data: `PaymentInfoMagcard` class for card transaction data

**Payment Methods Supported:**
- Cash (`PaymentInfoCash`) - `src/main/java/com/unicenta/pos/payment/PaymentInfoCash.java`
- Card (Magcard) (`PaymentInfoMagcard`)
- Cheque (`JPaymentCheque`)
- Bank transfer (`JPaymentBank`)
- Voucher (`VoucherPaymentInfo`)
- Paper tickets (`JPaymentPaper`)
- Debt/Customer account (`JPaymentDebt`)
- Free products (`PaymentInfoFree`)

**Payment Terminal Integration:**
- JavaPOS (javapos) 1.13 - Hardware abstraction layer
- JPos 2.0.10 - ISO 8583 message protocol for terminals
- Serial communication: RXTX 2.2

## Hardware & Peripherals

**Printer Integration:**
- Interface: `DevicePrinter` in `src/main/java/com/unicenta/pos/printer/`
- Implementation: `DevicePrinterPrinter` - System printer via Java Print Service
- Location: `src/main/java/com/unicenta/pos/printer/printer/DevicePrinterPrinter.java`
- Features:
  - Multiple printer support
  - Custom page sizes (72mm x 200mm, A4, etc.)
  - Media size configuration (MediaSizeName, Media)
  - Receipt printer (custom-width) and standard printer support
  - Print job attributes (orientation, job name)

**Scale Integration:**
- Module: `src/main/java/com/unicenta/pos/scale/`
- Hardware: Weight input devices via serial/USB

**Serial Port Communication:**
- Library: RXTX 2.2 (org.qbang.rxtx wrapper)
- Usage: Card readers, scales, some receipt printers
- Native libraries: Platform-specific in `target/lib/Windows/`, `target/lib/Linux/`, `target/lib/Mac_OS_X/`

**USB Device Support:**
- USB4Java 1.2.0 - USB device control
- USB API 1.0.2 - Standard interface
- 1-Wire Protocol: OneWireAPI 0.1 - Temperature sensors, serial IDs

## ERP & Web Service Integration

**Openbravo ERP Integration:**
- Dependency: `openbravo 1.0-SNAPSHOT` (uk.co.pos_apps)
- SOAP-based communication via Apache Axis 1.4
- Usage: Data synchronization with ERP backend
- Modules: `src/main/java/com/unicenta/pos/transfer/` - Data transfer and synchronization
- Classes:
  - `Transfer.java` - Main transfer engine with multi-database support
  - `DataLogicSystem.java` - System-level data operations

**Web Services:**
- SOAP Client: Apache Axis 1.4 + Axis JAXRPC 1.4
- WSDL Support: axis-wsdl4j 1.5.1
- XML handling: SAAJ API 1.3.5, XML APIs 1.0.b2
- Protocol: Standard SOAP/HTTP

**HTTP Network Access:**
- URL handling: java.net.URL, java.net.URLConnection
- Method: Direct socket connections for custom network operations
- Usage examples:
  - `UniBrowser.java` - HTML rendering and web viewing
  - `VideoPlayer.java` - Streaming video playback
  - `FtpUpload.java` - FTP file transfers

**RMI (Remote Method Invocation):**
- Usage: `InstanceQuery` in `src/main/java/com/unicenta/pos/instance/InstanceQuery.java`
- Feature: Single instance enforcement (prevents multiple POS instances)
- Methods: `java.rmi.RemoteException`, `java.rmi.NotBoundException`

## External Services

**License/Plugin System:**
- Plugin infrastructure: `com.unicenta.plugins.*` package
- Application hooks: `Application` class with payment and metrics APIs
- Location: dependency `unicenta-plugins 1.1` (external JAR)

**Metrics & Monitoring:**
- Metrics system: `com.unicenta.plugins.metrics.Metrics` class
- Features: Performance tracking, business metrics collection

## Authentication & Authorization

**User Management:**
- No external OAuth/LDAP integration detected
- Database-backed user system:
  - User credentials: Stored in database (encrypted passwords)
  - Role-based access control (RBAC) via database
  - Classes: `PeoplePanel`, `RolesPanel`, `RoleInfo` in `src/main/java/com/unicenta/pos/admin/`

## Data Import Sources

**Customer Import:**
- CSV format via `CustomerCSVImport.java`
- Fields: Customer name, reference, contact info
- Location: `src/main/java/com/unicenta/pos/imports/CustomerCSVImport.java`

**Product/Inventory Import:**
- CSV format via `JPanelCSVImport.java`
- Fields: Product reference, barcode, name, buy/sell price, tax, category, supplier
- Location: `src/main/java/com/unicenta/pos/imports/JPanelCSVImport.java`

**Stock Quantity Updates:**
- CSV format via `StockQtyImport.java`
- Fields: Product ID, quantity changes

## Configuration & Settings

**Machine & Hostname:**
- Local machine detection: `InetAddress.getLocalHost().getHostName()`
- Configuration key: `machine.hostname`
- Usage: FTP uploads, license verification, instance identification

**Time & Localization:**
- Locale: Java Locale API with configurable language/country/variant
- Date/Time formats: Via `Formats` utility class with pattern configuration
- Timezone: JVM default (no custom timezone handling detected)

## Known Integrations Summary

| Component | Type | Technology | Status |
|-----------|------|-----------|--------|
| Database | Data store | MariaDB, MySQL, PostgreSQL, Derby, SQLite | Active |
| Printer | Hardware | Java Print Service, receipt printer support | Active |
| Payment | External service | PaymentSense, Magcard readers | Active |
| Card Reader | Hardware | Serial/USB reader via RXTX | Active |
| Scale | Hardware | Serial/USB via RXTX | Supported |
| ERP | External system | Openbravo via SOAP/Axis | Active |
| FTP | File transfer | URL-based FTP upload | Active |
| Barcode | Generation | Barcode4J | Active |
| Reporting | Engine | JasperReports via FOP | Active |
| Email | Communication | Not detected | Not implemented |
| Webhooks | Callbacks | Not detected | Not implemented |

---

*Integration audit: 2026-04-03*
