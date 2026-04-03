package com.unicenta.pos.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link StringParser}.
 *
 * StringParser reads tokens from a string, splitting on a given delimiter char.
 * Each call to nextToken(char) returns the text up to (but not including) the
 * next occurrence of the delimiter, then advances past the delimiter.
 * When the input is exhausted, further calls return "".
 */
public class StringParserTest {

    // =========================================================================
    // Basic splitting
    // =========================================================================

    @Test
    public void nextToken_basicSplit_firstToken() {
        StringParser parser = new StringParser("hello,world");
        assertEquals("hello", parser.nextToken(','));
    }

    @Test
    public void nextToken_basicSplit_secondToken() {
        StringParser parser = new StringParser("hello,world");
        parser.nextToken(',');
        assertEquals("world", parser.nextToken(','));
    }

    @Test
    public void nextToken_threeTokens_allReturned() {
        StringParser parser = new StringParser("a,b,c");
        assertEquals("a", parser.nextToken(','));
        assertEquals("b", parser.nextToken(','));
        assertEquals("c", parser.nextToken(','));
    }

    // =========================================================================
    // No delimiter in string
    // =========================================================================

    @Test
    public void nextToken_noDelimiterPresent_returnsWholeString() {
        StringParser parser = new StringParser("nodelimiter");
        assertEquals("nodelimiter", parser.nextToken(','));
    }

    @Test
    public void nextToken_noDelimiter_secondCallReturnsEmpty() {
        StringParser parser = new StringParser("nodelimiter");
        parser.nextToken(',');
        assertEquals("", parser.nextToken(','));
    }

    // =========================================================================
    // Empty input
    // =========================================================================

    @Test
    public void nextToken_emptyString_returnsEmptyImmediately() {
        StringParser parser = new StringParser("");
        assertEquals("", parser.nextToken(','));
    }

    @Test
    public void nextToken_emptyString_repeatedCallsReturnEmpty() {
        StringParser parser = new StringParser("");
        assertEquals("", parser.nextToken(','));
        assertEquals("", parser.nextToken(','));
    }

    // =========================================================================
    // Beyond end of input
    // =========================================================================

    @Test
    public void nextToken_callAfterExhausted_returnsEmpty() {
        StringParser parser = new StringParser("x,y");
        parser.nextToken(','); // "x"
        parser.nextToken(','); // "y"
        assertEquals("", parser.nextToken(','));
    }

    @Test
    public void nextToken_manyCallsAfterExhausted_allReturnEmpty() {
        StringParser parser = new StringParser("only");
        parser.nextToken(',');
        assertEquals("", parser.nextToken(','));
        assertEquals("", parser.nextToken(','));
        assertEquals("", parser.nextToken(','));
    }

    // =========================================================================
    // Adjacent delimiters (empty tokens)
    // =========================================================================

    @Test
    public void nextToken_adjacentDelimiters_returnsEmptyTokenBetween() {
        StringParser parser = new StringParser("a,,b");
        assertEquals("a", parser.nextToken(','));
        assertEquals("",  parser.nextToken(','));
        assertEquals("b", parser.nextToken(','));
    }

    @Test
    public void nextToken_leadingDelimiter_firstTokenIsEmpty() {
        StringParser parser = new StringParser(",b");
        assertEquals("", parser.nextToken(','));
        assertEquals("b", parser.nextToken(','));
    }

    @Test
    public void nextToken_trailingDelimiter_lastTokenIsEmpty() {
        StringParser parser = new StringParser("a,");
        assertEquals("a", parser.nextToken(','));
        assertEquals("",  parser.nextToken(','));
    }

    // =========================================================================
    // Different delimiters
    // =========================================================================

    @Test
    public void nextToken_pipeDelimiter_splitsCorrectly() {
        StringParser parser = new StringParser("foo|bar|baz");
        assertEquals("foo", parser.nextToken('|'));
        assertEquals("bar", parser.nextToken('|'));
        assertEquals("baz", parser.nextToken('|'));
    }

    @Test
    public void nextToken_semicolonDelimiter_splitsCorrectly() {
        StringParser parser = new StringParser("one;two");
        assertEquals("one", parser.nextToken(';'));
        assertEquals("two", parser.nextToken(';'));
    }

    @Test
    public void nextToken_mixedDelimiters_eachCallUsesOwnDelimiter() {
        // First token ends at ',' second token ends at ';'
        StringParser parser = new StringParser("first,second;third");
        assertEquals("first",  parser.nextToken(','));
        assertEquals("second", parser.nextToken(';'));
        assertEquals("third",  parser.nextToken(','));
    }

    // =========================================================================
    // Single-character string
    // =========================================================================

    @Test
    public void nextToken_singleChar_noDelimiter_returnsThatChar() {
        StringParser parser = new StringParser("X");
        assertEquals("X", parser.nextToken(','));
    }

    @Test
    public void nextToken_singleChar_isDelimiter_returnsEmpty() {
        StringParser parser = new StringParser(",");
        assertEquals("", parser.nextToken(','));
        assertEquals("", parser.nextToken(','));
    }
}
