# Codebase Concerns

**Analysis Date:** 2026-04-03

---

## Security Concerns

### SQL Injection via String Concatenation

**Severity: CRITICAL**

Multiple files build SQL queries via string concatenation with user-supplied values instead of using parameterized queries. This is the most serious security concern in the codebase.

**`src/main/java/com/unicenta/pos/sales/restaurant/RestaurantDBUtils.java`:**
- Lines 131, 152, 231, 252, 302, 374, 411, 486, 521, 542, 577, 598: At least 12 methods directly concatenate variables into SQL strings. Examples:
  ```java
  SQL = "SELECT customer FROM places WHERE NAME='"+ tableName + "'";   // line 131
  SQL = "SELECT customer FROM places WHERE ID='"+ tableId + "'";       // line 152
  SQL = "SELECT waiter FROM places WHERE NAME='"+ tableName + "'";     // line 231
  SQL = "SELECT TICKETID FROM places WHERE ID='"+ ID + "'";            // line 302
  SQL = "SELECT guests FROM places WHERE ID='"+ tableID + "'";         // line 374
  SQL = "SELECT COUNT(*) AS RECORDCOUNT FROM places WHERE TICKETID='"+ ticketID + "'";  // line 521
  ```
- Impact: Any user-controlled table or ticket identifier could inject arbitrary SQL.
- Fix approach: Replace all string concatenation with `PreparedStatement` parameter binding, which the class already uses for UPDATE/INSERT operations.

**`src/main/java/com/unicenta/pos/panels/JPanelCloseMoney.java`:**
- Lines 244, 248, 263, 267: Date values concatenated into SQL strings.
  ```java
  "WHERE TICKETID = 'No Sale' AND OPENDATE > " + "'" + m_PaymentsToClose.printDateStart() + "'"
  ```
- Impact: If date formatting ever produces unexpected output, SQL injection becomes possible.
- Fix approach: Use `PreparedStatement` with `setTimestamp()`.

**`src/main/java/com/unicenta/pos/panels/JPanelCloseMoneyReprint.java`:**
- Lines 219, 223, 237, 241: Same pattern as `JPanelCloseMoney.java`.

**`src/main/java/com/unicenta/pos/transfer/Transfer.java`:**
- Lines 2927, 2944: Database schema name concatenated into SQL.
  ```java
  "WHERE table_schema = " + "'" + selected + "'"
  ```

### Weak Cryptography

**Severity: HIGH**

**`src/main/java/com/unicenta/pos/util/AltEncrypter.java`:**
- Uses **DES-EDE (3DES) in ECB mode** (line 46: `DESEDE/ECB/PKCS5Padding`) for database password encryption.
- ECB mode is insecure -- identical plaintext blocks produce identical ciphertext blocks.
- The passphrase for key derivation is predictable: `"cypherkey" + username` (used across 20+ call sites).
- Exceptions during initialization are silently swallowed (lines 51-53), leaving `cipherEncrypt`/`cipherDecrypt` as `null` and causing NPE at runtime.
- Fix approach: Migrate to AES-256-GCM with a proper key derivation function (PBKDF2 or Argon2). Use a random IV per encryption. Store encrypted passwords in a secure credential store instead of `.properties` files.

**`src/main/java/com/unicenta/pos/util/Hashcypher.java`:**
- Uses **SHA-1** (line 71) for password hashing, which is deprecated for security purposes.
- No salting of passwords -- identical passwords produce identical hashes.
- Falls back to **plaintext** password storage (line 78: `"plain:" + sPassword`) if SHA-1 is unavailable.
- Also accepts plaintext comparison (line 54: `sHashPassword.equals("plain:" + sPassword)`).
- Fix approach: Use bcrypt, scrypt, or Argon2id for password hashing. Migrate existing SHA-1 hashes on next login.

### Hardcoded Encryption Key Prefix

**Severity: HIGH**

