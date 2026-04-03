package com.unicenta.pos.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class AltEncrypterTest {

    // --- roundtrip ---

    @Test
    public void roundtripSimpleString() {
        AltEncrypter enc = new AltEncrypter("secretkey");
        String original = "Hello, World!";
        String encrypted = enc.encrypt(original);
        assertNotNull(encrypted);
        assertNotEquals(original, encrypted);
        assertEquals(original, enc.decrypt(encrypted));
    }

    @Test
    public void roundtripEmptyString() {
        AltEncrypter enc = new AltEncrypter("somepassphrase");
        String original = "";
        String encrypted = enc.encrypt(original);
        assertNotNull(encrypted);
        assertEquals(original, enc.decrypt(encrypted));
    }

    @Test
    public void roundtripSpecialCharacters() {
        AltEncrypter enc = new AltEncrypter("p@$$w0rd!");
        String original = "Spécïàl chârs: <>&\"'\n\t";
        String encrypted = enc.encrypt(original);
        assertNotNull(encrypted);
        assertEquals(original, enc.decrypt(encrypted));
    }

    @Test
    public void roundtripLongString() {
        AltEncrypter enc = new AltEncrypter("longpassphrase");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            sb.append("abcdefghij");
        }
        String original = sb.toString();
        String encrypted = enc.encrypt(original);
        assertNotNull(encrypted);
        assertEquals(original, enc.decrypt(encrypted));
    }

    @Test
    public void roundtripNumericString() {
        AltEncrypter enc = new AltEncrypter("numerickey");
        String original = "1234567890";
        assertEquals(original, enc.decrypt(enc.encrypt(original)));
    }

    // --- different passphrases produce different output ---

    @Test
    public void differentPassphrasesProduceDifferentCiphertext() {
        AltEncrypter enc1 = new AltEncrypter("passphrase1");
        AltEncrypter enc2 = new AltEncrypter("passphrase2");
        String original = "SensitiveData";
        String cipher1 = enc1.encrypt(original);
        String cipher2 = enc2.encrypt(original);
        assertNotNull(cipher1);
        assertNotNull(cipher2);
        assertNotEquals("Different passphrases must produce different ciphertext", cipher1, cipher2);
    }

    // --- wrong passphrase cannot decrypt ---

    @Test
    public void wrongPassphraseCannotDecryptCorrectly() {
        AltEncrypter encRight = new AltEncrypter("correctpassphrase");
        AltEncrypter encWrong = new AltEncrypter("wrongpassphrase");
        String original = "TopSecret";
        String encrypted = encRight.encrypt(original);
        // decrypt with wrong key: either returns null or garbage — must not equal original
        String decrypted = encWrong.decrypt(encrypted);
        assertNotEquals("Wrong passphrase must not yield the original plaintext", original, decrypted);
    }

    // --- same passphrase, same result (deterministic with seeded PRNG) ---

    @Test
    public void samePassphraseProducesSameCiphertext() {
        String original = "Deterministic";
        AltEncrypter enc1 = new AltEncrypter("fixedkey");
        AltEncrypter enc2 = new AltEncrypter("fixedkey");
        assertEquals(enc1.encrypt(original), enc2.encrypt(original));
    }

    // --- encrypt output is hex string (only hex chars) ---

    @Test
    public void encryptOutputIsHexString() {
        AltEncrypter enc = new AltEncrypter("hexcheck");
        String encrypted = enc.encrypt("test");
        assertNotNull(encrypted);
        assertTrue("Encrypted output should be a hex string",
                encrypted.matches("[0-9A-Fa-f]+"));
    }
}
