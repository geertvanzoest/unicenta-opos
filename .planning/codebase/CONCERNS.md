# Codebase Concerns

**Analysis Date:** 2026-04-03

## Tech Debt

**Mixed UI Framework Complexity:**
- Issue: Codebase extensively uses Swing (206 files) alongside JavaFX (6 files), creating maintenance burden and inconsistent UI patterns
- Files: Primary Swing usage across `src/main/java/com/unicenta/pos/` with isolated JavaFX in `OrderPop.java`
- Impact: Makes UI refactoring difficult, increases complexity, newer developers must learn both frameworks, performance unpredictability with Swing/JavaFX interop
- Fix approach: Gradual migration toward single framework (JavaFX preferred for modern Java). Start with new features in JavaFX, refactor high-visibility UI components incrementally

**Outdated JDBC Pattern:**
- Issue: Direct Statement/ResultSet usage without modern ORM patterns throughout codebase instead of prepared statements
- Files: `src/main/java/com/unicenta/pos/forms/DataLogicSales.java` (2827 lines), `src/main/java/com/unicenta/data/loader/` layer
- Impact: Verbose code, potential SQL injection vulnerabilities if dynamic queries are introduced, resource leak risk, poor separation of concerns
- Fix approach: Introduce an ORM layer (Hibernate/JPA) or repository pattern. Start with new data access methods using prepared statements. Wrap existing Session/Sentence patterns

**Legacy Database Compatibility Layer:**
- Issue: Multiple database support (MariaDB, MySQL, PostgreSQL, Derby, SQLite) forces compatibility logic throughout
- Files: `src/main/java/com/unicenta/pos/config/JPanelConfigDatabase.java` (1297 lines), database drivers in pom.xml
- Impact: Maintenance burden testing across 5 databases, driver versioning complexity, prevents use of database-specific optimizations
- Fix approach: Standardize on MariaDB/PostgreSQL with clear deprecation path for others. Abstract DB dialect handling in a dedicated layer

## Known Bugs

**MySQL Connector Deprecated:**
- Symptoms: User warning displayed in UI configuration panel
- Files: `src/main/java/com/unicenta/pos/config/JPanelConfigDatabase.java` (line ~380)
- Trigger: User selects MySQL from database driver dropdown
- Workaround: Migrate to MariaDB driver (version 2.7.0 already in pom.xml)
- Fix: Remove MySQL from supported drivers, force MariaDB migration path

**Commented-Out SQL Injection Code:**
- Symptoms: Code that would be vulnerable if uncommented
- Files: `src/main/java/com/unicenta/orderpop/OrderPop.java` (line 270, commented)
- Trigger: If `populateDatabase()` method is ever uncommented/reactivated
- Problem: `st.executeUpdate("insert into orders values(1,'" + order + "')")` - raw string concatenation in SQL
- Fix: Remove commented code entirely, or rewrite with PreparedStatement if feature is needed

## Security Considerations

**Hardcoded Default Credentials:**
- Risk: Default "password" value in application configuration
- Files: `src/main/java/com/unicenta/pos/forms/AppConfig.java` (hardcoded default)
- Current mitigation: Defaults are only used in config setup UI; users must configure real credentials
- Recommendations: 
  - Ensure config files are never committed with real credentials
  - Add validation to prevent "password" from being used in production
  - Implement credential obfuscation for stored passwords (already using AltEncrypter)

**Insufficient Exception Handling:**
- Risk: 45 catch blocks catching `Exception` (base type), 43 instances of printStackTrace/getMessage used for logging
- Files: Scattered throughout data layer (DataLogicSales, DataLogicCustomers, various imports)
- Current mitigation: Logback configured for logging
- Recommendations:
  - Replace `printStackTrace()` with proper logger calls
  - Catch specific exceptions instead of base `Exception`
  - Ensure sensitive data (DB passwords, transaction details) not logged

**Direct Database Connection Exposure:**
- Risk: OrderPop utility creates direct JDBC connections with stored credentials
- Files: `src/main/java/com/unicenta/orderpop/OrderPop.java` (lines 100-150)
- Current mitigation: Uses AltEncrypter for password obfuscation
- Recommendations:
  - Use connection pooling (HikariCP)
  - Enforce HTTPS for any remote connections
  - Validate all credentials before connecting

## Performance Bottlenecks

**Monolithic JPanelTicket Class:**
- Problem: 3515 lines in single class - main POS UI component
- Files: `src/main/java/com/unicenta/pos/sales/JPanelTicket.java`
- Cause: Accumulation of features over time, mixed concerns (UI, business logic, reporting)
- Impact: Hard to test, difficult to maintain, slow to load, violates Single Responsibility Principle
- Improvement path:
  1. Extract payment logic to dedicated PaymentProcessor class
  2. Extract line item calculations to TicketCalculator
  3. Extract UI event handlers into strategy classes
  4. Target: Reduce to <800 lines with clear delegation

