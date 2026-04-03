package com.unicenta.format;

import com.unicenta.basic.BasicException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.SwingConstants;

public class FormatsTest {

    @After
    public void resetPatterns() {
        Formats.setIntegerPattern(null);
        Formats.setCurrencyPattern(null);
    }

    // ---- STRING ----

    @Test
    public void stringFormatValue() {
        Assert.assertEquals("hello", Formats.STRING.formatValue("hello"));
    }

    @Test
    public void stringFormatValueNull() {
        Assert.assertEquals("", Formats.STRING.formatValue(null));
    }

    @Test
    public void stringParseValue() throws BasicException {
        Assert.assertEquals("world", Formats.STRING.parseValue("world"));
    }

    @Test
    public void stringParseValueNull() throws BasicException {
        Assert.assertNull(Formats.STRING.parseValue(null));
    }

    @Test
    public void stringParseValueEmpty() throws BasicException {
        Assert.assertNull(Formats.STRING.parseValue(""));
    }

    // ---- INT ----

    @Test
    public void intFormatValue() {
        String result = Formats.INT.formatValue(42);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("42"));
    }

    @Test
    public void intFormatValueNull() {
        Assert.assertEquals("", Formats.INT.formatValue(null));
    }

    @Test
    public void intParseValue() throws BasicException {
        // Use formatValue to produce a locale-safe string, then parse it back
        String formatted = Formats.INT.formatValue(123);
        Object result = Formats.INT.parseValue(formatted);
        Assert.assertEquals(123, result);
    }

    @Test
    public void intParseValueNull() throws BasicException {
        Assert.assertNull(Formats.INT.parseValue(null));
    }

    // ---- DOUBLE ----

    @Test
    public void doubleFormatValueNull() {
        Assert.assertEquals("", Formats.DOUBLE.formatValue(null));
    }

    @Test
    public void doubleFormatValueNotNull() {
        String result = Formats.DOUBLE.formatValue(3.14);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
    }

    // ---- BOOLEAN ----

    @Test
    public void booleanFormatTrue() {
        Assert.assertEquals("true", Formats.BOOLEAN.formatValue(Boolean.TRUE));
    }

    @Test
    public void booleanFormatFalse() {
        Assert.assertEquals("false", Formats.BOOLEAN.formatValue(Boolean.FALSE));
    }

    @Test
    public void booleanParseTrue() throws BasicException {
        Assert.assertEquals(Boolean.TRUE, Formats.BOOLEAN.parseValue("true"));
    }

    @Test
    public void booleanParseFalse() throws BasicException {
        Assert.assertEquals(Boolean.FALSE, Formats.BOOLEAN.parseValue("false"));
    }

    @Test
    public void booleanFormatNull() {
        Assert.assertEquals("", Formats.BOOLEAN.formatValue(null));
    }

    // ---- CURRENCY ----

    @Test
    public void currencyFormatNotNull() {
        String result = Formats.CURRENCY.formatValue(9.99);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void currencyFormatNull() {
        Assert.assertEquals("", Formats.CURRENCY.formatValue(null));
    }

    // ---- PERCENT ----

    @Test
    public void percentFormatNotNull() {
        String result = Formats.PERCENT.formatValue(0.21);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void percentFormatNull() {
        Assert.assertEquals("", Formats.PERCENT.formatValue(null));
    }

    // ---- BYTEA ----

    @Test
    public void byteaFormatValue() {
        byte[] bytes = "test".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        Assert.assertEquals("test", Formats.BYTEA.formatValue(bytes));
    }

    @Test
    public void byteaParseValue() throws BasicException {
        Object result = Formats.BYTEA.parseValue("hello");
        Assert.assertTrue(result instanceof byte[]);
        Assert.assertArrayEquals(
            "hello".getBytes(java.nio.charset.StandardCharsets.UTF_8),
            (byte[]) result
        );
    }

    @Test
    public void byteaFormatNull() {
        Assert.assertEquals("", Formats.BYTEA.formatValue(null));
    }

    // ---- getCurrencyDecimals ----

    @Test
    public void currencyDecimalsNonNegative() {
        Assert.assertTrue(Formats.getCurrencyDecimals() >= 0);
    }

    // ---- parseValue with default ----

    @Test
    public void parseValueWithDefaultReturnsDefaultForNull() throws BasicException {
        Object result = Formats.STRING.parseValue(null, "default");
        Assert.assertEquals("default", result);
    }

    @Test
    public void parseValueWithDefaultReturnsDefaultForEmpty() throws BasicException {
        Object result = Formats.STRING.parseValue("", "default");
        Assert.assertEquals("default", result);
    }

    @Test
    public void parseValueWithDefaultReturnsValueWhenPresent() throws BasicException {
        Object result = Formats.STRING.parseValue("present", "default");
        Assert.assertEquals("present", result);
    }

    // ---- alignment values ----

    @Test
    public void stringAlignmentIsLeft() {
        Assert.assertEquals(SwingConstants.LEFT, Formats.STRING.getAlignment());
    }

    @Test
    public void intAlignmentIsRight() {
        Assert.assertEquals(SwingConstants.RIGHT, Formats.INT.getAlignment());
    }

    @Test
    public void booleanAlignmentIsCenter() {
        Assert.assertEquals(SwingConstants.CENTER, Formats.BOOLEAN.getAlignment());
    }

    // ---- custom patterns ----

    @Test
    public void setIntegerPatternNull() {
        // null resets to default — should not throw
        Formats.setIntegerPattern(null);
        String result = Formats.INT.formatValue(7);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("7"));
    }

    @Test
    public void setIntegerPatternEmpty() {
        // empty string resets to default
        Formats.setIntegerPattern("");
        String result = Formats.INT.formatValue(8);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("8"));
    }

    @Test
    public void setIntegerPatternCustom() {
        Formats.setIntegerPattern("000");
        String result = Formats.INT.formatValue(5);
        Assert.assertEquals("005", result);
    }

    @Test
    public void setCurrencyPatternNull() {
        // null resets to default
        Formats.setCurrencyPattern(null);
        String result = Formats.CURRENCY.formatValue(1.0);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void setCurrencyPatternEmpty() {
        Formats.setCurrencyPattern("");
        String result = Formats.CURRENCY.formatValue(2.5);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void setCurrencyPatternCustom() {
        Formats.setCurrencyPattern("#.00");
        String result = Formats.CURRENCY.formatValue(3.0);
        Assert.assertEquals("3.00", result);
    }
}
