# Coding Conventions

**Analysis Date:** 2026-04-03

## Naming Patterns

**Files:**
- POS UI components prefixed with `J` (Swing convention): `JDialogNewCustomer.java`, `JCustomerFinder.java`, `JPanel*`, `JDlg*`
- Data model classes with `Info` suffix: `CustomerInfo.java`, `TicketInfo.java`, `TaxInfo.java`
- Data access classes with `DataLogic` prefix: `DataLogicCustomers.java`, `DataLogicSales.java`
- View/UI classes with `View` or `Panel` suffix: `CustomersView.java`, `CustomersPanel.java`
- Action/handler classes with `Action` suffix: `MenuPanelAction.java`, `ProcessAction.java`
- Exception classes with `Exception` suffix: `BasicException.java`, `ScaleException.java`
- Dialog/UI windows prefixed with `J` or containing `Dialog`: `JDialogNewCustomer.java`, `ScaleDialog.java`

**Functions and Methods:**
- camelCase for all methods: `getId()`, `setName()`, `getTicketCount()`
- getter methods prefixed with `get`: `getHost()`, `getProperty()`, `getName()`
- setter methods prefixed with `set`: `setName()`, `setDirty()`, `setProperty()`
- test methods start with `should` or describe the test scenario: `shouldGroupTwoOfTheSameProduct()`, `shouldConvertUpdateToSQLite()`
- internal/helper methods prefixed with descriptive verb: `getLocalHostName()`, `fireChangedDirty()`, `addDirtyListener()`

**Variables:**
- camelCase for local and member variables: `m_instance`, `m_bDirty`, `searchkey`, `taxid`
- boolean fields sometimes use `m_b` or `is` prefix: `m_bDirty` (older code), `isDirty()` (modern)
- constants in UPPER_SNAKE_CASE: `serialVersionUID`, `UPDATE_SQL`, `JOIN_SQL`
- abbreviations kept short: `m_` for member variables (older convention), `s` prefix for static

**Types:**
- PascalCase for class names: `CustomerInfo`, `TicketInfo`, `StartPOS`
- PascalCase for interface names: `AppProperties`, `DirtyListener`
- Exception classes extend `Exception` and end with `Exception`: `BasicException`

## Code Style

**Formatting:**
- Java 11 language level
- Source encoding: UTF-8
- Indentation: 4 spaces (standard Java)
- Line length: No strict limit observed (some files exceed 100 chars)
- No explicit code formatter configured in project

**Linting:**
- No CheckStyle, Spotbugs, or PMD configuration present
- Compiler options in pom.xml: `showDeprecation=true`, `debug=true`
- Maven compiler version: 2.3.2 with source/target 11

**Code Organization:**
- Copyright headers: GPL v3 license header at top of source files
- Author comments: `@author` JavaDoc tags with developer names
- Date annotations: `// JG 20 Sep 12 Extended for...` style inline comments

## Import Organization

**Order:**
1. Package declaration
2. External library imports (organized by package)
3. Internal/project imports (organized by package)
4. Lombok imports (if used)
5. Java standard library imports

**Example from `AppConfig.java`:**
```java
package com.unicenta.pos.forms;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Locale;
import java.util.Properties;
```

**Path Aliases:**
- No path aliases detected
- Full package paths used throughout: `com.unicenta.pos.forms`, `com.unicenta.pos.customers`

## Error Handling

**Patterns:**
- Try-catch blocks for expected exceptions (IOException, SQLException, UnknownHostException)
- Specific exception types caught, not generic `Exception`
- Exception messages logged at error level when using Lombok `@Slf4j`
- Fallback/default values returned on exception (e.g., `"localhost"` in `getLocalHostName()`)

**Example from `AppConfig.java`:**
```java
private String getLocalHostName() {
    try {
        return java.net.InetAddress.getLocalHost().getHostName();
    } catch (java.net.UnknownHostException eUH) {
        return "localhost";
    }
}
```

**Exception Propagation:**
- Methods declare thrown exceptions in signature: `throws Exception`, `throws IOException`, `throws SQLException`
- Allows caller to handle or propagate further

**Custom Exceptions:**
- `BasicException` base class in `com.unicenta.basic` for domain-specific errors
- Supports chained exceptions: `BasicException(String msg, Throwable cause)`

## Logging

**Framework:** Logback (SLF4J facade)

**Dependencies:**
- `ch.qos.logback:logback-classic:1.2.2`
- `org.projectlombok:lombok:1.18.6` for `@Slf4j` annotation

**Annotation Usage:**
- Lombok `@Slf4j` decorator injects `log` field automatically (109 classes use this pattern)
- Files in: `com.unicenta.orderpop`, `com.unicenta.pos.forms`, `com.unicenta.pos.customers`, `com.unicenta.pos.scale`, etc.

**Logging Patterns:**
```java
// Info level for operations
log.info("Fetch Orders from DB");
log.info("Found {} orders", orders.size());
log.info("Time of getCustomersWithOutImage {}", (System.currentTimeMillis() - time));

// Error level for exceptions
log.error(ex.getMessage());
log.error("DB thread time-out + 3 sec's not shut down clean");
```

**Levels Used:**
- `log.info()` - operational flow, milestones
- `log.error()` - error conditions, exceptions
- `log.debug()` - commented out in current code (e.g., config file reading)

## Comments

**When to Comment:**
- File-level headers explaining purpose (not always present)
- `@author` tags with developer names
- Complex algorithm explanations (minimal in codebase)
- Date-stamped change notes: `// JG 20 Sep 12 Extended for Postal`
- Disabled code commented but kept (some loggers): `//log.debug("Reading configuration file...")`

**JavaDoc/JSDoc:**
- Basic JavaDoc for public methods with `@param` and `@return` tags
- Example from `AppConfig.java`:
```java
/**
 * Get key pair value from properties resource
 *
 * @param sKey key pair value
 * @return key pair from .properties filename
 */
@Override
public String getProperty(String sKey) {
    return properties.getProperty(sKey);
}
```

**Sparse but Present:**
- Not all methods have JavaDoc
- Class-level JavaDoc more common than method-level
- `@Override` annotation used consistently for interface implementations

## Function Design

**Size:** Functions range from 5 to 50+ lines
- Getter/setter methods: 1-5 lines
- Business logic methods: 10-40 lines
- Complex workflows: 50+ lines

**Parameters:**
- Constructor patterns with multiple string/object parameters
- Builder-like approach not used; direct object creation with setters
- Example: `new CustomerInfo(id)` then `setName()`, `setSearchkey()`, etc.

**Return Values:**
- Null returns are acceptable (properties return null if missing)
- Empty objects returned occasionally: `new ArrayList<>()` for empty lists
- Primitive wrappers used: `Boolean`, `Double`, `Integer`

## Module Design

**Exports:**
- No barrel file pattern observed
- Direct import of classes: `import com.unicenta.pos.customers.CustomerInfo;`
- All classes in same package imported explicitly

**Barrel Files:**
- Not used; single-class files are norm

## Lombok Usage

**Pattern:**
- `@Slf4j` extensively used for automatic logger injection
- No `@Data`, `@Getter`, `@Setter` found
- Manual getter/setter patterns preferred in data model classes
- Reduces boilerplate for logging only

---

*Convention analysis: 2026-04-03*
