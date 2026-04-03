# Testing Patterns

**Analysis Date:** 2026-04-03

## Test Framework

**Runner:**
- JUnit 4.12
- Maven Surefire (default test runner)
- Config: `pom.xml` specifies `<scope>test</scope>` for test dependencies

**Assertion Library:**
- JUnit assertions: `Assert.assertEquals()`, `Assert.assertTrue()`, `Assert.assertFalse()`
- Simple `assert` statements in some tests: `assert !fixSqliteDate.contains("{")`

**Mock Framework:**
- Mockito 4.2.0
- `mockito-inline` for mocking final/static classes
- `mockito-junit-jupiter` for integration with JUnit (though JUnit 4 is primary)

**Run Commands:**
```bash
mvn clean package              # Full build + tests
mvn test                       # Run tests only
mvn test -Dtest=ClassName     # Run specific test class
```

**No explicit watch mode** in pom.xml configuration.

## Test File Organization

**Location:**
- `src/test/java/` - Test source root
- Co-located with main packages: `src/test/java/com/unicenta/pos/ticket/TicketInfoTest.java`
- Mirrors production structure under `src/test/java/`

**Naming:**
- Suffix convention: `*Test.java`
- Examples: `TicketInfoTest.java`, `PackageScanTest.java`, `StaticSentenceTest.java`

**Current Coverage:**
- Only 3 test classes found in entire codebase (133K+ lines of production code)
- Test classes:
  - `com.unicenta.pos.ticket.TicketInfoTest`
  - `com.unicenta.PackageScanTest`
  - `com.unicenta.data.loader.StaticSentenceTest`

**Structure:**
```
src/test/
└── java/
    └── com/unicenta/
        ├── PackageScanTest.java
        └── pos/
            ├── ticket/
            │   └── TicketInfoTest.java
            └── data/loader/
                └── StaticSentenceTest.java
```

## Test Structure

**Suite Organization:**

From `TicketInfoTest.java`:
```java
public class TicketInfoTest {

    @Test
    public void shouldGroupFourOfTheSameProduct() {
        // Test implementation
    }

    @Test
    public void shouldGroupTwoOfTheSameProduct() {
        // Test implementation
    }

    @Test
    public void shouldGroupDiscountTwoDifferentProducts() {
        // Test implementation
    }

    private TicketInfo addTwoOfTheSameProduct(TicketInfo ticket) {
        // Helper method
    }
}
```

**Patterns:**

1. **No setup/teardown:**
   - No `@Before` or `@After` lifecycle hooks used (except in `StaticSentenceTest`)
   - Test isolation through local object creation
   - Each test method creates fresh test fixtures

2. **Setup pattern (from StaticSentenceTest):**
```java
@Before
public void setup() throws Exception {
    session = Mockito.mock(Session.class);
}
```

3. **Inline assertions:**
```java
@Test
public void shouldGroupTwoOfTheSameProduct() {
    TicketInfo ticket = addTwoOfTheSameProduct(null);
    Assert.assertEquals(2, ticket.getLinesCount());  // Arrange, Act, Assert in sequence
}
```

4. **Helper methods for setup:**
```java
private TicketInfo addTwoOfTheSameProduct(TicketInfo ticket) {
    if (ticket == null) {
        ticket = new TicketInfo();
    }
    // ... setup logic
    return ticket;
}
```

## Mocking

**Framework:** Mockito 4.2.0

**Pattern from `StaticSentenceTest.java`:**
```java
Session session;

@Before
public void setup() throws Exception {
    session = Mockito.mock(Session.class);
}

@Test
public void shouldConvertUpdateToSQLite() throws Exception {
    Mockito.when(session.getURL()).thenReturn("jdbc:sqlite://home/temp/.unicenta/unicentaopos");
    StaticSentence staticSentence = new StaticSentence(session, "");
    String fixSqliteDate = staticSentence.fixSqliteDate(UPDATE_SQL);
    // ...
}
```

**What to Mock:**
- External dependencies: Database connections (`Session`), file I/O
- Services that are slow or have side effects
- Use `Mockito.mock()` for concrete types
- Use `Mockito.when().thenReturn()` for stubbing return values

**What NOT to Mock:**
- Domain model objects (create real instances): `new TicketInfo()`, `new TicketLineInfo()`
- Utility functions
- Value objects that are cheap to construct
- Test should verify business logic, not mock abstractions of domain objects

**Inline Mocking Usage:**
```java
Mockito.when(session.getURL()).thenReturn("jdbc:sqlite://...");
```

## Fixtures and Factories

**Test Data:**

