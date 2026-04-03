# Technology Stack

**Analysis Date:** 2026-04-03

## Languages & Runtime

**Primary:**
- Java 11 - Core application language
  - Compiler target: Java 11 (Maven properties `maven.compiler.source` and `maven.compiler.target`)
  - Entry point: `com.unicenta.pos.forms.StartPOS`

**Build System:**
- Apache Maven 3.x - Project build, dependency management, and packaging
  - Configuration: `pom.xml` (located at `/Users/geert/src/hscjeka/unicenta-opos/pom.xml`)
  - Build command: `mvn clean package`
  - Executable JAR: `target/unicentaopos.jar`
  - Classpath strategy: Dependencies copied to `target/lib/` during prepare-package phase

## Frameworks & Libraries

**UI Frameworks:**
- JavaFX 11 (OpenJFX) - Modern UI framework for GUI
  - Components: javafx-base, javafx-controls, javafx-fxml, javafx-graphics, javafx-media, javafx-swing, javafx-web
  - Located in: `src/main/java/com/unicenta/pos/forms/`, `src/main/java/com/unicenta/pos/sales/`, `src/main/java/com/unicenta/beans/`
  
- Swing - Legacy desktop UI components
  - Dependencies: swingx-all 1.6.5-1, swing-layout 1.0.3
  - Look and Feel support: Multiple themes via FlatLaf and Substance
  
- FlatLaf 1.6.5 - Modern flat look and feel for Swing
  - Dependencies: flatlaf, flatlaf-intellij-themes 2.0.1, flatlaf-swingx
  - Theme variants: Carbon, Dracula, Material Design Dark, Arc Dark, Atom One Light, GitHub, Material Lighter

- Substance 7.1.00 - Advanced Swing Look and Feel

**Charting & Graphics:**
- JFreeChart 1.0.19 - Chart and graph generation for reports
- JCommon 1.0.24 - Common utilities for JFreeChart
- Apache FOP 2.1 - XSL-FO processor for PDF and print output

**Reporting:**
- JasperReports 6.4.0 - Report generation engine
  - Supporting library: jasperreports-fonts 6.0.0, DynamicJasper-core-fonts 2.0
  - Compiler: eclipse jdt core compiler (ecj) 4.6.1
  - Viewer: `com.unicenta.pos.util.JRViewer400` (custom wrapper)
  - Located in: `src/main/java/com/unicenta/pos/reports/`

**Data Access & ORM:**
- JDBC (Java Database Connectivity) - Raw JDBC layer
  - Custom wrapper: `com.unicenta.data.loader.Session` handles connections and transactions
  - Sentence layer: `com.unicenta.data.loader.JDBCSentence`, `PreparedSentence` for parameterized queries
  - Located in: `src/main/java/com/unicenta/data/loader/`

**Logging:**
- Logback 1.2.2 - SLF4J implementation for logging
  - Annotation: @Slf4j from Lombok used throughout codebase
  - No explicit logback.xml configuration detected; uses defaults

**Data Processing:**
- Apache Commons utilities:
  - commons-beanutils 1.9.3 - Bean property manipulation
  - commons-codec 1.10 - Encoding/decoding (Base64, etc.)
  - commons-collections 3.2.2 - Collection utilities
  - commons-digester 2.1 - XML-to-object mapping
  - commons-discovery 0.5 - Service discovery for SOAP
  - commons-lang 2.6 - String and utility functions

- Apache Velocity 1.7 - Template engine for dynamic content generation
- ORO 2.0.8 - Regular expression library
- BeanShell 2.0b4 - Dynamic scripting for reports/customization

**File Format Support:**
- Apache POI 3.10.1 - Excel (XLS) file generation and parsing
- iText 2.1.7 - PDF generation (older version)
- JavaCSV 2.0 - CSV file parsing and generation
  - Used for product/customer imports in `src/main/java/com/unicenta/pos/imports/`

**Hardware & Peripheral Integration:**
- JavaPOS (javapos) 1.13 - Point of Sale terminal hardware control
  - JPos 2.0.10 - ISO 8583 message handling for payment terminals
  - RXTX 2.2 - Serial port communication (via org.qbang wrapper)
  - USB4Java 1.2.0 - USB device communication
  - USB API 1.0.2 - Standard USB interface
  - OneWireAPI 0.1 - 1-Wire device protocol support
  - Located in: `src/main/java/com/unicenta/pos/payment/`, `src/main/java/com/unicenta/pos/printer/`

- Java Print Service (javax.print) - Standard printer interface
  - Custom DevicePrinterPrinter: `src/main/java/com/unicenta/pos/printer/printer/DevicePrinterPrinter.java`

**Web Services & APIs:**
- Apache Axis 1.4 - SOAP web service client
  - Dependencies: axis, axis-jaxrpc, axis-wsdl4j 1.5.1
  - SAAJ API 1.3.5 - SOAP with Attachments API
  - XML APIs 1.0.b2
  - Located in: `src/main/java/com/unicenta/pos/transfer/` for ERP integrations

**Database Abstraction:**
- Custom database layer with multi-database support
  - SessionDB factory pattern in `src/main/java/com/unicenta/data/loader/SessionDB*`
  - Database-specific implementations for different SQL dialects

