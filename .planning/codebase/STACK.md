# Technology Stack

**Analysis Date:** 2026-04-03

## Languages

**Primary:**
- Java 11 - All application code (`maven.compiler.source=11`, `maven.compiler.target=11`)

**Secondary:**
- Groovy - Logback configuration only (`src/main/resources/logback.groovy`)
- BeanShell - Runtime scripting for reports and UI customization (70 `.bs` scripts in `src/main/resources/com/unicenta/reports/`)
- SQL - Database schema and migration scripts (21 `.sql` files in `src/main/resources/com/unicenta/pos/scripts/`)

## Runtime

**Environment:**
- JDK/JRE 11+ (OpenJDK or Oracle JDK)
- CI uses Eclipse Temurin 11 (`.github/workflows/ci.yml`)

**Package Manager:**
- Apache Maven 3.x
- Lockfile: not present (Maven does not use lockfiles; versions pinned in `pom.xml`)

## Frameworks

**Core:**
- JavaFX 11 (OpenJFX) - Modern UI framework, modules: javafx-base, javafx-controls, javafx-fxml, javafx-graphics, javafx-media, javafx-swing, javafx-web
- Swing - Legacy desktop UI with extended components (SwingX 1.6.5-1, swing-layout 1.0.3)
- FlatLaf 1.6.5 - Modern flat Look and Feel for Swing (+ flatlaf-intellij-themes 2.0.1, flatlaf-swingx 1.6.5)
- Substance 7.1.00 - Alternative Swing Look and Feel
- WebLAF 1.2.9 / weblaf-ui 2.1.3 - Additional Look and Feel

**Testing:**
- JUnit 4.12 - Unit testing (scope: test)
- Mockito 4.2.0 - Mocking (mockito-inline for static mocking, mockito-junit-jupiter unused but present)
- JaCoCo 0.8.12 - Code coverage (maven plugin, reports to `target/site/jacoco/`)

**Build/Dev:**
- maven-compiler-plugin 2.3.2 - Java compilation (showDeprecation=true, debug=true)
- maven-jar-plugin 3.2.0 - Executable JAR packaging with manifest classpath
- maven-dependency-plugin 2.10 - Copies dependencies to `target/lib/`
- maven-resources-plugin 3.5.0 - Copies platform-native libraries, scripts, templates, reports, locales
- jacoco-maven-plugin 0.8.12 - Coverage instrumentation and reporting
- wagon-ftp 2.10 - Distribution management via FTP

## Key Dependencies

**Critical (core application functionality):**
- `org.openjfx:javafx-*` 11 - GUI framework modules
- `org.mariadb.jdbc:mariadb-java-client` 2.7.0 - Primary database driver
- `net.sf.jasperreports:jasperreports` 6.4.0 - Report generation engine
- `com.formdev:flatlaf` 1.6.5 - Default Look and Feel
- `org.projectlombok:lombok` 1.18.6 (provided) - @Slf4j, @Getter, @Setter annotations
- `ch.qos.logback:logback-classic` 1.2.2 - Logging implementation

**Database Drivers:**
- `org.mariadb.jdbc:mariadb-java-client` 2.7.0 - MariaDB (primary)
- `mysql:mysql-connector-java` 5.1.39 - MySQL
- `org.postgresql:postgresql` 9.4.1208 - PostgreSQL
- `org.apache.derby:derby` 10.14.2.0 - Apache Derby (embedded, default for fresh installs)
- `org.xerial:sqlite-jdbc` 3.7.2 - SQLite (experimental)

**Hardware Integration:**
- `com.javapos:jpos` 1.13 - JavaPOS hardware abstraction (printers, displays, fiscal devices)
- `org.jpos:jpos` 2.0.10 - ISO 8583 payment terminal protocol
- `org.bidib.jbidib.org.qbang.rxtx:rxtxcomm` 2.2 - Serial/parallel port communication
- `org.usb4java:usb4java` 1.2.0 + `javax.usb:usb-api` 1.0.2 - USB device communication
- `com.dalsemi.onewire:OneWireAPI` 0.1 - iButton/1-Wire authentication tokens

