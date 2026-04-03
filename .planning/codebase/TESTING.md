# Testing Patterns

**Analysis Date:** 2026-04-03

## Test Framework

**Runner:**

- JUnit 4 (version 4.12)
- Config: Maven Surefire plugin (default integration, no custom `maven-surefire-plugin` config in `pom.xml`)
- Note: `mockito-junit-jupiter` (JUnit 5 bridge) is declared in `pom.xml` but unused -- all tests use JUnit 4

**Assertion Library:**

- JUnit 4 Assert class: `org.junit.Assert` with static imports
- No additional assertion libraries (AssertJ, Hamcrest)
- Standard assertions used: `assertEquals()`, `assertTrue()`, `assertFalse()`, `assertNull()`, `assertNotNull()`, `assertNotSame()`, `assertSame()`, `assertArrayEquals()`

**Mocking Framework:**

- Mockito Inline: `org.mockito:mockito-inline:4.2.0`
- Used selectively -- only 1 test file (`StaticSentenceTest.java`) uses Mockito
- Note: `mockito-inline` is not scoped as `test` in `pom.xml` (should be, but is legacy)

**Coverage Tool:**

- JaCoCo Maven plugin version 0.8.12
- Configured in `pom.xml` lines 543-558: `prepare-agent` + `report` goals
- Report auto-generates during `mvn test` phase
- Output: `target/site/jacoco/jacoco.xml` (for CI) and `target/site/jacoco/index.html` (for humans)

**Run Commands:**

```bash
mvn test                          # Run all tests
mvn test -Dtest=ClassName         # Run specific test class
mvn test -Dtest=ClassName#method  # Run specific test method
mvn clean verify                  # Full build + test (CI uses this)
open target/site/jacoco/index.html # View coverage report (macOS)
```

## Test File Organization

**Location:** `src/test/java/com/unicenta/` -- mirrors `src/main/java/com/unicenta/` exactly

**Naming Convention:**

- Test class suffix: `Test` (not `Tests` or `TestCase`)
- File names match source class: `LuhnAlgorithm.java` -> `LuhnAlgorithmTest.java`
- Exception: `TicketInfoExtendedTest.java` tests additional `TicketInfo` behavior beyond `TicketInfoTest.java`

**Directory Structure:**

```text
src/test/java/com/unicenta/
├── PackageScanTest.java
├── data/
│   └── loader/
│       └── StaticSentenceTest.java
├── format/
│   ├── DoubleUtilsTest.java
│   └── FormatsTest.java
│   └── FormatsValidateTest.java
├── pos/
│   ├── customers/
│   │   └── CustomerInfoTest.java
│   ├── payment/
│   │   ├── PaymentInfoCashTest.java
│   │   ├── PaymentInfoFreeTest.java
│   │   └── VoucherPaymentInfoTest.java
│   ├── ticket/
│   │   ├── CategoryInfoTest.java
│   │   ├── TaxInfoTest.java
│   │   ├── TicketInfoTest.java
│   │   ├── TicketInfoExtendedTest.java
│   │   ├── TicketLineInfoTest.java
│   │   ├── TicketTaxInfoTest.java
│   │   └── UserInfoTest.java
│   └── util/
│       ├── AltEncrypterTest.java
│       ├── Base64EncoderTest.java
│       ├── HashcypherTest.java
│       ├── LuhnAlgorithmTest.java
│       ├── RoundUtilsTest.java
│       ├── StringParserTest.java
│       └── StringUtilsTest.java
```

## Test Structure

### Suite Organization

Tests use comment-delimited sections to group related tests within a class. Three section styles are used interchangeably:

**Style 1 -- Dashes (most common):**

```java
// --- constructor sets id, others null ---

@Test
public void constructorSetsId() { ... }

// --- setName / getName ---

@Test
public void setNameThenGetName() { ... }
```

**Style 2 -- Equals signs (utility tests):**

```java
// =========================================================================
// encodeXML
// =========================================================================

@Test
public void encodeXML_ampersand_isEncoded() { ... }
```

**Style 3 -- Hyphens (security/crypto tests):**

```java
// -----------------------------------------------------------------------
// hashString(String)
// -----------------------------------------------------------------------

@Test
public void hashStringNullReturnsEmptyPrefix() { ... }
```

Use **Style 1** for new tests.

### Test Method Naming

Two naming styles coexist. Use the **underscore style** for new tests:

**Underscore style** (preferred for new tests):

- `<method>_<condition>_<expectedResult>`
- Examples: `encodeXML_ampersand_isEncoded()`, `round_zero_returnsZero()`, `constructor_setsTotal()`

**CamelCase style** (legacy, acceptable):

- `<action><SpecificCase>`
- Examples: `constructorSetsId()`, `setNameThenGetName()`, `hashStringKnownValue()`

### Arrange-Act-Assert Pattern