- Files: All files using `AltEncrypter` (20+ locations across the codebase).
- The string `"cypherkey"` is hardcoded as a key prefix in every `AltEncrypter` instantiation.
- Combined with the username (also stored in config), this means the encryption key is fully deterministic and recoverable.
- Files affected include:
  - `src/main/java/com/unicenta/pos/config/JPanelConfigDatabase.java` (lines 131, 147, 176, 186)
  - `src/main/java/com/unicenta/pos/forms/AppViewConnection.java` (lines 86, 96, 107, 119)
  - `src/main/java/com/unicenta/pos/sales/JPanelTicket.java` (line 2337)
  - `src/main/java/com/unicenta/orderpop/OrderPop.java` (line 290)
  - `src/main/java/com/unicenta/pos/imports/JPanelCSVCleardb.java` (line 97)
  - `src/main/java/com/unicenta/pos/transfer/Transfer.java` (lines 341, 364, 387)

### BeanShell Script Execution (Code Injection Surface)

**Severity: MEDIUM**

- The application uses BeanShell (`bsh.Interpreter`) to evaluate scripts stored as database resources.
- `src/main/java/com/unicenta/pos/scripting/ScriptEngineBeanshell.java`: Core eval engine.
- `src/main/java/com/unicenta/pos/sales/JPanelTicket.java` (line 3050-3059): Evaluates `script.SendOrder` from database.
- `src/main/java/com/unicenta/pos/forms/BeanFactoryScript.java` (line 58): Loads and evaluates scripts from resources.
- `src/main/java/com/unicenta/pos/forms/JPrincipalApp.java` (line 144): Menu structure built via script eval.
- If an attacker gains write access to the database `resources` table, they can execute arbitrary Java code.
- Impact: Full code execution on the POS machine.
- Current mitigation: Database access is required; this is not directly user-exploitable.

### Database Credentials in Properties File

**Severity: MEDIUM**

- Database credentials are stored in `~/{APP_ID}.properties` (user home directory).
- `src/main/java/com/unicenta/pos/forms/AppConfig.java`: Reads `db.user` and `db.password` properties.
- Password "encryption" uses the weak `AltEncrypter` described above.
- Multiple classes independently read and decrypt credentials instead of using a centralized auth service:
  - `src/main/java/com/unicenta/orderpop/OrderPop.java` (lines 286-291)
  - `src/main/java/com/unicenta/pos/imports/JPanelCSVCleardb.java` (lines 92-98)
  - `src/main/java/com/unicenta/pos/sales/JPanelResetPickupId.java` (lines 154-160)
  - `src/main/java/com/unicenta/pos/sales/JProductLineEdit.java` (lines 501-507)
  - `src/main/java/com/unicenta/pos/sales/JProductLineEditTax.java` (lines 520-526)

---

## Tech Debt

### God Objects

**`src/main/java/com/unicenta/pos/sales/JPanelTicket.java` (3515 lines):**
- The largest file in the codebase. Combines UI rendering, business logic (ticket management, payment processing, printing), database access, scripting, and event handling in one class.
- Extends `JPanel` and implements `JPanelView`, `BeanFactoryApp`, `TicketsEditor`.
- Fix approach: Extract payment logic, printing logic, script evaluation, and catalog management into separate service classes. Keep the JPanel focused on UI event handling.

**`src/main/java/com/unicenta/pos/transfer/Transfer.java` (3017 lines):**
- Handles both UI (Swing forms) and data migration logic (reading from source DB, writing to target DB).
- Contains 48 `ResultSet` references and 44 `SELECT *` queries.
- Fix approach: Separate into `TransferUI` (form) and `TransferService` (migration logic with per-table handler classes).

**`src/main/java/com/unicenta/pos/forms/DataLogicSales.java` (2827 lines):**
- Single data access class containing ALL sales-related queries.
- Mixes product, customer, category, tax, stock, receipt, and payment queries.
- Fix approach: Split into focused repositories: `ProductRepository`, `CustomerRepository`, `CategoryRepository`, `TaxRepository`, `StockRepository`.

**`src/main/java/com/unicenta/pos/config/JPanelConfigPeripheral.java` (2549 lines):**
- UI configuration panel for all peripherals in one file.

**`src/main/java/com/unicenta/pos/imports/JPanelCSVImport.java` (2076 lines):**
- CSV parsing, validation, DB insertion, and progress UI all in one class.

### Duplicated Code

