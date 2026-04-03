# External Integrations

**Analysis Date:** 2026-04-03

## APIs & External Services

**Legacy Web Services:**
- Apache Axis 1.4 - SOAP/Web Services for external system integration
  - SDK/Client: `axis`, `axis-jaxrpc`, `saaj-api`, `axis-wsdl4j`
  - Purpose: Enterprise integration with external POS/ERP systems

**Openbravo Integration:**
- Openbravo ERP connector (uk.co.pos_apps:openbravo 1.0-SNAPSHOT)
  - SDK/Client: `openbravo` dependency
  - Purpose: Potential integration with Openbravo ERP systems
  - Files: `src/main/java/com/unicenta/pos/inventory/MaterialProdInfo.java` references OpenbravoPos

**Plugins Architecture:**
- uniCenta Plugin Framework (com.unicenta:unicenta-plugins 1.1)
  - SDK/Client: `unicenta-plugins` dependency
  - Purpose: Extensibility for custom integrations
  - Entry points: `com.unicenta.plugins.Application`, `com.unicenta.plugins.metrics.Metrics`

## Data Storage

**Databases:**
- **MariaDB** (Primary)
  - Connector: `mariadb-java-client` 2.7.0
  - Connection: Configured via `db.URL`, `db.schema`, `db.options` properties
  - User credentials: Encrypted password support (cipher key pattern: `cypherkey{user}`)
  - Files: `src/main/java/com/unicenta/pos/forms/AppViewConnection.java`

- **MySQL** (Legacy/Compatibility)
  - Connector: `mysql-connector-java` 5.1.39
  - Connection: Via standard JDBC connection strings
  - Driver class: `com.mysql.jdbc.Driver`

- **PostgreSQL**
  - Connector: `postgresql` 9.4.1208
  - Connection: Via standard JDBC connection strings
  - Driver class: `org.postgresql.Driver`

- **Apache Derby** (Embedded option)
  - Connector: `derby` 10.14.2.0
  - Driver class: `org.apache.derby.jdbc.EmbeddedDriver`
  - Use case: Standalone/desktop deployments

- **SQLite** (Lightweight option)
  - Connector: `sqlite-jdbc` 3.7.2
  - Driver class: `org.sqlite.JDBC`
  - Use case: Single-instance, file-based deployments
  - Files: Detection logic in `src/main/java/com/unicenta/pos/config/JPanelConfigDatabase.java`

**File Storage:**
- Local filesystem only
- Database scripts stored in: `src/scripts/`
- Configuration files stored in: `src/other/Configs/`

**Caching:**
- Berkeley DB Java Edition (com.sleepycat:je 5.0.73)
  - Purpose: Optional in-process key-value caching
  - Status: Included but not widely used in current codebase

## Authentication & Identity

**Auth Provider:**
- Custom authentication implemented in uniCenta
- No third-party OAuth/SSO integration detected
- Password encryption: Custom cipher-based (`AltEncrypter` class)
- Files: `src/main/java/com/unicenta/pos/util/AltEncrypter.java`

## Hardware & Device Integration

**Payment Devices:**
- JavaPOS 2.0.10 (jpos) - Industry standard for POS devices
  - Supported device classes: Card readers, payment terminals, cash drawers
  - Files: `src/main/java/com/unicenta/pos/payment/`

**Serial Communication:**
- RXTX 2.2 (rxtxcomm) - Serial port for legacy devices
  - Purpose: Support for scales, magnetic card readers, other serial devices

**USB Devices:**
- USB4Java 1.2.0 - USB device communication
- USB API 1.0.2 - Standard USB interface
- Purpose: Direct USB peripherals (scales, card readers, receipt printers)

**1-Wire Devices:**
- OneWireAPI 0.1 - 1-Wire protocol support
- Purpose: Temperature sensors, barcode readers with 1-Wire interface

**Barcode Generation:**
- Barcode4j 2.1 - Barcode/QR code generation
- Purpose: Product labels, receipt markings
- Files: Referenced in POS printing/inventory modules

## Reporting & Output