**Reporting & Documents:**
- `net.sf.jasperreports:jasperreports` 6.4.0 - Report templates (.jrxml, 67 templates)
- `net.sf.jasperreports:jasperreports-fonts` 6.0.0 + `ar.com.fdvs:DynamicJasper-core-fonts` 2.0 - Embedded fonts
- `org.eclipse.jdt.core.compiler:ecj` 4.6.1 - JasperReports expression compiler
- `com.lowagie:itext` 2.1.7 - PDF generation (legacy, excluded from jasperreports to avoid conflict)
- `org.apache.xmlgraphics:fop` 2.1 - XSL-FO to PDF/print
- `org.apache.poi:poi` 3.10.1 - Excel export (XLS format)
- `net.sf.barcode4j:barcode4j` 2.1 - Barcode generation (EAN-13, Code128, UPC-A, etc.)

**Data Processing:**
- `org.beanshell:bsh` 2.0b4 - Runtime scripting engine for report queries and UI logic
- `org.apache.velocity:velocity` 1.7 - Template engine for dynamic content
- `net.sourceforge.javacsv:javacsv` 2.0 - CSV parsing for product/customer imports
- `joda-time:joda-time` 2.9.7 - Date/time manipulation

**Apache Commons:**
- `commons-beanutils` 1.9.3 - Bean property manipulation
- `commons-codec` 1.10 - Encoding/decoding
- `commons-collections` 3.2.2 - Collection utilities
- `commons-digester` 2.1 - XML-to-object mapping
- `commons-discovery` 0.5 - Service discovery (SOAP)
- `commons-lang` 2.6 - String/utility functions

**Charting:**
- `org.jfree:jfreechart` 1.0.19 + `org.jfree:jcommon` 1.0.24 - Charts in reports

**Web Services:**
- `axis:axis` 1.4 - SOAP client
- `org.apache.axis:axis-jaxrpc` 1.4 - JAX-RPC API
- `axis:axis-wsdl4j` 1.5.1 - WSDL parsing
- `javax.xml.soap:saaj-api` 1.3.5 - SOAP with Attachments

**UI Components:**
- `org.swinglabs.swingx:swingx-all` 1.6.5-1 - Extended Swing components
- `org.swinglabs:swing-layout` 1.0.3 - GroupLayout for Swing
- `org.netbeans.external:AbsoluteLayout` RELEASE82 - NetBeans layout manager
- `org.jdatepicker:jdatepicker` 1.3.4 - Date picker widget
- `com.gluonhq:charm-glisten` 6.2.2 - Gluon mobile-to-desktop UI bridge
- `org.pushingpixels:trident` 1.4 - Animation library

**Other:**
- `javax.xml.bind:jaxb-api` 2.3.1 - XML binding
- `javax.persistence:persistence-api` 1.0.2 - JPA interfaces (no ORM implementation used)
- `xml-apis:xml-apis` 1.0.b2 - XML processing
- `oro:oro` 2.0.8 - Regular expressions (legacy, Velocity dependency)
- `org.reflections:reflections` 0.9.12 - Classpath scanning
- `com.sleepycat:je` 5.0.73 - Berkeley DB Java Edition (embedded key-value store, legacy)
- `com.unicenta:unicenta-plugins` 1.1 - Plugin framework (payment integrations, metrics)
- `uk.co.pos_apps:openbravo` 1.0-SNAPSHOT - Openbravo ERP integration

## Configuration

**Application Configuration:**
- Properties file: `[user.home]/unicentaopos.properties` (created on first run)
- Managed by: `com.unicenta.pos.forms.AppConfig` (`src/main/java/com/unicenta/pos/forms/AppConfig.java`)
- Defaults set in `loadDefault()` method covering database, printer, locale, payment, device, and table settings
- Separate configuration UI entry point: `com.unicenta.pos.config.JFrmConfig` (launched via `configure.sh`)