**ProductFilter variants (6 near-identical files):**
- `src/main/java/com/unicenta/pos/ticket/ProductFilter.java`
- `src/main/java/com/unicenta/pos/ticket/ProductFilter1.java`
- `src/main/java/com/unicenta/pos/ticket/ProductFilter2.java`
- `src/main/java/com/unicenta/pos/ticket/ProductFilter3.java`
- `src/main/java/com/unicenta/pos/ticket/ProductFilter4.java`
- `src/main/java/com/unicenta/pos/ticket/ProductFilterSales.java`
- Fix approach: Create a single configurable `ProductFilter` that accepts field configuration, eliminating 5 near-duplicate classes.

**CategoryFilter duplication:**
- `src/main/java/com/unicenta/pos/ticket/CategoryFilter.java`
- `src/main/java/com/unicenta/pos/ticket/CategoryFilter_1.java`

**TicketLineInfo duplication:**
- `src/main/java/com/unicenta/pos/ticket/TicketLineInfo.java`
- `src/main/java/com/unicenta/pos/ticket/TicketLineInfo1.java` (637 lines)
- `TicketLineInfo1` is referenced only 11 times and appears to be a variant or copy.

**Database credential decryption:**
- The pattern `AltEncrypter("cypherkey" + user).decrypt(password.substring(6))` is duplicated across 13+ files instead of being centralized.

### Unused Dependencies

**`pom.xml` contains dependencies that appear unused or obsolete:**
- `mockito-junit-jupiter` (scope: test) -- All tests use JUnit 4, not JUnit 5 Jupiter.
- `oro` (Apache ORO regex, version 2.0.8) -- Extremely old library, last released 2003.
- `substance` (Swing LaF) and `trident` (animation) -- Likely replaced by FlatLaf.
- `weblaf` (version 1.2.9) and `weblaf-ui` (version 2.1.3) -- Two different versions of the same library.
- `charm-glisten` (Gluon mobile) -- Unlikely to be used in a desktop POS system.
- `je` (Berkeley DB Java Edition) -- No clear usage found.
- `OneWireAPI` -- Niche hardware interface.

### Unused/Example Classes

**`src/main/java/com/unicenta/pos/util/` contains unused example/test code:**
- `SwingWorkerExample.java` -- Example class not referenced elsewhere.
- `HtmlTester.java`, `HtmlTester2.java`, `HtmlTester3.java` -- Test/demo files.
- `NewFXMain.java`, `NewFXSwingMain.java` -- Standalone demo applications.
- `ElapsedTimeBetweenDates.java` -- Utility with `System.out.println` test code.

---

## Maintainability Risks

### Silent Exception Swallowing

**Severity: HIGH**

Over 50 catch blocks silently discard exceptions with empty catch bodies. This makes debugging production issues nearly impossible.

**Worst offenders:**

**`src/main/java/com/unicenta/pos/sales/restaurant/RestaurantDBUtils.java`:**
- Every single method (30+ methods) catches `SQLException` with an empty body. Database errors are completely invisible.

**`src/main/java/com/unicenta/pos/util/AltEncrypter.java`:**
- Constructor (lines 51-53): If encryption setup fails, no error is reported. `cipherEncrypt`/`cipherDecrypt` remain `null`, causing NPE on first use.
- `encrypt()` (lines 64-66): Returns `null` on failure with no logging.
- `decrypt()` (lines 78-80): Returns `null` on failure with no logging.

**`src/main/java/com/unicenta/pos/forms/DataLogicSystem.java`:**
- Lines 580-581, 665-666, 735-736, 746-747: Resource operations silently fail.

**`src/main/java/com/unicenta/pos/imports/JPanelCSVCleardb.java`:**
- Line 104: Database connection failure is silently swallowed.
- Line 118: `deactivate()` silently swallows close failures.
- Line 219: DELETE operation failure is silently swallowed.

**`src/main/java/com/unicenta/pos/printer/javapos/DeviceFiscalPrinterJavaPOS.java`:**
- Lines 89-90, 100-101, 115-116, 127-128, 142-143, 153-154, 164-165: Seven empty catch blocks for fiscal printer operations.

**`src/main/java/com/unicenta/pos/transfer/Transfer.java`:**
- Lines 465-468: `BatchUpdateException` AND `SQLException` both silently caught during `clearData()` -- a destructive batch DELETE across 20+ tables.

### Overly Broad Exception Catching

**Severity: MEDIUM**

45+ locations catch `Exception` (the base exception class) instead of specific exception types, masking programming errors.

