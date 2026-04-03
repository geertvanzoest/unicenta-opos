# Coding Conventions

**Analysis Date:** 2026-04-03

## Code Style

**Formatting:**
- Java 11 codebase with 4-space indentation
- Standard Java brace style: opening brace on same line as declaration
- No formatter/linter config present (no Checkstyle, Spotbugs, Google Java Format)
- Maven compiler plugin with `showDeprecation=true` and `debug=true`

**Line Length & Structure:**
- No explicit line length limit enforced
- Utility methods typically 10-40 lines, domain getters/setters 1-3 lines
- Large UI classes exist as outliers (see "Anti-patterns" below)

## Naming Conventions

### Classes

- **PascalCase** for all classes: `CustomerInfo`, `LuhnAlgorithm`, `PaymentInfoCash`
- **`*Info` suffix** for domain value objects: `CustomerInfo`, `TaxInfo`, `TicketInfo`, `UserInfo`, `CategoryInfo`, `TicketLineInfo`, `TicketTaxInfo`
- **`*Test` suffix** for test classes: `LuhnAlgorithmTest`, `CustomerInfoTest`
- **`*Editor` suffix** for UI editor panels: `ProductsEditor`, `CategoriesEditor`, `TaxEditor`
- **`*View` suffix** for read-only UI panels: `CustomersView`, `SuppliersView`
- **`DataLogic*` prefix** for data access layer: `DataLogicSales`, `DataLogicSystem`, `DataLogicAdmin`
- **`JPanel*`/`J*` prefix** for Swing panels: `JPanelTicket`, `JPanelCloseMoney`, `JRootApp`
- Utility classes use private constructors and only static methods: `StringUtils`, `RoundUtils`, `LuhnAlgorithm`

### Methods

- **camelCase** for all methods: `checkCC()`, `encodeXML()`, `authenticate()`, `hashString()`
- **`get*` prefix** for getters: `getId()`, `getName()`, `getTaxid()`, `getSearchkey()`
- **`set*` prefix** for setters: `setName()`, `setSearchkey()`, `setTaxid()`, `setPcode()`
- **`is*`/`has*` prefix** for boolean getters: `hasPrePay()`, `isCascade()`
- **`print*` prefix** for formatted-string methods: `printName()`, `printPaid()`, `printChange()`, `printTendered()`, `printTotal()`
- **`copy*` prefix** for cloning: `copyPayment()`, `copyTicketLine()`

### Variables and Fields

Use camelCase, but three legacy naming patterns coexist:

1. **`m_` prefix** (Hungarian notation) for private member fields -- dominant in legacy/upstream code:
   - 1395 occurrences across 250 files
   - Examples: `m_dPaid`, `m_dTotal`, `m_dTendered`, `m_sTicket`, `m_iLine`
   - Sub-patterns: `m_d` for double, `m_s` for String, `m_i` for int

2. **Type-initial prefix** for local/parameter variables (legacy):
   - `s` prefix for String: `sValue`, `sPassword`, `sHashPassword`
   - `d` prefix for double: `dTotal`, `dPaid`, `dMultiply`

3. **Plain camelCase** without prefix (newer code):
   - Examples: `prePayAmount`, `multiply`, `price`, `productid`, `curdebt`
   - Use this style for **all new code**

**Constants:**
- **UPPER_SNAKE_CASE**: `UPDATE_SQL`, `JOIN_SQL`, `RATE`, `DELTA`
- Legacy violations exist but do not replicate: `hexchars`, `cardformat` in `src/main/java/com/unicenta/pos/util/StringUtils.java` line 36

### Packages

- Reverse domain: `com.unicenta.pos.*`, `com.unicenta.data.*`, `com.unicenta.beans.*`
- Functional grouping by domain: `customers`, `payment`, `ticket`, `forms`, `util`, `inventory`

## Import Organization

**Order (as observed):**
1. Package declaration
2. Internal imports (`com.unicenta.*`)
3. Third-party imports (`org.*`, `com.formdev.*`)
4. Standard Java imports (`java.*`, `javax.*`)

Note: order is inconsistent across the codebase. Some files place `java.*` before `com.unicenta.*`. Use your IDE's default organize-imports.

**No wildcard imports** -- explicit imports only.

