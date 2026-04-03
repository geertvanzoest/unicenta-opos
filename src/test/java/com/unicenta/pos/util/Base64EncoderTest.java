package com.unicenta.pos.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class Base64EncoderTest {

    // --- encode(byte[]) ---

    @Test
    public void encodeKnownValue() {
        // "Hello" -> "SGVsbG8="
        byte[] input = "Hello".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        assertEquals("SGVsbG8=", Base64Encoder.encode(input));
    }

    @Test
    public void encodeEmptyArray() {
        String result = Base64Encoder.encode(new byte[0]);
        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    public void encodeBinaryData() {
        byte[] binary = {0x00, 0x01, 0x02, (byte) 0xFF, (byte) 0xFE};
        String encoded = Base64Encoder.encode(binary);
        assertNotNull(encoded);
        assertFalse(encoded.isEmpty());
    }

    // --- decode(String) ---

    @Test
    public void decodeKnownValue() {
        byte[] result = Base64Encoder.decode("SGVsbG8=");
        assertNotNull(result);
        assertEquals("Hello", new String(result, java.nio.charset.StandardCharsets.UTF_8));
    }

    @Test
    public void decodeEmptyString() {
        byte[] result = Base64Encoder.decode("");
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    // --- encode / decode roundtrip ---

    @Test
    public void roundtripAsciiString() {
        byte[] original = "Hello, World!".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String encoded = Base64Encoder.encode(original);
        byte[] decoded = Base64Encoder.decode(encoded);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void roundtripBinaryData() {
        byte[] original = new byte[256];
        for (int i = 0; i < 256; i++) {
            original[i] = (byte) i;
        }
        String encoded = Base64Encoder.encode(original);
        byte[] decoded = Base64Encoder.decode(encoded);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void roundtripUnicodeString() {
        byte[] original = "Ünïcödé".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String encoded = Base64Encoder.encode(original);
        byte[] decoded = Base64Encoder.decode(encoded);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void roundtripSingleByte() {
        byte[] original = {(byte) 0xAB};
        String encoded = Base64Encoder.encode(original);
        byte[] decoded = Base64Encoder.decode(encoded);
        assertArrayEquals(original, decoded);
    }

    // --- encodeChunked(byte[]) ---

    @Test
    public void encodeChunkedContainsNewline() {
        // encodeBase64Chunked wraps at 76 chars — any input > 57 bytes will produce a newline
        byte[] input = new byte[80];
        for (int i = 0; i < 80; i++) {
            input[i] = (byte) ('A' + (i % 26));
        }
        String chunked = Base64Encoder.encodeChunked(input);
        assertNotNull(chunked);
        assertTrue("encodeChunked output should contain a newline", chunked.contains("\n") || chunked.contains("\r"));
    }

    @Test
    public void encodeChunkedShortInputMayEndWithNewline() {
        // Even short input gets a trailing CRLF from encodeBase64Chunked
        byte[] input = "Hi".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String chunked = Base64Encoder.encodeChunked(input);
        assertNotNull(chunked);
        // Content without whitespace must equal the plain encode
        String plain = Base64Encoder.encode(input);
        assertEquals(plain, chunked.trim());
    }

    @Test
    public void encodeChunkedRoundtrip() {
        // decode must accept chunked (multi-line) output
        byte[] original = new byte[200];
        for (int i = 0; i < 200; i++) {
            original[i] = (byte) i;
        }
        String chunked = Base64Encoder.encodeChunked(original);
        byte[] decoded = Base64Encoder.decode(chunked);
        assertArrayEquals(original, decoded);
    }

    @Test
    public void encodeChunkedEmptyArray() {
        String result = Base64Encoder.encodeChunked(new byte[0]);
        assertNotNull(result);
    }
}
