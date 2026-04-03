package com.unicenta.pos.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class LuhnAlgorithmTest {

    // -----------------------------------------------------------------------
    // Valid card numbers
    // -----------------------------------------------------------------------

    @Test
    public void validVisaTestCard() {
        assertTrue(LuhnAlgorithm.checkCC("4111111111111111"));
    }

    @Test
    public void validMastercardTestCard() {
        assertTrue(LuhnAlgorithm.checkCC("5500000000000004"));
    }

    @Test
    public void validAmexTestCard() {
        assertTrue(LuhnAlgorithm.checkCC("340000000000009"));
    }

    @Test
    public void validDiscoverTestCard() {
        assertTrue(LuhnAlgorithm.checkCC("6011000000000004"));
    }

    @Test
    public void validSingleZero() {
        // "0" — sum=0, 0%10==0 => valid per Luhn definition
        assertTrue(LuhnAlgorithm.checkCC("0"));
    }

    // -----------------------------------------------------------------------
    // Invalid card numbers — wrong check digit
    // -----------------------------------------------------------------------

    @Test
    public void invalidVisaWrongCheckDigit() {
        // Last digit changed from 1 to 2
        assertFalse(LuhnAlgorithm.checkCC("4111111111111112"));
    }

    @Test
    public void invalidMastercardWrongCheckDigit() {
        // Last digit changed from 4 to 5
        assertFalse(LuhnAlgorithm.checkCC("5500000000000005"));
    }

    @Test
    public void invalidAmexWrongCheckDigit() {
        // Last digit changed from 9 to 8
        assertFalse(LuhnAlgorithm.checkCC("340000000000008"));
    }

    @Test
    public void invalidDiscoverWrongCheckDigit() {
        // Last digit changed from 4 to 3
        assertFalse(LuhnAlgorithm.checkCC("6011000000000003"));
    }

    // -----------------------------------------------------------------------
    // Short / minimal numbers
    // -----------------------------------------------------------------------

    @Test
    public void shortNumberSingleNonZeroDigit() {
        // "1" — sum=1, 1%10!=0 => invalid
        assertFalse(LuhnAlgorithm.checkCC("1"));
    }

    @Test
    public void shortTwoDigitInvalidNumber() {
        assertFalse(LuhnAlgorithm.checkCC("12"));
    }

    @Test
    public void shortTwoDigitValidNumber() {
        // "18": from right: 8 (odd pos, unchanged) + 1*2=2 => sum=10 => valid
        assertTrue(LuhnAlgorithm.checkCC("18"));
    }

    // -----------------------------------------------------------------------
    // Edge cases — null, empty, non-numeric, spaces, dashes
    // -----------------------------------------------------------------------

    @Test
    public void nullInputReturnsFalse() {
        // StringUtils.isNumber(null) returns false
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

    @Test
    public void mixedAlphanumericReturnsFalse() {
        assertFalse(LuhnAlgorithm.checkCC("4111abc1111111"));
    }

    @Test
    public void numberWithSpacesReturnsFalse() {
        // Spaces are not digits — StringUtils.isNumber rejects them
        assertFalse(LuhnAlgorithm.checkCC("4111 1111 1111 1111"));
    }

    @Test
    public void numberWithDashesReturnsFalse() {
        assertFalse(LuhnAlgorithm.checkCC("4111-1111-1111-1111"));
    }

    @Test
    public void numberWithPlusSignReturnsFalse() {
        assertFalse(LuhnAlgorithm.checkCC("+4111111111111111"));
    }

    @Test
    public void numberWithDecimalPointReturnsFalse() {
        assertFalse(LuhnAlgorithm.checkCC("4111111111111.1"));
    }
}
