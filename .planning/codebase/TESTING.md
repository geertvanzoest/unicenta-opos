# Testing Patterns

**Analysis Date:** 2026-04-03

## Test Framework

**Runner:**
- JUnit 4 (version 4.12)
- Config: pom.xml maven-surefire-plugin (default integration, no custom config)

**Assertion Library:**
- JUnit 4 Assert class: `org.junit.Assert` with static imports
- No additional assertion libraries (AssertJ, Hamcrest) detected
- Standard assertions: `assertEquals()`, `assertTrue()`, `assertFalse()`, `assertNull()`, `assertNotNull()`, `assertNotSame()`

**Mocking Framework:**
- Mockito: `org.mockito:mockito-inline:4.2.0` and `org.mockito:mockito-junit-jupiter:4.2.0`
- Used for mocking Session objects and external dependencies
- Not widely used in current test suite (23 test files)

**Coverage Tool:**
- JaCoCo Maven plugin version 0.8.12
- Automatically generates coverage reports during `test` phase
- Report location: `target/site/jacoco/` (standard JaCoCo output)

**Run Commands:**
```bash
mvn clean test                      # Run all tests
mvn clean test -Dtest=<TestClass>   # Run specific test class
mvn clean package                   # Package & run tests
mvn jacoco:report                   # Generate coverage report
```

## Test File Organization

**Location:** `src/test/java/com/unicenta/`
- Mirrors `src/main/java/com/unicenta/` directory structure exactly
- Co-located by package namespace, not by feature

**Naming Convention:**
- Test class suffix: `Test` (not `Tests` or `TestCase`)
- Examples: `LuhnAlgorithmTest.java`, `CustomerInfoTest.java`, `PaymentInfoCashTest.java`
- File names match source class names: source `LuhnAlgorithm.java` → test `LuhnAlgorithmTest.java`

**Directory Structure:**
```
src/test/java/
├── com/unicenta/
│   ├── pos/
│   │   ├── customers/
│   │   │   └── CustomerInfoTest.java
│   │   ├── payment/
│   │   │   ├── PaymentInfoCashTest.java
│   │   │   ├── PaymentInfoFreeTest.java
│   │   │   └── VoucherPaymentInfoTest.java
│   │   ├── ticket/
│   │   │   ├── TicketInfoTest.java
│   │   │   ├── TicketLineInfoTest.java
│   │   │   ├── TicketTaxInfoTest.java
│   │   │   ├── CategoryInfoTest.java
│   │   │   ├── UserInfoTest.java
│   │   │   └── TaxInfoTest.java
│   │   ├── util/
│   │   │   ├── LuhnAlgorithmTest.java
│   │   │   ├── Base64EncoderTest.java
│   │   │   ├── AltEncrypterTest.java
│   │   │   ├── HashcypherTest.java
│   │   │   ├── RoundUtilsTest.java
│   │   │   ├── StringUtilsTest.java
│   │   │   └── StringParserTest.java
│   ├── data/
│   │   └── loader/
│   │       └── StaticSentenceTest.java
│   ├── PackageScanTest.java
│   └── format/
│       └── DoubleUtilsTest.java
```

## Test Structure

**Suite Organization:**
Tests follow a strict comment-structured organization pattern. Example from `CustomerInfoTest.java`:

```java
public class CustomerInfoTest {

    // --- constructor sets id, others null ---
    
    @Test
    public void constructorSetsId() {
        CustomerInfo c = new CustomerInfo("cust-001");
        assertEquals("cust-001", c.getId());
    }
    
    // --- setName / getName ---
    
    @Test
    public void setNameThenGetName() {
        CustomerInfo c = new CustomerInfo("cust-001");
        c.setName("Maria van Dam");
        assertEquals("Maria van Dam", c.getName());
    }
    
    // --- printName encodes XML ---
    
    @Test
    public void printNameEncodesAmpersand() {
        // ...
    }
}
```

**Patterns:**

1. **Minimal Setup/Teardown:**
   - Most tests use `@Before` (implicit) or create fresh instances in each test
   - Example from `RoundUtilsTest.java`:
   ```java
   @Before
   public void setTwoDecimalCurrency() {
       // Force 2-decimal currency regardless of the test runner's locale
       Formats.setCurrencyPattern("0.00");
   }
   ```
   
2. **No Test Inheritance:**
   - Each test class is standalone
   - No base test classes or mixins observed
   
3. **Arrange-Act-Assert Pattern:**
   - Implicit AAA: object creation → method call → assertion
   - Example from `PaymentInfoCashTest.java` (lines 10-14):
   ```java
   @Test
   public void basicConstructor_setsTotal() {
       PaymentInfoCash p = new PaymentInfoCash(10.00, 15.00);  // Arrange
       assertEquals(10.00, p.getTotal(), 0.001);              // Act & Assert
   }
   ```
   
4. **Comment Sections:**
   - Tests grouped with comment headers describing what's being tested
   - Headers use format: `// --- [behavior description] ---`
   - Improves readability and creates logical test organization within a single class

## Mocking

**Framework:** Mockito
- Import: `import org.mockito.Mockito;`
- Used selectively, not extensively