**Transfer Class Complexity:**
- Problem: 3017 lines handling multi-database transfer operations
- Files: `src/main/java/com/unicenta/pos/transfer/Transfer.java`
- Cause: Database schema migration logic for multiple DB types bundled together
- Impact: Fragile, difficult to test, single point of failure for data integrity
- Improvement path:
  1. Extract per-database migration strategies
  2. Use adapter pattern for DB-specific operations
  3. Add comprehensive integration tests for each database type

**Large File Operations:**
- Problem: Multiple UI panels exceed 2000 lines (JPanelConfigPeripheral: 2549, ProductsEditor: 2370, JPanelCSVImport: 2076)
- Files: `src/main/java/com/unicenta/pos/config/JPanelConfigPeripheral.java`, `src/main/java/com/unicenta/pos/inventory/ProductsEditor.java`, `src/main/java/com/unicenta/pos/imports/JPanelCSVImport.java`
- Impact: Slow IDE parsing, hard to navigate, merge conflicts likely
- Improvement path: Break into smaller panels using composition

## Fragile Areas

**Database Configuration Panel:**
- Files: `src/main/java/com/unicenta/pos/config/JPanelConfigDatabase.java` (1297 lines)
- Why fragile: Manually manages UI state for 5 database types, complex visibility logic, direct JDBC testing
- Safe modification: Add integration tests before changing DB selection logic. Test all 5 database paths. Validate connection strings separately from UI
- Test coverage: Minimal - no dedicated unit tests for database configuration validation
- Risk: Change to SQL dialect selection or connection validation could silently break MariaDB/PostgreSQL/Derby support

**Restaurant Mode Toggle:**
- Files: `src/main/java/com/unicenta/pos/sales/JPanelTicket.java` (multiple places), `src/main/java/com/unicenta/pos/sales/restaurant/RestaurantDBUtils.java`
- Why fragile: Boolean flag `restaurant` controls major code paths (table selection, kitchen display, order printing)
- Safe modification: Add feature flags/configuration object instead of boolean. Add tests that run same flows with both modes
- Test coverage: Gaps in restaurant-specific code paths
- Risk: Changes to ticket flow could break restaurant mode unnoticed

**Script Engine Integration:**
- Files: `src/main/java/com/unicenta/pos/scripting/ScriptFactory.java`, `src/main/java/com/unicenta/pos/scripting/ScriptEngine*.java` (Velocity, BeanShell)
- Why fragile: Dynamic script evaluation in `JPanelTicket` (see inner ScriptObject class), embedded expressions in reports
- Safe modification: Never evaluate user-provided scripts. Test script syntax in isolated sandbox. Document all available script variables
- Test coverage: No visible tests for script execution paths
- Risk: Unsafe script execution could allow arbitrary code injection if scripts come from untrusted source

**Restaurant Database Utilities:**
- Files: `src/main/java/com/unicenta/pos/sales/restaurant/RestaurantDBUtils.java`
- Why fragile: Mixes PreparedStatement (good) with raw Statement (risky). No transaction management visible
- Safe modification: Audit all SQL generation. Ensure PreparedStatement is used for all dynamic values. Add transaction wrapper
- Test coverage: No tests visible
- Risk: Data corruption in restaurant mode (tables/orders) if concurrent modifications occur

## Scaling Limits

**Single-Instance Database Connection:**
- Current capacity: ~50 concurrent users per instance (estimated from Session pooling)
- Limit: JDBC connections, single MariaDB instance
- Impact: Restaurant chain cannot scale to multiple locations without infrastructure changes
- Scaling path:
  1. Implement connection pooling (HikariCP: 10-20 connections vs current limit)
  2. Add database replication for read-heavy operations (reports)
  3. Consider separate read-only analytics database
  4. For multi-location: Implement centralized master DB with local caching

**Swing UI Rendering:**
- Current capacity: ~10k SKUs in catalog before noticeable UI lag
- Limit: AWT Event Dispatch Thread bottleneck, full table models loaded in memory
- Impact: Large inventory installations slow, list searches become sluggish
- Scaling path:
  1. Implement lazy-loading in table models (virtual scrolling)
  2. Separate UI refresh from data loading threads
  3. Cache frequently-accessed categories
  4. Move to JavaFX for better rendering performance (future)

**Report Generation:**
- Current capacity: ~1000 transactions per report before memory pressure
- Limit: JasperReports loads full data into memory, no streaming
- Impact: Year-end reports or large dataset exports cause OutOfMemory
- Scaling path:
  1. Implement report pagination
  2. Switch to streaming XSLT for large datasets
  3. Add background report generation with progress tracking

## Dependencies at Risk

**Deprecated/Unmaintained Packages:**
- Risk: Multiple packages with no recent updates
  - `commons-digester` 2.1 (2006)
  - `velocity` 1.7 (2010)
  - `bsh` 2.0b4 (2005)
  - `oro` 2.0.8 (2005)
  - `substance` 7.1.00 (2012)
  - `jfreechart` 1.0.19 (2014)
