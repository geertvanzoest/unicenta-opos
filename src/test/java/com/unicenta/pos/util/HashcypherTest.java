package com.unicenta.pos.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class HashcypherTest {

    // -----------------------------------------------------------------------
    // hashString(String)
    // -----------------------------------------------------------------------

    @Test
    public void hashStringNullReturnsEmptyPrefix() {
        assertEquals("empty:", Hashcypher.hashString(null));
    }

    @Test
    public void hashStringEmptyReturnsEmptyPrefix() {
        assertEquals("empty:", Hashcypher.hashString(""));
    }

    @Test
    public void hashStringProducesSha1Prefix() {
        String hash = Hashcypher.hashString("password");
        assertNotNull(hash);
        assertTrue("hash must start with 'sha1:'", hash.startsWith("sha1:"));
    }

    @Test
    public void hashStringKnownValue() {
        // SHA-1("Hello") = aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d
        String hash = Hashcypher.hashString("Hello");
        assertEquals("sha1:AAF4C61DDCC5E8A2DABEDE0F3B482CD9AEA9434D", hash);
    }

    @Test
    public void hashStringIsConsistent() {
        String hash1 = Hashcypher.hashString("sameInput");
        String hash2 = Hashcypher.hashString("sameInput");
        assertEquals("hashString must be deterministic", hash1, hash2);
    }

    @Test
    public void hashStringDifferentInputsDifferentHashes() {
        String hash1 = Hashcypher.hashString("input1");
        String hash2 = Hashcypher.hashString("input2");
        assertNotEquals("Different inputs must produce different hashes", hash1, hash2);
    }

    @Test
    public void hashStringSha1HexLength() {
        // "sha1:" (5 chars) + 40 hex chars = 45 total
        String hash = Hashcypher.hashString("test");
        assertEquals(45, hash.length());
    }

    // -----------------------------------------------------------------------
    // authenticate(String password, String hashPassword)
    // -----------------------------------------------------------------------

    // --- sha1 prefix ---

    @Test
    public void authenticateSha1ValidPassword() {
        String hash = Hashcypher.hashString("mypassword");
        assertTrue(Hashcypher.authenticate("mypassword", hash));
    }

    @Test
    public void authenticateSha1InvalidPassword() {
        String hash = Hashcypher.hashString("mypassword");
        assertFalse(Hashcypher.authenticate("wrongpassword", hash));
    }

    @Test
    public void authenticateSha1CaseSensitive() {
        String hash = Hashcypher.hashString("Secret");
        assertFalse(Hashcypher.authenticate("secret", hash));
    }

    // --- plain: prefix ---

    @Test
    public void authenticatePlainValidPassword() {
        assertTrue(Hashcypher.authenticate("abc123", "plain:abc123"));
    }

    @Test
    public void authenticatePlainInvalidPassword() {
        assertFalse(Hashcypher.authenticate("wrong", "plain:abc123"));
    }

    @Test
    public void authenticatePlainEmptyPassword() {
        assertTrue(Hashcypher.authenticate("", "plain:"));
    }

    // --- empty: prefix ---

    @Test
    public void authenticateEmptyPrefixWithNullPassword() {
        assertTrue(Hashcypher.authenticate(null, "empty:"));
    }

    @Test
    public void authenticateEmptyPrefixWithEmptyPassword() {
        assertTrue(Hashcypher.authenticate("", "empty:"));
    }

    @Test
    public void authenticateEmptyPrefixWithNonEmptyPasswordFails() {
        assertFalse(Hashcypher.authenticate("notempty", "empty:"));
    }

    // --- null hash ---

    @Test
    public void authenticateNullHashWithNullPassword() {
        assertTrue(Hashcypher.authenticate(null, null));
    }

    @Test
    public void authenticateNullHashWithEmptyPassword() {
        assertTrue(Hashcypher.authenticate("", null));
    }

    @Test
    public void authenticateNullHashWithNonEmptyPasswordFails() {
        assertFalse(Hashcypher.authenticate("something", null));
    }

    // --- empty string hash (treated same as "empty:") ---

    @Test
    public void authenticateEmptyStringHashWithEmptyPassword() {
        assertTrue(Hashcypher.authenticate("", ""));
    }

    @Test
    public void authenticateEmptyStringHashWithNullPassword() {
        assertTrue(Hashcypher.authenticate(null, ""));
    }

    @Test
    public void authenticateEmptyStringHashWithNonEmptyPasswordFails() {
        assertFalse(Hashcypher.authenticate("pass", ""));
    }

    // --- legacy plain (no prefix) ---

    @Test
    public void authenticateLegacyPlainPasswordMatchesDirect() {
        // Hash without a recognised prefix: direct string comparison
        assertTrue(Hashcypher.authenticate("legacypass", "legacypass"));
    }

    @Test
    public void authenticateLegacyPlainPasswordWrongFails() {
        assertFalse(Hashcypher.authenticate("wrong", "legacypass"));
    }
}