**Example from `src/main/java/com/unicenta/pos/ticket/TicketLineInfo.java`:**
```java
package com.unicenta.pos.ticket;

import com.unicenta.basic.BasicException;
import com.unicenta.data.loader.DataRead;
import com.unicenta.data.loader.DataWrite;
import com.unicenta.format.Formats;
import com.unicenta.pos.forms.AppLocal;
import com.unicenta.pos.util.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;
```

## Common Patterns

### File Header

Every source file starts with the GPL v3 license header (18 lines):
```java
//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2018 uniCenta
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//    ... (GPL v3 text)
```
**Do NOT add this header to new test files.** Only production source files carry it.

### Domain Objects (Info Classes)

Standard pattern observed across `src/main/java/com/unicenta/pos/ticket/`, `src/main/java/com/unicenta/pos/payment/`, `src/main/java/com/unicenta/pos/customers/`:

```java
public class TaxInfo implements Serializable, IKeyed {
    private static final long serialVersionUID = -2705212098856473043L;
    private String id;
    private String name;
    // ... fields

    public TaxInfo(String id, String name, ...) {
        this.id = id;
        this.name = name;
        // ...
    }

    @Override
    public Object getKey() { return id; }

    // getters and setters ...

    @Override
    public String toString() { return name; }
}
```

Key characteristics:
- Implement `Serializable` with explicit `serialVersionUID`
- Often implement `IKeyed` interface (returning primary key)
- `toString()` returns the human-readable name field
- Constructor initializes all fields; unset values are `null`
- No validation in setters

### Utility Classes

Static-only classes with private constructor to prevent instantiation:
```java
public class StringUtils {
    private StringUtils() {}
    public static String encodeXML(String sValue) { ... }
    public static String byte2hex(byte[] binput) { ... }
}
```

Files: `src/main/java/com/unicenta/pos/util/StringUtils.java`, `src/main/java/com/unicenta/pos/util/RoundUtils.java`, `src/main/java/com/unicenta/pos/util/LuhnAlgorithm.java`

### Constructor Overloading

Multiple constructors for flexibility, often with delegation:
```java
// PaymentInfoCash — 2-arg, 3-arg, 4-arg constructors
public PaymentInfoCash(double dTotal, double dPaid) { ... }
public PaymentInfoCash(double dTotal, double dPaid, double dTendered) { ... }
public PaymentInfoCash(double dTotal, double dPaid, double dTendered, double prePayAmount) {
    this(dTotal, dTendered, dPaid);
    this.prePayAmount = prePayAmount;
}
```

### Abstract Base Classes

Abstract classes define contracts for families of implementations:
```java
// src/main/java/com/unicenta/pos/payment/PaymentInfo.java
public abstract class PaymentInfo {
    public abstract String getName();
    public abstract double getTotal();
    public abstract PaymentInfo copyPayment();
    public abstract String getTransactionID();
    // ...
    public String printTotal() {
        return Formats.CURRENCY.formatValue(getTotal());
    }
}
```

## Error Handling

### Exception Hierarchy

- Base exception: `com.unicenta.basic.BasicException` extends `java.lang.Exception`
- Supports: no-arg, message, message+cause, cause-only constructors
- Used as checked exception throughout the data/business layer

### Error Handling Patterns

**Wrap-and-rethrow** (data layer):
```java
catch (SQLException eSQL) {
    throw new BasicException(eSQL);
}
```

**Log-and-swallow** (UI layer -- common but not ideal):
```java
catch (BasicException ex) {
    log.error(ex.getMessage());
}
```

**Expected exceptions** (via annotation in tests):
```java
@Test(expected = UnsupportedOperationException.class)
public void getTendered_throwsUnsupportedOperationException() {
    p.getTendered();
}
```

## Logging

### Framework

- SLF4J facade via **Logback** (`ch.qos.logback:logback-classic:1.2.2`)
- Logger injection via **Lombok `@Slf4j` annotation** on 53 classes
- Only 1 class uses manual `LoggerFactory.getLogger()`: `src/main/java/com/unicenta/pos/forms/DataLogicSales.java`
- 29 files still use `System.out.println()`/`System.err.println()` -- legacy code, do not replicate

### Configuration

Config file: `src/main/resources/logback.groovy` (Groovy DSL, not XML):
```groovy
def patternString = '%d{YYYY-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg %ex{20}%n'
def userHome = System.getProperty("user.home")

appender("applicationLogFile", RollingFileAppender) {
  rollingPolicy(TimeBasedRollingPolicy) {
    fileNamePattern = "$userHome/.unicenta/unicenta-%d{yyyy-MM-dd}.log"
    maxHistory = "5"
  }
}

appender('console', ConsoleAppender) { ... }
logger('com.unicenta', INFO)
root(INFO, ['console', 'applicationLogFile'])
```

