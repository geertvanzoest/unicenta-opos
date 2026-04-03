# Technology Stack

**Analysis Date:** 2026-04-03

## Languages

**Primary:**
- Java 11 - Core POS application language and business logic

## Runtime

**Environment:**
- Java Runtime Environment (JRE) 11
- JavaFX 11 - Desktop UI framework

**Package Manager:**
- Maven 3.x
- Lockfile: `pom.xml` (present)

## Frameworks

**Core UI:**
- JavaFX 11 - Cross-platform desktop application framework (`javafx-base`, `javafx-controls`, `javafx-fxml`, `javafx-graphics`, `javafx-media`, `javafx-swing`, `javafx-web`)
- Swing - Legacy UI components (bundled with Java)
- FlatLaf 1.6.5 - Modern look and feel for Swing applications
- FlatLaf IntelliJ Themes 2.0.1 - UI theme library
- FlatLaf SwingX 1.6.5 - Integration with SwingX components
- Substance 7.1.00 - Additional UI theming
- WebLaf 1.2.9 - Web-based look and feel
- SwingX 1.6.5-1 - Extended Swing components

**Reporting:**
- JasperReports 6.4.0 - Report generation and PDF export
- JasperReports Fonts 6.0.0 - Font support for reports
- DynamicJasper Core Fonts 2.0 - Dynamic report creation
- Apache FOP 2.1 - XSL-FO processor for report rendering
- iText 2.1.7 - PDF manipulation (excluded from JasperReports, managed separately)

**Charting:**
- JFreeChart 1.0.19 - Business charting and graphing
- JCommon 1.0.24 - Utility library for JFreeChart

**Document Processing:**
- Apache POI 3.10.1 - Excel/Word document generation

**Testing:**
- JUnit 4.12 - Unit test framework
- Mockito 4.2.0 - Mocking framework
- Mockito Inline 4.2.0 - Advanced mocking capabilities
- Mockito JUnit Jupiter 4.2.0 - JUnit 5 integration

**Build/Dev:**
- Maven Compiler Plugin 2.3.2 - Java compilation
- Maven Dependency Plugin 2.10 - Dependency management
- Maven JAR Plugin 3.2.0 - JAR packaging
- Maven Resources Plugin 3.5.0 - Resource handling
- Maven Wagon FTP 2.10 - FTP deployment extension

## Key Dependencies

**Critical:**
- mariadb-java-client 2.7.0 - MariaDB database driver (primary database)
- mysql-connector-java 5.1.39 - MySQL compatibility
- postgresql 9.4.1208 - PostgreSQL driver
- derby 10.14.2.0 - Apache Derby embedded database
- sqlite-jdbc 3.7.2 - SQLite driver
- Logback Classic 1.2.2 - Logging framework
- SLF4J (via Logback) - Logging facade

**Hardware/Device:**
- JavaPOS (jpos) 2.0.10 - Point of Sale hardware interface
- JavaPOS (javapos) 1.13 - Additional POS device support
- RXTX (rxtxcomm) 2.2 - Serial port communication
- USB4Java 1.2.0 - USB device communication
- USB API 1.0.2 - USB device interface
- OneWireAPI 0.1 - 1-Wire device protocol

**Utilities:**
- Lombok 1.18.6 - Java annotation processor for reducing boilerplate
- Joda-Time 2.9.7 - Date/time handling
- Apache Commons Lang 2.6 - String/collection utilities
- Apache Commons Collections 3.2.2 - Collection data structures
- Apache Commons BeanUtils 1.9.3 - Bean manipulation
- Apache Commons Digester 2.1 - XML parsing
- Apache Commons Codec 1.10 - Encoding/decoding utilities
- Apache Commons Discovery 0.5 - Service discovery
- JavaCSV 2.0 - CSV parsing and generation
- Reflections 0.9.12 - Classpath scanning and reflection utilities
- BeanShell 2.0b4 - Embedded scripting engine
- Barcode4j 2.1 - Barcode generation

**Web Services & Integration:**
- Apache Axis 1.4 - SOAP/Web Services (legacy)
- Apache Axis JAXRPC 1.4 - RPC framework
- SAAJ API 1.3.5 - SOAP with Attachments
- Axis WSDL4J 1.5.1 - WSDL parsing
- Velocity 1.7 - Template engine
- ORO 2.0.8 - Regular expressions

**Data Access:**
- Persistence API 1.0.2 - JPA interface
- JAXB API 2.3.1 - XML binding

**UI Components:**
- Charm Glisten 6.2.2 - Gluon mobile UI framework
- Swing Layout 1.0.3 - Layout managers
- NetBeans AbsoluteLayout (RELEASE82) - Absolute positioning layout
- JDatePicker 1.3.4 - Date picker component
- Trident 1.4 - Timeline/animation framework

**Database Utilities:**
- Berkeley DB Java Edition 5.0.73 - Embedded key-value database

**Compiler:**
- Eclipse JDT Core Compiler (ecj) 4.6.1 - Java compilation

## Configuration

**Environment:**
- Configuration loaded via `AppConfig` class from `AppConfig.properties`
- Database multi-instance support with optional password encryption (cipher key)
- Locale/internationalization configuration (language, country, variant)
- Number, date, time, currency formatting configuration
- Swing Look and Feel selection via configuration

**Build:**
- Maven POM at `pom.xml` with compiler target Java 11
- UTF-8 source encoding configured
- Resources packaged from `src/main/resources`
- Platform-specific libraries in `src/other/` (Windows, Linux, Mac_OS_X)
- Maven repositories: uniCenta repo, Maven Central, Gluon Nexus

## Platform Requirements

**Development:**
- Java 11 JDK
- Maven 3.x
- Git (for version control)

**Production:**
- Java 11 JRE
- Database (MariaDB, MySQL, PostgreSQL, Derby, or SQLite)
- Hardware device drivers (for POS peripherals like card readers, printers, scales)

**Deployment:**
- Standalone JAR executable: `unicentaopos.jar`
- Classpath contains bundled dependencies in `lib/` directory
- Main class: `com.unicenta.pos.forms.StartPOS`
- Cross-platform support: Windows, Linux, macOS

---

*Stack analysis: 2026-04-03*