- `src/main/java/com/unicenta/pos/forms/StartPOS.java` (lines 103, 124)
- `src/main/java/com/unicenta/pos/sales/KitchenDisplay.java` (lines 49, 76): `catch (Exception e){ }`
- `src/main/java/com/unicenta/pos/admin/OWWatch.java` (line 127): `catch (Exception e){}`
- `src/main/java/com/unicenta/pos/payment/JPaymentCashPos.java` (line 197)
- `src/main/java/com/unicenta/pos/config/JPanelConfigDatabase.java` (lines 1034, 1089, 1215)

### Console Output Instead of Logging

**Severity: LOW**

81 occurrences of `System.out.println`, `System.err.println`, or `e.printStackTrace()` across 33 files. The project has Logback configured (`logback.groovy`) and uses `@Slf4j` in some classes, but many classes still use console output.

Notable files:
- `src/main/java/com/unicenta/pos/admin/OWWatch.java` (8 occurrences)
- `src/main/java/com/unicenta/pos/util/SwingWorkerExample.java` (7 occurrences)
- `src/main/java/com/unicenta/pos/util/ElapsedTimeBetweenDates.java` (6 occurrences)
- `src/main/java/com/unicenta/pos/util/JRViewer400.java` (10 occurrences)

### Scattered Database Connection Management

**Severity: MEDIUM**

At least 13 classes independently call `DriverManager.getConnection()` instead of using the centralized `Session` class. Each independently decrypts credentials and manages its own connection lifecycle.

- `src/main/java/com/unicenta/orderpop/OrderPop.java` (line 294)
- `src/main/java/com/unicenta/pos/transfer/Transfer.java` (lines 200, 347, 370, 393, 2124)
- `src/main/java/com/unicenta/pos/config/JPanelTicketSetup.java` (line 358)
- `src/main/java/com/unicenta/pos/sales/JPanelResetPickupId.java` (line 166)
- `src/main/java/com/unicenta/pos/sales/JProductLineEdit.java` (line 514)
- `src/main/java/com/unicenta/pos/sales/JProductLineEditTax.java` (line 533)
- `src/main/java/com/unicenta/pos/imports/JPanelCSVCleardb.java` (line 101)

Fix approach: Create a `ConnectionFactory` or use the existing `Session` class consistently. Centralize credential decryption.

### Empty TODO Handlers

**Severity: LOW**

20+ NetBeans-generated event handler stubs contain only `// TODO add your handling code here:` and do nothing. These are non-functional event handlers attached to UI components.

- `src/main/java/com/unicenta/pos/config/JPanelConfigGeneral.java` (lines 611, 615, 623, 627, 638)
- `src/main/java/com/unicenta/pos/config/JPanelConfigDatabase.java` (line 1164)
- `src/main/java/com/unicenta/pos/config/JPanelConfigPeripheral.java` (line 2290)
- `src/main/java/com/unicenta/pos/config/JPanelConfigSystem.java` (lines 746, 750, 780, 784)
- `src/main/java/com/unicenta/pos/inventory/StockManagement.java` (line 1305)
- `src/main/java/com/unicenta/pos/imports/JPanelCSVImport.java` (lines 1829, 2008, 2016)

---

## Performance Concerns

### Resource Leaks

**Severity: HIGH**

**`src/main/java/com/unicenta/pos/sales/restaurant/RestaurantDBUtils.java`:**
- `ResultSet`, `Statement`, and `PreparedStatement` objects are stored as instance fields and never closed in individual methods. A single shared `Connection` is used across all operations.
- `rs`, `stmt`, `pstmt` are reassigned without closing the previous instance, causing resource leaks.
- The `Connection` is obtained once in the constructor (line 41) and never reconnected if it drops.

**`src/main/java/com/unicenta/pos/panels/JPanelCloseMoney.java`:**
- `Connection`, `Statement`, `ResultSet` used without `finally` blocks -- no `finally` block found anywhere in the file.
- Lines 238-274: Raw JDBC without try-with-resources or manual cleanup.

**`src/main/java/com/unicenta/pos/imports/JPanelCSVCleardb.java`:**
- `Connection` and `Statement` obtained in `activate()` (line 100-102), closed in `deactivate()` (lines 115-118).
- If `activate()` fails silently (exception swallowed at line 104), `stmt` and `con` remain `null`, causing NPE in `deactivate()`.