### Usage Pattern

For new classes, use Lombok's `@Slf4j`:
```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyNewClass {
    public void doSomething() {
        log.info("Operation started");
        log.error("Failed: {}", ex.getMessage());
        log.debug("SQL: {}", sqlStatement);
    }
}
```

**Log levels observed:**
- `log.error(ex.getMessage())` -- exception handling (omits stack trace; use `log.error("msg", ex)` for new code)
- `log.info("descriptive message")` -- operational events, timing
- `log.debug("SQL: {}", statement)` -- only in `StaticSentence` and `PreparedSentence`
- `log.warn()` -- rarely used

## Documentation

### Javadoc

Upstream code has minimal Javadoc -- most methods have empty `@param`/`@return` tags:
```java
/**
 *
 * @param value
 */
public void setName(String value) {
    name = value;
}
```
This adds no value. For new code, only add Javadoc when it provides information not obvious from the method signature.

### File-Level Comments

- GPL header on production source files (see "File Header" above)
- Author annotations: `@author adrianromero`, `@author jack gerrard`, `@author JG uniCenta`
- Date annotations inline: `// JG 20 Sep 12 Extended for Postal`

### Test Comments

Tests use section headers to group related tests:
```java
// --- constructor sets id, others null ---
@Test public void constructorSetsId() { ... }

// --- setName / getName ---
@Test public void setNameThenGetName() { ... }
```

Alternative separator styles also used:
```java
// =========================================================================
// encodeXML
// =========================================================================
```

And:
```java
// -----------------------------------------------------------------------
// hashString(String)
// -----------------------------------------------------------------------
```

## Git/Commit Conventions

**Language:** Commit messages in Nederlands (Dutch)

**Format:** Conventional-commit style with Dutch descriptions:
```
<type>: <beschrijving>
```

**Types observed:**
- `test:` -- nieuwe unit tests
- `fix:` -- bugfixes
- `docs:` -- documentatie
- `ci:` -- CI/CD aanpassingen
- `deps:` -- dependency updates

**Examples from git log:**
```
test: unit tests voor PaymentInfoCash, PaymentInfoFree, VoucherPaymentInfo
fix: corrigeer SHA-1 verwachte waarde in HashcypherTest
docs: verwerk CodeRabbit review feedback op codebase documentatie
ci: JaCoCo coverage + Codecov integratie + badge
deps: bump maven-resources-plugin from 2.7 to 3.5.0 (#10)
```

**Branch naming:** `test/unit-tests-coverage`, `fix/build-maven`

**Workflow:** Always branch -> commit -> push -> PR. Never commit directly to `main`.

## Anti-patterns and Inconsistencies

### God Classes
Several files exceed 1000 lines (UI-heavy):
- `src/main/java/com/unicenta/pos/sales/JPanelTicket.java` (3515 lines)
- `src/main/java/com/unicenta/pos/transfer/Transfer.java` (3017 lines)
- `src/main/java/com/unicenta/pos/forms/DataLogicSales.java` (2827 lines)
- `src/main/java/com/unicenta/pos/config/JPanelConfigPeripheral.java` (2549 lines)
- `src/main/java/com/unicenta/pos/inventory/ProductsEditor.java` (2370 lines)

These are upstream legacy; do not refactor unless specifically tasked.

### Mixed Naming Styles
Three field naming conventions coexist (see "Variables and Fields" above). Use plain camelCase for all new code.

### Commented-Out Code
Production files contain commented-out code blocks:
```java
//    private double m_dTip;
//        return new PaymentInfoCash(m_dTotal, m_dPaid, prePayAmount);
```
Do not add commented-out code. Use version control instead.

### Empty TODO Comments
15+ auto-generated `// TODO add your handling code here:` from NetBeans IDE forms throughout `src/main/java/com/unicenta/pos/config/` and `src/main/java/com/unicenta/pos/payment/`.

### System.out Usage
73 occurrences of `System.out.println()`/`System.err.println()` across 29 files -- legacy. Use `@Slf4j` + `log.*()` for new code.

### Error Logging Without Stack Traces
Common pattern `log.error(ex.getMessage())` loses the stack trace. For new code, use:
```java
log.error("Operation failed", ex);  // includes stack trace
```

---

*Convention analysis: 2026-04-03*