**Utilities & Helpers:**
- Lombok 1.18.6 (provided scope) - Annotations for getter/setter/logging generation
- JodaTime 2.9.7 - Date/time manipulation
- Reflections 0.9.12 - Classpath scanning and reflection utilities
- AbsoluteLayout (NetBeans) RELEASE82 - UI layout manager
- JDatePicker 1.3.4 - Date picker component
- Berkeley DB JE 5.0.73 - Embedded key-value store (legacy)
- Trident 1.4 - Animation library for UI transitions
- Charm Glisten 6.2.2 - Gluon mobile-to-desktop UI bridge
- WebLAF 1.2.9 - Additional look and feel theme

**JAXB & Persistence:**
- JAXB API 2.3.1 - XML binding and serialization
- JPA Persistence API 1.0.2 - Standard Java persistence interface

**Testing:**
- JUnit 4.12 - Unit testing framework
  - Scope: test
  - Located in: `src/test/java/` (created during unit test phase)
- Mockito 4.2.0 - Mocking framework for testing
  - mockito-inline 4.2.0 - Inline mocking for static/private methods
  - mockito-junit-jupiter 4.2.0 - JUnit 5 integration (scope: test)

**Test Coverage:**
- JaCoCo 0.8.12 - Code coverage analysis
  - Generates coverage reports during test phase
  - Report location: `target/site/jacoco/`

## Database Support

**Supported Databases:**
- MariaDB 10.x (primary) - JDBC driver: mariadb-java-client 2.7.0
- MySQL 5.x - JDBC driver: mysql-connector-java 5.1.39
- PostgreSQL 9.4+ - JDBC driver: postgresql 9.4.1208
- Apache Derby 10.14 - Embedded relational database
- SQLite 3.x - JDBC driver: sqlite-jdbc 3.7.2

## Configuration

**Application Configuration:**
- Properties-based configuration via `AppConfig` class
- Configuration file: `[user.home]/unicentaopos.properties` (default location)
- Database connection parameters read from `.properties` file:
  - `db.engine` - Database engine type (MariaDB, MySQL, PostgreSQL, Apache Derby)
  - `db.URL` - JDBC connection URL
  - `db.user` - Database username
  - `db.password` - Database password (encrypted with `crypt:` prefix using AltEncrypter)
  - `db.name` - Database name
  - `db.driver` - JDBC driver class
  - `db.driverlib` - Path to custom driver JAR (optional)
  - `db.schema` - Database schema name
  - `db.options` - Additional connection options
  - `db.multi` - Enable secondary database connection

**UI Configuration:**
- Locale settings: `user.language`, `user.country`, `user.variant`
- Format patterns: `format.integer`, `format.double`, `format.currency`, `format.percent`, `format.date`, `format.time`, `format.datetime`
- Theme: `swing.defaultlaf` - Full class name of LookAndFeel implementation

**Ticket & Report Configuration:**
- Ticket header lines: `tkt.header1` through `tkt.header6`
- Ticket footer lines: `tkt.footer1` through `tkt.footer6`
- Machine hostname: `machine.hostname`

**Resource Bundles:**
- Localization via `.properties` files in `src/main/resources/`
- Message keys for UI text, reports, and data layer
- Language variants: en_US, es, fr, de, pt_BR, it, nl, hr, et, al_SQ, ar, da, etc.

**Build Configuration:**
- Source encoding: UTF-8
- Debug symbols: Enabled (`showDeprecation=true`, `debug=true`)
- Dependency resolution: Maven Central + uniCenta repository + Gluon nexus

## Project Metadata

**Maven Coordinates:**
- Group ID: `com.unicenta`
- Artifact ID: `unicentaopos`
- Version: 5.0
- Packaging: JAR (executable with manifest classpath)

**Maven Repositories:**
- Maven Central (https://repo1.maven.org/maven2/)
- uniCenta Repository (https://repo.unicenta.org/maven2/)
- Gluon Nexus (https://nexus.gluonhq.com/nexus/content/repositories/releases/) - for Charm Glisten

**Manifest Information:**
- Main-Class: `com.unicenta.pos.forms.StartPOS`
- Implementation-Title: unicenta-pos
- Implementation-Version: 5.0
- Implementation-Vendor-Id: com.unicenta
- Implementation-Vendor: unicenta.com

## Platform Requirements

**Development Environment:**
- JDK 11+ (OpenJDK or Oracle JDK)
- Maven 3.6+
- IDE: NetBeans recommended (AbsoluteLayout support), Eclipse, or IntelliJ IDEA

**Runtime Environment:**
- JRE 11+
- Database server (MariaDB, MySQL, PostgreSQL, or embedded Derby/SQLite)
- System printer (for receipt/ticket printing)
- Serial ports or USB (optional, for peripheral devices like card readers, scales)

**Deployment Artifacts:**
- Single executable JAR: `target/unicentaopos.jar`
- Dependencies directory: `target/lib/` (included in distribution)
- Platform-specific native libraries: `target/lib/Windows/`, `target/lib/Linux/`, `target/lib/Mac_OS_X/`
- Configuration templates: `target/Configs/`
- Report templates: `target/Templates/`, `target/reports/`

---

*Stack analysis: 2026-04-03*