**General pattern: try-with-resources barely used**
- Only 32 try-with-resources occurrences across 17 files (out of 619 source files).
- The vast majority of JDBC code uses manual resource management without proper `finally` blocks.

### `SELECT *` Queries

**Severity: MEDIUM**

55 `SELECT *` queries found across 5 files, primarily in:
- `src/main/java/com/unicenta/pos/transfer/Transfer.java` (44 occurrences) -- Selects all columns from every table during data migration.
- `src/main/java/com/unicenta/pos/panels/JPanelCloseMoney.java` (4 occurrences)
- `src/main/java/com/unicenta/pos/panels/JPanelCloseMoneyReprint.java` (4 occurrences)

Impact: Transfers unnecessary data over the wire, wastes memory, and breaks when columns change.

### Single Database Connection (No Pooling)

**Severity: MEDIUM**

- `src/main/java/com/unicenta/data/loader/Session.java`: Maintains a single `Connection` object, reconnecting only if it drops.
- No connection pooling (HikariCP, C3P0, etc.) is used.
- All database operations share this single connection.
- Impact: Under concurrent usage (multiple POS terminals), this is a bottleneck. Long-running queries block the entire application.
- Fix approach: Introduce HikariCP as a connection pool. The `Session` class is the single point to modify.

### UI Thread Blocking

**Severity: MEDIUM**

Database queries are executed on the Swing EDT (Event Dispatch Thread) in several places, causing UI freezes:
- `src/main/java/com/unicenta/pos/panels/JPanelCloseMoney.java` (lines 238-274): Raw JDBC queries on EDT.
- `src/main/java/com/unicenta/pos/sales/restaurant/RestaurantDBUtils.java`: All methods execute synchronously on the calling thread (typically EDT).
- Some import operations correctly use `SwingWorker` (`JPanelCSVImport`, `StockQtyImport`, `CustomerCSVImport`), but most data operations do not.

---

## Dependency Vulnerabilities

### Severely Outdated Dependencies

**Severity: HIGH**

| Dependency | Current Version | Latest Stable | Risk |
|---|---|---|---|
| `sqlite-jdbc` | 3.7.2 | 3.45+ | Known CVEs, extremely old |
| `mysql-connector-java` | 5.1.39 | 8.3+ | Known CVEs, end of life |
| `mariadb-java-client` | 2.7.0 | 3.3+ | Missing security patches |
| `postgresql` | 9.4.1208 | 42.7+ | Known CVEs |
| `commons-beanutils` | 1.9.3 | 1.9.4 | CVE-2019-10086 |
| `itext` | 2.1.7 | 8.0+ | Multiple known CVEs, AGPL |
| `poi` (Apache POI) | 3.10.1 | 5.2+ | Known CVEs |
| `velocity` | 1.7 | 2.3+ | Known CVEs |
| `commons-collections` | 3.2.2 | 4.4 | Deserialization CVEs |
| `jasperreports` | 6.4.0 | 6.21+ | Missing security patches |
| `logback-classic` | 1.2.2 | 1.4+ | CVE-2021-42550 |
| `junit` | 4.12 | 4.13.2 | CVE-2020-15250 |
| `axis` | 1.4 | -- | End of life, known RCE CVEs |
| `lombok` | 1.18.6 | 1.18.32 | Compatibility issues |
| `flatlaf` | 1.6.5 | 3.4+ | Missing improvements |
| `derby` | 10.14.2.0 | 10.16+ | Known CVEs |

**Most critical:** `axis` 1.4 (Apache Axis 1 has been end-of-life since 2006 with known remote code execution vulnerabilities), `commons-collections` 3.x (deserialization gadget chain), and `logback-classic` 1.2.2 (JNDI injection).

### Build Tool Versions

- `maven-compiler-plugin` 2.3.2 is extremely outdated (current: 3.12+).
- `maven-dependency-plugin` 2.10 is outdated (current: 3.6+).

### Dependencies at Risk

**`axis:axis:1.4`:**
- Risk: End of life since 2006, known remote code execution CVEs via XML external entity (XXE) attacks.
- Impact: If used for SOAP communication, the entire POS system is vulnerable to remote attacks.
- Migration plan: Determine if SOAP integration is still needed. If yes, migrate to Apache CXF. If no, remove entirely.