**Patterns:**
Example from `StaticSentenceTest.java`:
```java
@Before
public void setup() throws Exception {
    session = Mockito.mock(Session.class);
}

@Test
public void shouldConvertUpdateToSQLite() throws Exception {
    Mockito.when(session.getURL()).thenReturn("jdbc:sqlite://home/temp/.unicenta/unicentaopos");
    StaticSentence staticSentence = new StaticSentence(session, "");
    String fixSqliteDate = staticSentence.fixSqliteDate(UPDATE_SQL);
    assert !fixSqliteDate.contains("{");
    assert fixSqliteDate.contains("UPDATE");
}
```

**What to Mock:**
- External service dependencies (database connections, Sessions)
- Objects with side effects
- Objects that are expensive to construct

**What NOT to Mock:**
- Value objects (`CustomerInfo`, `PaymentInfoCash`, domain entities)
- Utility functions (functions with no side effects)
- String, Integer, Boolean and other standard types
- Objects under test (except for injected dependencies)

**Mocking Approach:**
- `@Before` setup method to create mocks
- `Mockito.when().thenReturn()` for stubbing
- No `@InjectMocks` or annotation-based injection observed
- Manual constructor injection preferred

## Fixtures and Factories

**Test Data:**
- Inline test data creation in each test method
- No shared fixture classes observed
- Example from `CustomerInfoTest.java` (line 12):
```java
CustomerInfo c = new CustomerInfo("cust-001");
c.setName("Maria van Dam");
```

**Factories/Builders:**
- No test fixture factories observed in current test suite
- Direct constructors used throughout
- Multiple constructor variants leverage code paths (2-arg, 3-arg, 4-arg in PaymentInfoCash)

**Data Patterns:**
- Small focused datasets for each test case
- Card numbers for payment tests: `"4111111111111111"` (Visa test card)
- Locale-independent currency: `Formats.setCurrencyPattern("0.00")`

## Coverage

**Requirements:** Not enforced
- No coverage thresholds configured in `pom.xml`
- JaCoCo plugin present but purely for reporting

**View Coverage:**
```bash
mvn clean test
open target/site/jacoco/index.html  # macOS
firefox target/site/jacoco/index.html # Linux/Windows
```

**Current State:**
- 23 test files with ~600+ test methods
- Focus on unit testing domain objects and utilities
- No integration or E2E tests observed
- Test files primarily in: `com.unicenta.pos.util`, `com.unicenta.pos.payment`, `com.unicenta.pos.ticket`

**Gaps Identified:**
- No tests for UI components (`com.unicenta.beans`, `com.unicenta.pos.forms`)
- No tests for persistence layer (`com.unicenta.data.loader` has one test)
- No tests for service orchestration (`com.unicenta.pos.forms.DataLogicSales`, etc.)

## Test Types

**Unit Tests:**
- **Scope:** Individual method/function testing
- **Approach:** Test single responsibility, typically 3-5 assertions per test
- **Duration:** < 100ms per test
- **Example:** `LuhnAlgorithmTest` tests `checkCC()` with various input types (valid cards, invalid cards, edge cases, null, empty, non-numeric)

**Integration Tests:**
- **Scope:** Database integration
- **Status:** Minimal - only `StaticSentenceTest.java` tests SQL statement conversion
- **Approach:** Mocked Session, testing actual statement transformation logic
- **Not observed:** Component integration, service layer tests

**E2E Tests:**
- **Status:** Not used
- **Reason:** JavaFX UI-heavy application; E2E would require UI automation framework

## Common Patterns

**Descriptive Test Names:**
- Method naming: `<action>_<condition>_<expected_result>` OR `<method><specific_case>`
- Examples:
  - `testValidVisaCard()` 
  - `basicConstructor_setsTotal()`
  - `round_exactHalfCent_roundsToNearest()`
  - `setNameThenGetName()`
  - `printNameEncodesAmpersand()`

**Assertions with Epsilon:**
- Floating-point comparisons use epsilon (tolerance)
- Pattern: `assertEquals(expected, actual, epsilon)`
- Example from `PaymentInfoCashTest.java` (line 13):
```java
assertEquals(10.00, p.getTotal(), 0.001);
```
- Pattern from `RoundUtilsTest.java` (line 38):
```java
assertEquals(1.24, RoundUtils.round(1.235), 1e-10);
```

**Null/Empty Testing:**
- Explicit tests for null inputs
- Example from `LuhnAlgorithmTest.java` (lines 93-101):
```java
@Test
public void nullInputReturnsFalse() {
    assertFalse(LuhnAlgorithm.checkCC(null));
}

@Test
public void emptyStringReturnsFalse() {
    assertFalse(LuhnAlgorithm.checkCC(""));
}
```

**Async Testing:**
- Not observed in current test suite
- Application uses Swing/JavaFX but tests don't exercise async patterns
- `OrderPop.java` uses `setOnFailed(t -> log.error(...))` but no corresponding tests

**Error Testing:**
- Exception testing pattern not commonly observed
- IllegalArgumentException thrown in `StringUtils.hex2byte()` but no test for it
- Could benefit from `@Test(expected = IllegalArgumentException.class)` tests

## Test Statistics

- **Total Test Files:** 23
- **Most Tested Package:** `com.unicenta.pos.util` (7 test files)
- **Most Tested Package:** `com.unicenta.pos.ticket` (6 test files)  
- **Most Tested Package:** `com.unicenta.pos.payment` (3 test files)
- **Least Tested:** `com.unicenta.pos.forms` (0 tests), `com.unicenta.pos.customers` (1 test)

**Test Distribution (by type):**
- Domain object tests (POJO validation): ~60%
- Utility function tests: ~30%
- Integration tests: ~10%

---

*Testing analysis: 2026-04-03*
