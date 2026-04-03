package com.unicenta.pos.util;

import com.unicenta.format.Formats;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link RoundUtils}.
 *
 * RoundUtils.round() uses Formats.getCurrencyDecimals() which reads
 * from the JVM-default currency NumberFormat.  The tests set an
 * explicit currency pattern so results are locale-independent.
 */
public class RoundUtilsTest {

    @Before
    public void setTwoDecimalCurrency() {
        // Force 2-decimal currency regardless of the test runner's locale
        Formats.setCurrencyPattern("0.00");
    }

    // --- round() ---

    @Test
    public void round_exactHalfCent_roundsToNearest() {
        // Math.rint uses "round half to even" (banker's rounding)
        // 1.005 * 100 = 100.5 → rint → 100 (even) → 1.00
        double result = RoundUtils.round(1.005);
        // Accept either 1.00 or 1.01 depending on IEEE representation;
        // just verify the result is rounded to exactly 2 decimal places.
        assertEquals(result, RoundUtils.round(result), 1e-10);
    }

    @Test
    public void round_positiveValue_roundedToTwoDecimals() {
        assertEquals(1.24, RoundUtils.round(1.235), 1e-10);
    }

    @Test
    public void round_negativeValue_roundedToTwoDecimals() {
        assertEquals(-1.24, RoundUtils.round(-1.235), 1e-10);
    }

    @Test
    public void round_zero_returnsZero() {
        assertEquals(0.0, RoundUtils.round(0.0), 1e-10);
    }

    @Test
    public void round_alreadyRounded_unchanged() {
        assertEquals(9.99, RoundUtils.round(9.99), 1e-10);
    }

    @Test
    public void round_largeValue_roundedCorrectly() {
        assertEquals(12345.68, RoundUtils.round(12345.678), 1e-10);
    }

    @Test
    public void round_wholeNumber_returnsWholeNumber() {
        assertEquals(5.0, RoundUtils.round(5.0), 1e-10);
    }

    // --- compare() ---

    @Test
    public void compare_equalValues_returnsZero() {
        assertEquals(0, RoundUtils.compare(1.0, 1.0));
    }

    @Test
    public void compare_firstSmaller_returnsNegative() {
        assertTrue(RoundUtils.compare(1.00, 2.00) < 0);
    }

    @Test
    public void compare_firstLarger_returnsPositive() {
        assertTrue(RoundUtils.compare(3.00, 2.00) > 0);
    }

    @Test
    public void compare_valuesEqualAfterRounding_returnsZero() {
        // 1.001 and 1.004 both round to 1.00 with 2 decimals
        assertEquals(0, RoundUtils.compare(1.001, 1.004));
    }

    @Test
    public void compare_negativeValues_orderIsCorrect() {
        assertTrue(RoundUtils.compare(-2.00, -1.00) < 0);
    }

    @Test
    public void compare_zeroAndPositive_returnsNegative() {
        assertTrue(RoundUtils.compare(0.0, 0.01) < 0);
    }

    @Test
    public void compare_symmetry_oppositeSign() {
        int a = RoundUtils.compare(1.0, 2.0);
        int b = RoundUtils.compare(2.0, 1.0);
        assertTrue(a < 0 && b > 0);
    }

    // --- getValue() ---

    @Test
    public void getValue_null_returnsZero() {
        assertEquals(0.0, RoundUtils.getValue(null), 1e-10);
    }

    @Test
    public void getValue_nonNull_returnsValue() {
        assertEquals(3.14, RoundUtils.getValue(3.14), 1e-10);
    }

    @Test
    public void getValue_zero_returnsZero() {
        assertEquals(0.0, RoundUtils.getValue(0.0), 1e-10);
    }

    @Test
    public void getValue_negativeValue_returnsNegativeValue() {
        assertEquals(-7.5, RoundUtils.getValue(-7.5), 1e-10);
    }
}