**`commons-collections:3.2.2`:**
- Risk: Deserialization gadget chain allows remote code execution via crafted serialized objects.
- Impact: If untrusted serialized data is ever processed, full RCE is possible.
- Migration plan: Upgrade to `commons-collections4` (4.4+) which has the gadget chain removed.

**`logback-classic:1.2.2`:**
- Risk: CVE-2021-42550 -- JNDI injection vulnerability (similar to Log4Shell).
- Impact: If attacker can control log message content, they may achieve remote code execution.
- Migration plan: Upgrade to logback 1.2.13+ or 1.4.x.

**`bsh:2.0b4` (BeanShell):**
- Risk: Unmaintained since 2005. The `2.0b4` is a beta release that was never finalized.
- Impact: No security patches for any future vulnerabilities discovered.
- Migration plan: Consider Apache BeanShell 2.1+ (community fork) or migrate to GraalVM JavaScript/Groovy.

---

## Test Coverage Gaps

### Minimal Test Coverage

**Severity: HIGH**

- **23 test files** covering **619 source files** = ~3.7% file coverage.
- Core business logic files with no tests:
  - `src/main/java/com/unicenta/pos/sales/JPanelTicket.java` (3515 lines, 0 tests)
  - `src/main/java/com/unicenta/pos/forms/DataLogicSales.java` (2827 lines, 0 tests)
  - `src/main/java/com/unicenta/pos/forms/DataLogicSystem.java` (891 lines, 0 tests)
  - `src/main/java/com/unicenta/pos/panels/PaymentsModel.java` (1105 lines, 0 tests)
  - `src/main/java/com/unicenta/pos/sales/restaurant/RestaurantDBUtils.java` (623 lines, 0 tests)
  - `src/main/java/com/unicenta/pos/transfer/Transfer.java` (3017 lines, 0 tests)
  - `src/main/java/com/unicenta/pos/inventory/ProductsEditor.java` (2370 lines, 0 tests)

**Existing tests cover primarily:**
- Value objects / DTOs: `TicketInfo`, `CategoryInfo`, `CustomerInfo`, `TaxInfo`, `UserInfo`
- Utility classes: `AltEncrypter`, `Hashcypher`, `StringUtils`, `RoundUtils`, `Formats`
- Payment info classes: `PaymentInfoCash`, `PaymentInfoFree`, `VoucherPaymentInfo`

**Untested critical areas:**
- All data access logic (DataLogic* classes)
- All UI panels (JPanel* classes)
- Payment processing flow
- Ticket lifecycle management
- Restaurant module
- Import/export operations
- Printer/peripheral integration

---

## Modernization Opportunities

### Java 11+ Features Not Leveraged

The project targets Java 11 (`maven.compiler.source/target=11`) but uses few Java 11+ features:
- **try-with-resources** (Java 7): Only 32 usages vs. hundreds of manual resource management patterns.
- **var keyword** (Java 10): Not used anywhere.
- **String methods**: `isBlank()`, `strip()`, `repeat()` not used.
- **Optional**: Not used for null-safety.
- **Stream API** (Java 8): Minimal usage.
- **HttpClient** (Java 11): Not used.

### Framework Modernization

- **BeanShell** (`bsh` 2.0b4) is unmaintained. Consider migrating scripting to GraalVM JavaScript or Groovy.
- **Apache Axis 1.4** is end-of-life. If SOAP integration is needed, migrate to Apache CXF or JAX-WS RI.
- **Apache Velocity 1.7** is end-of-life. Migrate to Velocity 2.x (package changed from `org.apache.velocity` to `org.apache.velocity.engine`).
- **iText 2.1.7** is ancient. For PDF generation, consider OpenPDF (iText 2 fork, LGPL) or Apache PDFBox.
- **Joda-Time 2.9.7** should be replaced with `java.time` (available since Java 8).

### Build System

- Maven compiler plugin 2.3.2 does not support modern incremental compilation or annotation processing improvements. Update to 3.12+.
- No Maven wrapper (`mvnw`) -- requires pre-installed Maven.
- No dependency version management via `<dependencyManagement>` or a BOM.
- `AbsoluteLayout:RELEASE82` uses a non-reproducible version string.
- Consider adding `maven-enforcer-plugin` to prevent dependency conflicts.

---

*Concerns audit: 2026-04-03*