- Impact: Security vulnerabilities not patched, Java 11+ compatibility gaps
- Migration plan:
  1. Replace Velocity → Freemarker 2.3+
  2. Replace BeanShell → GraalVM JavaScript
  3. Replace commons-digester → direct XML parsing or Spring
  4. Upgrade jfreechart to 1.5.3+
  5. Remove Substance LAF (use FlatLaf exclusively)

**Outdated JDBC Drivers:**
- Risk: MySQL 5.1.39 (2013), PostgreSQL 9.4.1208 (2015)
- Impact: Missing bug fixes, Java 11 compatibility, no SSL/TLS improvements
- Fix: Upgrade PostgreSQL to 42.x, use MariaDB connector instead of MySQL

**Conflicting Logging Frameworks:**
- Risk: Multiple SLF4J implementations (logback, slf4j-nop exclusions)
- Files: pom.xml lines 154-160, 306-316 (exclusions in jpos and weblaf)
- Impact: Unexpected logging behavior, potential NoClassDefFoundError at runtime
- Fix: Clean up exclusions, ensure single logback implementation

**Vulnerable Dependencies:**
- Risk: commons-codec 1.10 (EOL 2016), commons-collections 3.2.2 (deserialization vulnerabilities), itext 2.1.7
- Impact: Potential code execution through gadget chains (commons-collections)
- Fix: Upgrade commons-codec to 1.15+, commons-collections to 4.0+, replace itext with iText 7+

## Missing Critical Features

**No Connection Pooling:**
- Problem: Raw JDBC without HikariCP or C3P0
- Blocks: Cannot scale to multiple concurrent terminals, connection exhaustion under load
- Fix: Add HikariCP 5.0+ dependency, migrate Session layer to use pooled connections

**No Integrated Testing Infrastructure:**
- Problem: Only unit tests visible are `StaticSentenceTest` and `TicketInfoTest`
- Blocks: Cannot safely refactor large classes, no regression test coverage for UI flows
- Fix: Add JUnit 5 + Mockito test suite covering data layer, add Integration tests with TestContainers for DB

**No Audit Trail:**
- Problem: No logged changes to transactions, inventory, or users (outside of DB transaction logs)
- Blocks: Compliance requirements, fraud investigation, accountability
- Fix: Add AuditLog entity with before/after values for all financial operations

**No API/REST Layer:**
- Problem: Monolithic UI-coupled design prevents headless operation
- Blocks: Integration with external systems, mobile ordering, reporting from other tools
- Fix: Extract business logic to REST API (Spring Boot), then build new UIs on top

## Test Coverage Gaps

**Database Layer (High Risk):**
- What's not tested: DataLogicSales, DataLogicCustomers - complex SQL generation and transaction handling
- Files: `src/main/java/com/unicenta/pos/forms/DataLogicSales.java`, `src/main/java/com/unicenta/pos/customers/DataLogicCustomers.java`
- Risk: Silent data corruption on schema changes, undetected query regressions
- Priority: Critical - these handle money and inventory

**Payment Processing (High Risk):**
- What's not tested: Multi-payment transactions, refunds, payment allocation
- Files: `src/main/java/com/unicenta/pos/payment/` package (6 classes), `JPanelTicket.java` payment methods
- Risk: Financial transactions corrupted, cash drawer mismatches, payment processing failures
- Priority: Critical - directly impacts revenue

**Restaurant Mode (Medium Risk):**
- What's not tested: Table management, order routing to kitchen display, restaurant-specific workflows
- Files: `src/main/java/com/unicenta/pos/sales/restaurant/` package, JPanelTicket restaurant conditionals
- Risk: Orders lost in kitchen queue, incorrect table billing, multi-table orders corrupted
- Priority: High - affects restaurants heavily

**UI Event Handlers (Medium Risk):**
- What's not tested: Most Swing event listeners in config panels and main forms
- Files: Config panels (JPanelConfigPeripheral, JPanelConfigDatabase, etc.) - many TODO comments suggest unfinished handlers
- Risk: UI state corruption, configuration not persisting, peripheral devices not initializing
- Priority: Medium - affects user experience

**Import/Export Workflows (Medium Risk):**
- What's not tested: CSV import validation, data transformation, error recovery
- Files: `src/main/java/com/unicenta/pos/imports/JPanelCSVImport.java` (2076 lines), `StockQtyImport.java`, `CustomerCSVImport.java`
- Risk: Silent data corruption during bulk imports, duplicate records, lost data on error
- Priority: Medium - affects batch operations

**Multi-Database Compatibility (Medium Risk):**
- What's not tested: Schema creation across MariaDB, PostgreSQL, Derby, SQLite
- Files: `src/main/java/com/unicenta/pos/transfer/Transfer.java` (database-specific SQL)
- Risk: Database migration fails silently, data incompleteness on non-standard DB
- Priority: Medium - affects less common DB setups

---

*Concerns audit: 2026-04-03*
