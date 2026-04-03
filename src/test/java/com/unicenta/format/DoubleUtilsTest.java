package com.unicenta.format;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link DoubleUtils#fixDecimals(Number)}.
 *
 * The method applies: Math.rint(value.doubleValue() * 1_000_000.0) / 1_000_000.0
 * which rounds to 6 significant decimal places.
 */
public class DoubleUtilsTest {

    private static final double DELTA = 1e-10;

    // --- Basic values ---

    @Test
    public void fixDecimals_zero_returnsZero() {
        assertEquals(0.0, DoubleUtils.fixDecimals(0), DELTA);
    }

    @Test
    public void fixDecimals_positiveInteger_returnsUnchanged() {
        assertEquals(42.0, DoubleUtils.fixDecimals(42), DELTA);
    }

    @Test
    public void fixDecimals_negativeInteger_returnsUnchanged() {
        assertEquals(-7.0, DoubleUtils.fixDecimals(-7), DELTA);
    }

    @Test
    public void fixDecimals_positiveDouble_returnsValue() {
        assertEquals(1.5, DoubleUtils.fixDecimals(1.5), DELTA);
    }

    @Test
    public void fixDecimals_negativeDouble_returnsValue() {
        assertEquals(-3.25, DoubleUtils.fixDecimals(-3.25), DELTA);
    }

    // --- Floating-point precision ---

    @Test
    public void fixDecimals_oneTenthPlusTwoTenths_equalsThreeTenths() {
        // 0.1 + 0.2 in IEEE 754 is not exactly 0.3 — fixDecimals should normalise it
        double raw = 0.1 + 0.2;
        double result = DoubleUtils.fixDecimals(raw);
        assertEquals(0.3, result, 1e-6);
    }

    @Test
    public void fixDecimals_sixDecimalPlaces_preserved() {
        assertEquals(1.234567, DoubleUtils.fixDecimals(1.234567), DELTA);
    }

    @Test
    public void fixDecimals_seventhDecimalRoundsToSix() {
        // Math.rint uses ties-to-even: 1.0000005 * 1_000_000 = 1_000_000.5 → 1_000_000 (even) → 1.0
        // Use 1.0000006 to avoid tie: 1.0000006 * 1_000_000 = 1_000_000.6 → 1_000_001 → 1.000001
        double result = DoubleUtils.fixDecimals(1.0000006);
        assertEquals(1.000001, result, DELTA);
    }

    // --- Large numbers ---

    @Test
    public void fixDecimals_largePositiveNumber_returnsUnchanged() {
        assertEquals(1_000_000.0, DoubleUtils.fixDecimals(1_000_000.0), DELTA);
    }

    @Test
    public void fixDecimals_largeNegativeNumber_returnsUnchanged() {
        assertEquals(-999_999.0, DoubleUtils.fixDecimals(-999_999.0), DELTA);
    }

    // --- Number subtype ---

    @Test
    public void fixDecimals_integerType_handledCorrectly() {
        Integer intVal = 5;
        assertEquals(5.0, DoubleUtils.fixDecimals(intVal), DELTA);
    }

    @Test
    public void fixDecimals_longType_handledCorrectly() {
        Long longVal = 1_234_567L;
        assertEquals(1_234_567.0, DoubleUtils.fixDecimals(longVal), DELTA);
    }

    @Test
    public void fixDecimals_floatType_handledCorrectly() {
        Float floatVal = 2.5f;
        assertEquals(2.5, DoubleUtils.fixDecimals(floatVal), DELTA);
    }

    // --- Whole numbers expressed as doubles ---

    @Test
    public void fixDecimals_wholeNumberDouble_noFractionalPart() {
        double result = DoubleUtils.fixDecimals(100.0);
        assertEquals(0.0, result % 1.0, DELTA);
    }

    @Test
    public void fixDecimals_negativeWholeNumberDouble_noFractionalPart() {
        double result = DoubleUtils.fixDecimals(-50.0);
        assertEquals(0.0, result % 1.0, DELTA);
    }
}