**PDF/Document Generation:**
- JasperReports 6.4.0 + dependencies (fonts, dynamic jasper)
  - Purpose: Receipt templates, sales reports, financial statements
  - Template location: `src/main/resources/com/` (report definitions)
  - Files: `src/main/java/com/unicenta/pos/` (report execution)

- Apache FOP 2.1 (XSL-FO processor)
  - Purpose: Advanced report rendering from FOP templates

- iText 2.1.7 - Direct PDF manipulation
  - Purpose: Low-level PDF generation/modification

**Excel Export:**
- Apache POI 3.10.1
  - Purpose: Excel spreadsheet generation for data exports

**Document Printing:**
- Velocity 1.7 - Template engine
  - Purpose: Dynamic document template processing
  - Files: `src/main/java/com/unicenta/pos/scripting/ScriptEngineVelocity.java`

## Monitoring & Observability

**Error Tracking:**
- Not detected - no Sentry/Rollbar integration

**Logs:**
- Logback 1.2.2 + SLF4J
  - Configuration: `src/main/resources/logback.groovy` (Groovy DSL format)
  - Logging pattern: Annotation-based with Lombok `@Slf4j`
  - Files: `src/main/java/com/unicenta/pos/forms/StartPOS.java` uses `@Slf4j` annotation

**Metrics:**
- uniCenta Metrics plugin (com.unicenta.plugins.metrics.Metrics)
  - Purpose: Application metrics collection
  - Status: Plugin-based, customizable

## CI/CD & Deployment

**Hosting:**
- Standalone Java application (JAR)
- No cloud-specific deployment detected

**CI Pipeline:**
- GitHub Actions (via `.github/` directory)
- Maven-based build system
- Build artifact: `target/unicentaopos.jar`
- Artifact upload step configured

**Deployment Package:**
- Assembly includes:
  - JAR executable with embedded manifest
  - Bundled dependencies in `lib/` directory
  - Platform-native libraries (Windows, Linux, macOS)
  - Report templates in `target/reports/`
  - Configuration files in `target/Configs/`
  - Localization files in `target/locales/`

## Environment Configuration

**Required Environment Variables:**
- None explicitly required in code
- Database configuration driven by properties file (not env vars)

**Configuration File Locations:**
- Application properties: `AppConfig.properties` (loaded via AppConfig class)
- Database configuration: Multi-instance support with `db.*` and `db1.*` properties
- Locale configuration: Language/country/variant settings
- Look & Feel configuration: Swing UI theme selection

**Configuration Format:**
- Java properties files (key=value)
- Database passwords can be encrypted with cipher key: `crypt:{encrypted_value}`
- Decryption: `AltEncrypter` class with key pattern `cypherkey{username}`

**Secrets Location:**
- `.env` file location: Not enforced (uses properties file)
- Encrypted password storage in config properties
- No external secrets management system detected

## Webhooks & Callbacks

**Incoming:**
- Not detected - no webhook endpoints implemented

**Outgoing:**
- No outgoing webhook integrations detected
- Web service integration available via Apache Axis (legacy SOAP)

## Platform-Specific Integration

**Windows:**
- Native libraries in: `src/other/Windows/`
- Device driver support for Windows COM ports

**Linux:**
- Native libraries in: `src/other/Linux/`
- Serial device support via `/dev/ttyX*`

**macOS:**
- Native libraries in: `src/other/Mac_OS_X/`
- Serial device support via `/dev/tty*`

## Templating & Scripting

**Template Engine:**
- Velocity 1.7 - Document and report template generation
- Scripting support: BeanShell 2.0b4 embedded scripting engine
- Files: `src/main/java/com/unicenta/pos/scripting/` (ScriptFactory, ScriptEngineVelocity, etc.)

## Internationalization

**Locale Support:**
- Configurable default locale (language, country, variant)
- Message bundles for UI translation
- Bundle locations: `src/main/resources/` with language-specific files
  - Pattern: `{module}_messages_{language_code}.properties`
  - Examples: `pos_messages_el.properties`, `beans_messages_de.properties`, `data_messages_es_AR.properties`

---

*Integration audit: 2026-04-03*