From `TicketInfoTest.java`:
```java
private TicketInfo twoDifferentProducts() {
    TicketInfo ticket = new TicketInfo();
    ArrayList<TicketLineInfo> ticketLines = new ArrayList<>();
    TaxInfo stdTax = new TaxInfo("1", "STD", "001", null, null, 10, false, 1);
    
    TicketLineInfo ticketLineInfo1 = new TicketLineInfo("1234", "test1", "001", "", 1, 10, stdTax);
    ticketLineInfo1.setProperty("GROUP", "BOGO");
    ticketLineInfo1.setProperty("COUNT", "2");
    ticketLineInfo1.setProperty("PRICE", "5");
    
    ticketLines.add(ticketLineInfo1);
    // ... more setup
    return ticket;
}
```

**Location:**
- Private helper methods within test class (no separate fixture/factory files)
- Direct object construction in test methods
- No TestBuilder or Builder pattern observed
- No @ParameterizedTest annotations

**Test Data Patterns:**
- Hardcoded SQL strings for validation: `final static String UPDATE_SQL = "UPDATE ..."`
- Magic numbers in assertions (e.g., expecting 3 or 6 lines)
- Property-based setup on domain objects post-construction

## Coverage

**Requirements:** No coverage enforcement detected
- No JaCoCo or Cobertura plugin in pom.xml
- No coverage reports configured
- Current coverage: **~2%** (3 test classes for 133K+ LOC)

**View Coverage:**
- Run `mvn clean test` to execute tests
- No coverage report generation enabled

## Test Types

**Unit Tests:**
- Scope: Individual domain object behavior
- Approach: Direct object instantiation, verification of state changes
- Example: `TicketInfoTest` verifies ticket line grouping logic
- No database mocking observed (when needed, `Session` is mocked)

**Integration Tests:**
- Scope: SQL conversion/compatibility
- Approach: Mock database connection details, test actual business logic
- Example: `StaticSentenceTest` tests SQL dialect conversion for SQLite
- Uses Mockito to stub database URL, then verifies string transformation

**E2E Tests:**
- Framework: Not used
- No end-to-end test suite present
- Manual testing likely used for UI workflows

## Common Patterns

**Async Testing:**
- No async/Future tests observed
- Project uses JavaFX with `Task` classes, but no async test patterns
- Tests run synchronously only

**Error Testing:**
- Methods declare `throws Exception` in signature
- Exceptions not explicitly tested for
- Tests focus on happy path only
- Example: `setUp() throws Exception` but no error case verification

**Assertions:**
```java
// Direct JUnit asserts
Assert.assertEquals(2, ticket.getLinesCount());

// Simple boolean assertions
assert !fixSqliteDate.contains("{");
assert fixSqliteDate.contains("UPDATE");

// No fluent assertions or custom matchers
```

**Test Naming:**
- Descriptive names starting with `should`: `shouldGroupTwoOfTheSameProduct()`
- Clear intent: `shouldConvertUpdateToSQLite()`, `shouldConvertJoinToSQLite()`

## CI/CD Integration

**GitHub Actions Configuration:**

From `.github/workflows/ci.yml`:
```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v6
      - name: Set up JDK 11
        uses: actions/setup-java@v5
        with:
          java-version: '11'
          distribution: 'temurin'
          cache: maven
      - name: Build with Maven
        run: mvn -B clean package --file pom.xml
      - name: Upload build artifact
        if: github.ref == 'refs/heads/main'
        uses: actions/upload-artifact@v7
```

**Test Execution:**
- Tests run as part of `mvn clean package` (Surefire bound to test phase)
- Build fails if tests fail
- No separate test-only job
- CI runs on every push to `main` and pull requests
- Artifacts uploaded on `main` branch only

## Test Guidelines for Future Tests

**When Adding New Tests:**

1. **Naming:** Use `*Test.java` suffix and `should*` method names
2. **Location:** Place in `src/test/java/` mirroring the package structure of classes being tested
3. **Structure:** Keep tests focused on single behavior; use private helper methods for setup
4. **Fixtures:** Create real domain objects, mock only external dependencies (DB, services)
5. **Assertions:** Use `Assert.assertEquals()` for clarity; keep assertions simple and direct
6. **Mocking:** Use `Mockito.mock()` and `Mockito.when().thenReturn()` for dependencies
7. **Lifecycle:** Avoid `@Before`/`@After` unless absolutely necessary; prefer fresh object creation
8. **Coverage:** Aim to test business logic; UI and framework code may need different strategies

---

*Testing analysis: 2026-04-03*