All tests follow implicit AAA. Keep it tight -- most tests are 2-3 lines:

```java
@Test
public void basicConstructor_setsTotal() {
    PaymentInfoCash p = new PaymentInfoCash(10.00, 15.00);  // Arrange + Act
    assertEquals(10.00, p.getTotal(), 0.001);                // Assert
}
```

### Setup and Teardown

**`@Before` -- used for locale-dependent setup:**

```java
// src/test/java/com/unicenta/pos/util/RoundUtilsTest.java
@Before
public void setTwoDecimalCurrency() {
    Formats.setCurrencyPattern("0.00");
}
```

**`@After` -- used to reset shared static state:**

```java
// src/test/java/com/unicenta/format/FormatsTest.java
@After
public void resetPatterns() {
    Formats.setIntegerPattern(null);
    Formats.setCurrencyPattern(null);
}
```

**Important:** Always reset static state in `@After` when tests modify `Formats.*Pattern()` or other static configuration. Failing to do so causes order-dependent test failures.

### No Test Inheritance

Each test class is standalone. No base test classes or shared test mixins.

## Mocking

**Framework:** Mockito 4.2.0 (inline variant for mocking final classes)

**Only usage** -- `src/test/java/com/unicenta/data/loader/StaticSentenceTest.java`:

```java
Session session;

@Before
public void setup() throws Exception {
    session = Mockito.mock(Session.class);
}

@Test
public void shouldConvertUpdateToSQLite() throws Exception {
    Mockito.when(session.getURL())
        .thenReturn("jdbc:sqlite://home/temp/.unicenta/unicentaopos");
    StaticSentence staticSentence = new StaticSentence(session, "");
    String fixSqliteDate = staticSentence.fixSqliteDate(UPDATE_SQL);
    assert !fixSqliteDate.contains("{");
    assert fixSqliteDate.contains("UPDATE");
}
```

Note: This test uses bare `assert` (Java assert keyword) instead of JUnit's `assertFalse()`/`assertTrue()`. Do not replicate this -- use JUnit assertions for new tests.

**What to mock:**

- Database connections/sessions (`Session`, `Connection`)
- External service clients
- Objects with I/O side effects

**What NOT to mock:**

- Value objects (`CustomerInfo`, `PaymentInfoCash`, `TaxInfo`, etc.)
- Pure utility functions (`StringUtils`, `RoundUtils`)
- The class under test itself

**Mockito style for new tests:**

```java
import static org.mockito.Mockito.*;

@Before
public void setup() {
    dependency = mock(DependencyClass.class);
    when(dependency.method()).thenReturn(value);
    sut = new ClassUnderTest(dependency);
}
```

## Fixtures and Factories

### Helper Methods

Several test classes use private helper methods to create test objects:

**Factory method pattern (preferred for new tests):**

```java
// src/test/java/com/unicenta/pos/ticket/TicketLineInfoTest.java
private TaxInfo tax21() {
    return new TaxInfo("t1", "BTW 21%", "cat1", "custcat1", null, RATE, false, 1);
}

private TicketLineInfo line(double multiply, double price) {
    return new TicketLineInfo("prod1", multiply, price, tax21());
}
```

**Static factory method pattern:**

```java
// src/test/java/com/unicenta/pos/ticket/TicketInfoExtendedTest.java
private static final TaxInfo BTW = new TaxInfo("1", "BTW", "001", null, null, 0.21, false, 1);

private static TicketLineInfo line(String id, String name, double price) {
    return new TicketLineInfo(id, name, "001", "", 1.0, price, BTW);
}
```

**`createDefault()` pattern:**

```java
// src/test/java/com/unicenta/pos/ticket/TaxInfoTest.java
private TaxInfo createDefault() {
    return new TaxInfo("id1", "BTW 21%", "cat1", "custcat1", "parent1", 0.21, false, 1);
}
```

### Test Data Values

- Dutch context for domain data: `"BTW 21%"`, `"Dranken"`, `"Jan Jansen"`, `"Maria van Dam"`, `"4811 AA"` (postcode), `"076-1234567"`
- Standard test card numbers: `"4111111111111111"` (Visa), `"5500000000000004"` (Mastercard)
- Tax rates: `0.21` (Dutch BTW), `0.09` (reduced), `0.0` (exempt)
- No shared test fixture files or JSON/XML test data

### Constants for Precision

```java
// Common delta constants for floating-point assertions
private static final double DELTA = 1e-9;    // tight precision (TicketLineInfoTest)
private static final double DELTA = 1e-10;   // very tight (DoubleUtilsTest)
// Or inline: assertEquals(10.00, p.getTotal(), 0.001);  // looser (PaymentInfoCashTest)
```

Use `1e-9` as the default delta for new tests. Use `0.001` only when testing formatted currency values.

## Coverage

