# Coding Conventions

**Analysis Date:** 2026-04-03

## Code Style

**Formatting:**
- Java 11 codebase with 4-space indentation (implicit)
- Standard Java brace style: opening brace on same line as declaration
- No explicit code style config file (Checkstyle, Spotbugs) configured
- Maven compiler plugin with `showDeprecation=true` and debug enabled

**Line Length & Structure:**
- No explicit line length limit enforced
- Methods are typically under 100 lines, utilities under 50 lines
- Classes follow single responsibility principle (e.g., `LuhnAlgorithm.java`, `StringUtils.java` are single-purpose utilities)

**Example from `StringUtils.java` (lines 59-83):**
```java
public static String encodeXML(String sValue) {
    if (sValue == null) {
        return null;
    } else {
        StringBuilder buffer = new StringBuilder();      
        for (int i = 0; i < sValue.length(); i++) {
            char charToCompare = sValue.charAt(i);
            if (charToCompare == '&') {
                buffer.append("&amp;");
            } else if (charToCompare == '<') {
                buffer.append("&lt;");
            } else if (charToCompare == '>') {
                buffer.append("&gt;");
            } else if (charToCompare == '\"') {
                buffer.append("&quot;");
            } else if (charToCompare == '\'') {
                buffer.append("&apos;");
            } else {
                buffer.append(charToCompare);
            }
        }
        return buffer.toString();
    }
}
```

## Naming Conventions

**Classes:**
- PascalCase: `CustomerInfo`, `LuhnAlgorithm`, `PaymentInfoCash`, `StringUtils`
- Info/Data suffix for domain objects: `CustomerInfo`, `PaymentInfoCash`, `TicketInfo`, `UserInfo`, `CategoryInfo`
- Test classes suffix with `Test`: `LuhnAlgorithmTest`, `CustomerInfoTest`, `PaymentInfoCashTest`
- Utility classes typically static-only: `StringUtils`, `RoundUtils`, `LuhnAlgorithm` (private constructor)

**Methods:**
- camelCase: `checkCC()`, `encodeXML()`, `authenticate()`, `hashString()`, `changePassword()`
- Getters use `get` prefix: `getId()`, `getName()`, `getTaxid()`, `getSearchkey()`
- Setters use `set` prefix: `setName()`, `setSearchkey()`, `setTaxid()`, `setPcode()`
- Boolean getters use `is` or `has` prefix: `hasPrePay()`, `isValid()`
- Print/format methods use `print` prefix: `printName()`, `printPaid()`, `printChange()`, `printTendered()`

**Variables:**
- camelCase: `cardNumber`, `sPassword`, `m_sentence`, `m_SerWrite`, `curdebt`
- Field prefixes (legacy pattern): `m_` for members (`m_Stmt`, `m_sentence`, `m_SerRead`, `m_SerWrite`)
- Acronyms lowercase in camelCase: `sValue`, `sHashPassword`, `sPassword` (s prefix indicates String type)
- Protected/private fields with no prefix but prefixed with type initial: `protected String id`, `protected Double curdebt`

**Constants:**
- UPPER_SNAKE_CASE: `serialVersionUID`, `hexchars`, `cardformat`, `cardrandom`
- Example from `StringUtils.java` (line 36): `private static final char [] hexchars = {'0', '1', ...}`

**Package Names:**
- Reverse domain notation: `com.unicenta.pos`, `com.unicenta.data`, `com.unicenta.beans`, `com.unicenta.format`
- Functional grouping: `com.unicenta.pos.customers`, `com.unicenta.pos.payment`, `com.unicenta.pos.util`, `com.unicenta.pos.forms`

## Import Organization

**Order:**
1. Package declaration
2. Standard Java imports (`java.*`, `javax.*`)
3. Third-party imports (`org.*`, `com.*` external)
4. Internal imports (`com.unicenta.*`)

**Example from `CustomerInfo.java`:**
```java
package com.unicenta.pos.customers;

import com.unicenta.pos.util.StringUtils;
import java.awt.image.BufferedImage;
import java.io.Serializable;
```

**No wildcard imports observed** - explicit imports only.

## Common Patterns

**Utility Classes:**
- Static methods only with private constructor
- Example `LuhnAlgorithm.java` (lines 26-30):
```java
public class LuhnAlgorithm {
    private LuhnAlgorithm() {
    }
    public static boolean checkCC(String cardNumber) {
        // implementation
    }
}
```

**Domain Objects (Info Classes):**
- Immutable construction with mutable setters
- Serializable with `serialVersionUID`
- Implement `toString()` returning meaningful value
- Example `CustomerInfo.java` (lines 30-57):
```java
public class CustomerInfo implements Serializable {
    private static final long serialVersionUID = 9083257536541L;
    
    protected String id;
    protected String searchkey;
    // ... other fields
    
    public CustomerInfo(String id) {
        this.id = id;
        this.searchkey = null;
        // ... initialize other fields to null
    }
}
```