**Key config properties:**
- `db.engine` - Database type (Derby default)
- `db.URL`, `db.user`, `db.password` - Connection credentials
- `db.multi` - Enable secondary database
- `machine.printer` - Receipt printer type (screen/printer/epson/star/javapos)
- `machine.scale` - Weight scale type
- `machine.display` - Customer display type
- `machine.screenmode` - Window mode (fullscreen/windowed)
- `swing.defaultlaf` - UI theme class
- `payment.gateway` - Payment processor (external/PaymentSense)

**Logging:**
- Config file: `src/main/resources/logback.groovy`
- Log location: `[user.home]/.unicenta/unicenta-YYYY-MM-DD.log`
- Rolling policy: TimeBasedRollingPolicy, max 5 days history
- Default level: INFO for `com.unicenta` package
- Appenders: console + file

**Build Configuration:**
- Source encoding: UTF-8
- Compiler: showDeprecation=true, debug=true
- Output JAR: `target/unicentaopos.jar` (finalName: unicentaopos)

## Maven Repositories

- Maven Central: `https://repo1.maven.org/maven2/`
- uniCenta Repository: `https://repo.unicenta.org/maven2/` (custom dependencies: unicenta-plugins, openbravo)
- Gluon Nexus: `https://nexus.gluonhq.com/nexus/content/repositories/releases/` (charm-glisten)
- Distribution: FTP to `ftp://repo.unicenta.org/` via wagon-ftp

## CI/CD

**GitHub Actions Workflows:**
- `ci.yml` - Build + test + coverage upload (Temurin JDK 11, `mvn -B clean verify`)
  - Codecov upload: `target/site/jacoco/jacoco.xml`
  - Build artifact: `target/unicentaopos.jar` (retained 30 days, main branch only)
- `semgrep.yml` - SAST security scanning via Semgrep container
- `claude.yml` - Additional CI workflow

**External Services:**
- Codecov - Code coverage tracking
- Semgrep - Static analysis security scanning
- CodeRabbit - Automated code review on PRs

## Platform Requirements

**Development:**
- JDK 11+ (Temurin recommended)
- Maven 3.6+
- Database server: MariaDB/MySQL for full functionality, Derby for embedded testing

**Production:**
- JRE 11+
- Database: MariaDB (recommended), MySQL, PostgreSQL, or embedded Derby
- Minimum memory: `-Xms512m -Xmx1024m` (per `start.sh`)
- Cross-platform: Windows, Linux (x86/x86_64/ia64), macOS, Solaris (SPARC)
- Native libraries required for serial port access (RXTX `.so`/`.dll`/`.jnilib` in `src/other/`)

**Deployment Artifacts:**
- `target/unicentaopos.jar` - Main executable
- `target/lib/` - All dependency JARs
- `target/lib/Windows/`, `target/lib/Linux/`, `target/lib/Mac_OS_X/` - Platform-specific RXTX natives
- `target/locales/` - Localization bundles
- `target/reports/` - JasperReport templates
- `target/Configs/` - Configuration templates
- `target/Templates/` - Print templates
- `target/Bonus/` - Sample product images
- `start.sh` / `start.bat` - Launch scripts
- `configure.sh` / `configure.bat` - Configuration UI launch scripts

## Project Metadata

**Maven Coordinates:**
- Group ID: `com.unicenta`
- Artifact ID: `unicentaopos`
- Version: `5.0`
- Packaging: `jar`

**Application Identity:**
- APP_NAME: `uniCenta oPOS` (defined in `src/main/java/com/unicenta/pos/forms/AppLocal.java`)
- APP_ID: `unicentaopos`
- APP_VERSION: `5.0`
- Main class: `com.unicenta.pos.forms.StartPOS`

---

*Stack analysis: 2026-04-03*