**Requirements:** No thresholds enforced -- JaCoCo is reporting-only.

**CI Integration:** Codecov uploads on every push/PR via GitHub Actions:

```yaml
# .github/workflows/ci.yml lines 29-34
- name: Upload coverage to Codecov
  if: always()
  uses: codecov/codecov-action@v5
  with:
    token: ${{ secrets.CODECOV_TOKEN }}
    files: target/site/jacoco/jacoco.xml
    fail_ci_if_error: false
```

**View Coverage:**

```bash
mvn clean test
open target/site/jacoco/index.html
```

### Current Coverage State

- **Total test files:** 23
- **Total source files:** 617
- **Estimated test methods:** ~350+
- Focus areas: domain objects (`pos/ticket/`, `pos/payment/`), utilities (`pos/util/`), formatting (`format/`)

### Coverage Distribution by Package

| Package | Test Files | Coverage Level |
|---------|-----------|----------------|
| `com.unicenta.pos.util` | 7 | Good |
| `com.unicenta.pos.ticket` | 7 (incl. extended) | Good |
| `com.unicenta.pos.payment` | 3 | Good |
| `com.unicenta.format` | 3 | Good |
| `com.unicenta.pos.customers` | 1 | Minimal |
| `com.unicenta.data.loader` | 1 | Minimal |
| `com.unicenta` (root) | 1 | Minimal |
| `com.unicenta.pos.forms` | 0 | None |
| `com.unicenta.pos.sales` | 0 | None |
| `com.unicenta.pos.inventory` | 0 | None |
| `com.unicenta.pos.config` | 0 | None |
| `com.unicenta.beans` | 0 | None |
| `com.unicenta.data.gui` | 0 | None |
| `com.unicenta.pos.printer` | 0 | None |

## Test Types

### Unit Tests (dominant -- ~95%)

- **Scope:** Individual method/function behavior
- **Approach:** One assertion per test, fresh object per test
- **Duration:** < 100ms per test
- **Example pattern:** Test each constructor, each getter, each edge case separately

```java
// src/test/java/com/unicenta/pos/ticket/TaxInfoTest.java
@Test
public void constructorSetsRate() {
    assertEquals(0.21, createDefault().getRate(), 1e-9);
}

@Test
public void setRateUpdates() {
    TaxInfo tax = createDefault();
    tax.setRate(0.09);
    assertEquals(0.09, tax.getRate(), 1e-9);
}
```

### Integration Tests (minimal -- ~5%)

- **Only example:** `src/test/java/com/unicenta/data/loader/StaticSentenceTest.java`
- Tests SQL statement transformation logic with mocked `Session`
- Not true integration (no database connection)

### E2E Tests

- Not present. Application is JavaFX/Swing UI-heavy; would require TestFX or similar framework.

## Common Patterns

### Floating-Point Assertions

Always use epsilon for `double` comparisons:

```java
assertEquals(expected, actual, delta);

// Standard deltas:
assertEquals(10.00, p.getTotal(), 0.001);    // currency values
assertEquals(0.21, tax.getRate(), 1e-9);      // precise calculations
assertEquals(1.24, RoundUtils.round(1.235), 1e-10);  // rounding tests
```

### Null and Edge Case Testing

Every testable class includes explicit null/empty/edge case tests:

```java
// src/test/java/com/unicenta/pos/util/LuhnAlgorithmTest.java
@Test
public void nullInputReturnsFalse() {
    assertFalse(LuhnAlgorithm.checkCC(null));
}

@Test
public void emptyStringReturnsFalse() {
    assertFalse(LuhnAlgorithm.checkCC(""));
}

@Test
public void nonNumericLettersReturnsFalse() {
    assertFalse(LuhnAlgorithm.checkCC("abcdefghijk"));
}
```

### Expected Exception Testing

Use `@Test(expected = ...)` annotation:

```java
// src/test/java/com/unicenta/pos/util/StringUtilsTest.java
@Test(expected = IllegalArgumentException.class)
public void hex2byte_oddLengthString_throwsIllegalArgumentException() {
    StringUtils.hex2byte("ABC");
}

// src/test/java/com/unicenta/pos/payment/VoucherPaymentInfoTest.java
@Test(expected = UnsupportedOperationException.class)
public void getTendered_throwsUnsupportedOperationException() {
    VoucherPaymentInfo p = new VoucherPaymentInfo(15.00, "voucher", "V-001");
    p.getTendered();
}
```

### Roundtrip Testing

For encode/decode pairs, test roundtrip integrity:

```java
// src/test/java/com/unicenta/pos/util/Base64EncoderTest.java
@Test
public void roundtripAsciiString() {
    byte[] original = "Hello, World!".getBytes(StandardCharsets.UTF_8);
    String encoded = Base64Encoder.encode(original);
    byte[] decoded = Base64Encoder.decode(encoded);
    assertArrayEquals(original, decoded);
}
```

