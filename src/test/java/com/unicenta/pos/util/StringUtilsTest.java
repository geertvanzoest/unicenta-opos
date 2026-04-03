package com.unicenta.pos.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link StringUtils}.
 */
public class StringUtilsTest {

    // =========================================================================
    // encodeXML
    // =========================================================================

    @Test
    public void encodeXML_ampersand_isEncoded() {
        assertEquals("a&amp;b", StringUtils.encodeXML("a&b"));
    }

    @Test
    public void encodeXML_lessThan_isEncoded() {
        assertEquals("&lt;tag", StringUtils.encodeXML("<tag"));
    }

    @Test
    public void encodeXML_greaterThan_isEncoded() {
        assertEquals("tag&gt;", StringUtils.encodeXML("tag>"));
    }

    @Test
    public void encodeXML_doubleQuote_isEncoded() {
        assertEquals("say &quot;hi&quot;", StringUtils.encodeXML("say \"hi\""));
    }

    @Test
    public void encodeXML_apostrophe_isEncoded() {
        assertEquals("it&apos;s", StringUtils.encodeXML("it's"));
    }

    @Test
    public void encodeXML_null_returnsNull() {
        assertNull(StringUtils.encodeXML(null));
    }

    @Test
    public void encodeXML_plainText_returnedUnchanged() {
        assertEquals("hello world", StringUtils.encodeXML("hello world"));
    }

    @Test
    public void encodeXML_emptyString_returnsEmpty() {
        assertEquals("", StringUtils.encodeXML(""));
    }

    @Test
    public void encodeXML_allSpecialCharsCombined_allEncoded() {
        assertEquals(
            "&amp;&lt;&gt;&quot;&apos;",
            StringUtils.encodeXML("&<>\"'")
        );
    }

    @Test
    public void encodeXML_mixedContent_onlySpecialCharsEncoded() {
        assertEquals("a&lt;b&gt;c&amp;d", StringUtils.encodeXML("a<b>c&d"));
    }

    @Test
    public void encodeXML_multipleAmpersands_allEncoded() {
        assertEquals("&amp;&amp;&amp;", StringUtils.encodeXML("&&&"));
    }

    // =========================================================================
    // byte2hex / hex2byte
    // =========================================================================

    @Test
    public void byte2hex_singleByte_returnsUpperHex() {
        assertEquals("FF", StringUtils.byte2hex(new byte[]{(byte) 0xFF}));
    }

    @Test
    public void byte2hex_zeroByte_returnsTwoZeros() {
        assertEquals("00", StringUtils.byte2hex(new byte[]{0x00}));
    }

    @Test
    public void byte2hex_emptyArray_returnsEmptyString() {
        assertEquals("", StringUtils.byte2hex(new byte[0]));
    }

    @Test
    public void byte2hex_multipleBytes_returnsCorrectHex() {
        assertEquals("0A1BFF", StringUtils.byte2hex(new byte[]{0x0A, 0x1B, (byte) 0xFF}));
    }

    @Test
    public void hex2byte_validHex_returnsCorrectBytes() {
        byte[] result = StringUtils.hex2byte("0A1BFF");
        assertArrayEquals(new byte[]{0x0A, 0x1B, (byte) 0xFF}, result);
    }

    @Test
    public void hex2byte_emptyString_returnsEmptyArray() {
        assertArrayEquals(new byte[0], StringUtils.hex2byte(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void hex2byte_oddLengthString_throwsIllegalArgumentException() {
        StringUtils.hex2byte("ABC");
    }

    @Test
    public void byte2hex_hex2byte_roundtrip() {
        byte[] original = {0x00, 0x42, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
        String hex = StringUtils.byte2hex(original);
        byte[] restored = StringUtils.hex2byte(hex);
        assertArrayEquals(original, restored);
    }

    @Test
    public void hex2byte_lowercaseInput_parsedCorrectly() {
        byte[] result = StringUtils.hex2byte("ff00");
        assertArrayEquals(new byte[]{(byte) 0xFF, 0x00}, result);
    }

    // =========================================================================
    // isNumber
    // =========================================================================

    @Test
    public void isNumber_validDigitsOnly_returnsTrue() {
        assertTrue(StringUtils.isNumber("1234567890"));
    }

    @Test
    public void isNumber_singleDigit_returnsTrue() {
        assertTrue(StringUtils.isNumber("0"));
    }

    @Test
    public void isNumber_containsLetter_returnsFalse() {
        assertFalse(StringUtils.isNumber("123a456"));
    }

    @Test
    public void isNumber_onlyLetters_returnsFalse() {
        assertFalse(StringUtils.isNumber("abc"));
    }

    @Test
    public void isNumber_null_returnsFalse() {
        assertFalse(StringUtils.isNumber(null));
    }

    @Test
    public void isNumber_emptyString_returnsFalse() {
        assertFalse(StringUtils.isNumber(""));
    }

    @Test
    public void isNumber_containsSpace_returnsFalse() {
        assertFalse(StringUtils.isNumber("123 456"));
    }

    @Test
    public void isNumber_containsDecimalPoint_returnsFalse() {
        assertFalse(StringUtils.isNumber("3.14"));
    }

    @Test
    public void isNumber_containsNegativeSign_returnsFalse() {
        assertFalse(StringUtils.isNumber("-5"));
    }

    @Test
    public void isNumber_containsSpecialChar_returnsFalse() {
        assertFalse(StringUtils.isNumber("12#34"));
    }

    // =========================================================================
    // getCardNumber
    // =========================================================================

    @Test
    public void getCardNumber_returnsNonNull() {
        assertNotNull(StringUtils.getCardNumber());
    }

    @Test
    public void getCardNumber_returns12CharString() {
        assertEquals(12, StringUtils.getCardNumber().length());
    }

    @Test
    public void getCardNumber_containsOnlyDigits() {
        assertTrue(StringUtils.isNumber(StringUtils.getCardNumber()));
    }

    @Test
    public void getCardNumber_producesValid12DigitNumbers() {
        String first = StringUtils.getCardNumber();
        String second = StringUtils.getCardNumber();
        assertEquals(12, first.length());
        assertEquals(12, second.length());
        assertTrue(first.matches("\\d{12}"));
        assertTrue(second.matches("\\d{12}"));
    }
}