**Builder/Constructor Overloading:**
- Multiple constructors for flexibility
- Example `PaymentInfoCash` (inferred from test): 2-arg, 3-arg, 4-arg constructors
- Parent class constructor delegation: `super(s);`

## Error Handling

**Exception Hierarchy:**
- Base exception: `BasicException` extends `java.lang.Exception` located at `com.unicenta.basic.BasicException`
- Custom exceptions for domain-specific errors
- Constructors support: message-only, message+cause, cause-only

**Exception Patterns:**
- Checked exceptions (BasicException) thrown and declared in method signatures
- Example from `com.unicenta.data.loader.PreparedSentence`:
```java
catch (SQLException eSQL) {
    throw new BasicException(eSQL);
}
```

**Try-Catch-Finally:**
- Resources properly closed in method implementations
- Catch clauses typically log and either wrap in BasicException or handle gracefully
- Observable from presence of `log.error(ex.getMessage())` pattern throughout codebase

## Logging

**Framework:** Logback (SLF4J facade)
- Dependency: `ch.qos.logback:logback-classic:1.2.2`
- Logger injection via **Lombok's `@Slf4j` annotation** - auto-generates `log` field

**Example from `PreparedSentence.java` (lines 23, 34):**
```java
@Slf4j
public class PreparedSentence extends JDBCSentence {
    // log field is auto-generated by Lombok
}
```

**Log Levels Used:**
- `log.error()` - Exceptions and error conditions
- `log.info()` - Informational messages (db operations, table existence checks, timing)
- `log.warn()` - Not commonly observed in sampled code
- `log.debug()` - Not commonly observed in sampled code

**Logging Pattern:**
- Error exceptions: `log.error(ex.getMessage())`
- Info messages with context: `log.info("Fetch Orders from DB")`, `log.info("Time of getCustomersWithOutImage {}", (System.currentTimeMillis() - time))`
- No format string placeholders in error logs (just `ex.getMessage()`)

**Configuration:**
- No `logback.xml` in repo - uses default/built-in configuration
- Logback auto-configures when on classpath with SLF4J

## Documentation

**Javadoc:**
- Used extensively for public methods and classes
- Format: `/** ... */` before class/method declaration
- Includes `@author`, `@param`, `@return` tags
- Example from `LuhnAlgorithm.java` (lines 32-36):
```java
/**
 *
 * @param cardNumber
 * @return
 */
public static boolean checkCC(String cardNumber) {
```

**File Headers:**
- GPL v3 license header on all files
- Copyright: `uniCenta & previous Openbravo POS works` or similar
- Author attribution: `@author jack gerrard`, `@author adrianromero`, `@author Mikel Irurita`, `@author JG uniCenta`
- Links to project: `https://unicenta.com`

**Inline Comments:**
- Minimal inline comments observed
- Comments explain WHY, not WHAT
- Example from `RoundUtilsTest.java` (line 29):
```java
// Math.rint uses "round half to even" (banker's rounding)
// 1.005 * 100 = 100.5 → rint → 100 (even) → 1.00
double result = RoundUtils.round(1.005);
```

**Class-Level Documentation:**
- Date annotations for changes: `// JG 20 Sep 12 Extended for Postal`
- No extensive class-level comments, relying on self-documenting code

## Function Design

**Size:**
- Utility functions typically 10-40 lines
- Domain methods (getters/setters) 1-3 lines
- Business logic methods 30-80 lines
- Example: `Hashcypher.changePassword(Component, String)` is ~35 lines

**Parameters:**
- Methods prefer 0-3 parameters
- Longer parameter lists delegate to constructors or builder pattern
- Example: PaymentInfoCash overloaded with 2, 3, and 4 parameter versions

**Return Values:**
- Explicit null returns common for optional values: `return null;`
- Boolean for validation/checks: `public boolean hasPrePay()`
- Wrapped exceptions converted to custom exception type: `throw new BasicException(eSQL);`
- Void for void operations (very rare in sampled code)

## Module Design

**Exports:**
- All public classes are export points
- No package-private exports observed
- Internal utility classes use private constructors: `private StringUtils() {}`

**Barrel Files:**
- No barrel files (index.java or similar) observed
- Direct imports from specific classes preferred: `import com.unicenta.pos.util.StringUtils;`

**Packaging:**
- Functional decomposition: features grouped by domain (customers, payment, ticket, util)
- Data/persistence layer separate: `com.unicenta.data.loader`
- UI components grouped: `com.unicenta.beans`, `com.unicenta.pos.forms`

---

*Convention analysis: 2026-04-03*