### Copy/Clone Independence Testing

For `copy*()` methods, verify the copy is independent:

```java
// src/test/java/com/unicenta/pos/ticket/TicketLineInfoTest.java
@Test
public void copyTicketLineIsIndependent() {
    TicketLineInfo original = line(2.0, 10.0);
    TicketLineInfo copy = original.copyTicketLine();
    copy.setPrice(99.0);
    assertEquals(10.0, original.getPrice(), DELTA);  // original unchanged
}
```

### Constraint/Validation Chain Testing

For classes that apply validation chains (like `FormatsValidate`):

```java
// src/test/java/com/unicenta/format/FormatsValidateTest.java
@Test
public void multipleConstraintsChainInOrder() throws BasicException {
    FormatsConstrain addTen = new FormatsConstrain() {
        @Override
        public Object check(Object value) throws ParseException {
            return (Integer) value + 10;
        }
    };
    FormatsConstrain timesThree = new FormatsConstrain() {
        @Override
        public Object check(Object value) throws ParseException {
            return (Integer) value * 3;
        }
    };

    FormatsValidate fv = new FormatsValidate(Formats.INT,
        new FormatsConstrain[]{addTen, timesThree});
    String formatted = Formats.INT.formatValue(5);
    Object result = fv.parseValue(formatted);
    Assert.assertEquals(45, result);  // 5 -> +10=15 -> *3=45
}
```

## CI/CD Integration

### GitHub Actions Workflows

Three workflows in `.github/workflows/`:

**`ci.yml` -- Build + Test + Coverage:**

- Triggers: push to `main`, PRs to `main`
- Java 11 (Temurin) with Maven cache
- Runs `mvn -B clean verify`
- Uploads JaCoCo XML to Codecov
- Uploads JAR artifact on `main` pushes (30-day retention)

**`semgrep.yml` -- SAST Security Scanning:**

- Triggers: push to `main`, PRs to `main`
- Runs Semgrep CI in container

**`claude.yml` -- AI Code Review:**

- Triggers: issue/PR comments containing `@claude`
- Runs Claude Code Action for automated review

### CodeRabbit

Automatic code review on all PRs. After addressing CodeRabbit feedback, resolve the PR review threads to unblock merge.

## Test Gaps (Priority Areas)

### High Priority -- Business Logic

- `src/main/java/com/unicenta/pos/forms/DataLogicSales.java` (2827 lines, 0 tests) -- core sales data operations
- `src/main/java/com/unicenta/pos/panels/PaymentsModel.java` (1105 lines, 0 tests) -- payment calculations
- `src/main/java/com/unicenta/pos/forms/DataLogicSystem.java` (891 lines, 0 tests) -- system data operations

### Medium Priority -- Data Layer

- `src/main/java/com/unicenta/data/loader/PreparedSentence.java` -- SQL execution
- `src/main/java/com/unicenta/data/loader/Session.java` -- database connection management
- `src/main/java/com/unicenta/data/user/BrowsableEditableData.java` -- CRUD operations

### Low Priority -- UI Components

- `src/main/java/com/unicenta/pos/sales/JPanelTicket.java` (3515 lines) -- main POS UI
- `src/main/java/com/unicenta/beans/` -- custom Swing components
- UI testing would require TestFX or similar, significant setup investment

## Writing New Tests

### Checklist for Adding a Test Class

1. Create file at `src/test/java/com/unicenta/` mirroring the source path
2. Name it `<SourceClass>Test.java`
3. Use JUnit 4 imports (`org.junit.Test`, `org.junit.Assert`)
4. Use static import for assertions: `import static org.junit.Assert.*;`
5. No license header on test files
6. Group tests with `// --- section name ---` comments
7. Add `@After` cleanup if modifying static state (`Formats` patterns, etc.)
8. Use factory helper methods for repeated object creation
9. Test null inputs, empty strings, edge cases explicitly
10. Use `assertEquals(expected, actual, 1e-9)` for all `double` comparisons

### Template for New Test Class

```java
package com.unicenta.pos.example;

import org.junit.Test;
import static org.junit.Assert.*;

public class ExampleClassTest {

    // --- constructor ---

    @Test
    public void constructor_setsField() {
        ExampleClass obj = new ExampleClass("value");
        assertEquals("value", obj.getField());
    }

    // --- edge cases ---

    @Test
    public void constructor_nullField_isAllowed() {
        ExampleClass obj = new ExampleClass(null);
        assertNull(obj.getField());
    }

    // --- business logic ---

    @Test
    public void calculate_standardInput_returnsExpected() {
        ExampleClass obj = new ExampleClass("input");
        assertEquals(42.0, obj.calculate(), 1e-9);
    }
}
```

---

*Testing analysis: 2026-04-03*
