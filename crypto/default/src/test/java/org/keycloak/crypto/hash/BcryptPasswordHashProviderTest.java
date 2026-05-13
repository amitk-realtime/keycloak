package org.keycloak.crypto.hash;

import org.junit.Test;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Test bcrypt provider compatibility with PHP-generated hashes.
 * Validates that the provider can generate and verify hashes across versions.
 */
public class BcryptPasswordHashProviderTest {

    private static final String TEST_PASSWORD = "testpassword";
    private static final int COST = 10;

    /**
     * Test that PHP-generated bcrypt hashes can be verified by jbcrypt
     * This is the critical test for the migration.
     * 
     * PHP hash generated with: php -r "echo password_hash('testpassword', PASSWORD_BCRYPT, ['cost' => 10]);"
     */
    @Test
    public void testVerifyPhpGeneratedBcryptHash() {
        // Real PHP-generated hash with password 'testpassword' and cost 10
        String phpHash = "$2y$10$jiv8HOnowI/.g2jDY4XGB.CSvoVLw6k7fN.5YDSVAlip7ktP/9oaq";
        String password = "testpassword";
        
        // Test with default verifier
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), phpHash);
        assertTrue("jbcrypt should verify PHP-generated bcrypt hash", result.verified);
    }

    /**
     * Test that wrong password is rejected for PHP hash
     */
    @Test
    public void testPhpHashRejectsWrongPassword() {
        String phpHash = "$2y$10$jiv8HOnowI/.g2jDY4XGB.CSvoVLw6k7fN.5YDSVAlip7ktP/9oaq";
        String wrongPassword = "wrongpassword";
        
        BCrypt.Result result = BCrypt.verifyer().verify(wrongPassword.toCharArray(), phpHash);
        assertFalse("jbcrypt should reject wrong password for PHP hash", result.verified);
    }

    /**
     * Test that $2a$ hashes can be verified with default verifier
     */
    @Test
    public void testVerify2aHashWithDefaultVerifier() {
        // Generate a $2a$ hash
        byte[] hash2a = BCrypt.with(BCrypt.Version.VERSION_2A).hash(COST, TEST_PASSWORD.toCharArray());
        String hash2aStr = new String(hash2a, StandardCharsets.UTF_8);
        
        // Verify with default verifier (no version specified)
        BCrypt.Result result = BCrypt.verifyer().verify(TEST_PASSWORD.toCharArray(), hash2aStr);
        assertTrue("Default verifier should verify $2a$ hashes", result.verified);
    }

    /**
     * Test that wrong password is rejected
     */
    @Test
    public void testWrongPasswordRejected() {
        // Generate a $2y$ hash
        byte[] hash2y = BCrypt.with(BCrypt.Version.VERSION_2Y).hash(COST, TEST_PASSWORD.toCharArray());
        String hash2yStr = new String(hash2y, StandardCharsets.UTF_8);
        
        // Try to verify with wrong password
        BCrypt.Result result = BCrypt.verifyer().verify("wrongpassword".toCharArray(), hash2yStr);
        assertFalse("Default verifier should reject wrong password", result.verified);
    }

    /**
     * Test cross-version verification: $2y$ hash verified with $2a$ verifier
     */
    @Test
    public void testCrossVersionVerification2yWith2a() {
        // Generate a $2y$ hash
        byte[] hash2y = BCrypt.with(BCrypt.Version.VERSION_2Y).hash(COST, TEST_PASSWORD.toCharArray());
        String hash2yStr = new String(hash2y, StandardCharsets.UTF_8);
        
        // Verify with $2a$ verifier
        BCrypt.Result result = BCrypt.verifyer(BCrypt.Version.VERSION_2A).verify(TEST_PASSWORD.toCharArray(), hash2yStr);
        assertTrue("$2a$ verifier should verify $2y$ hashes", result.verified);
    }

    /**
     * Test cross-version verification: $2a$ hash verified with $2y$ verifier
     */
    @Test
    public void testCrossVersionVerification2aWith2y() {
        // Generate a $2a$ hash
        byte[] hash2a = BCrypt.with(BCrypt.Version.VERSION_2A).hash(COST, TEST_PASSWORD.toCharArray());
        String hash2aStr = new String(hash2a, StandardCharsets.UTF_8);
        
        // Verify with $2y$ verifier
        BCrypt.Result result = BCrypt.verifyer(BCrypt.Version.VERSION_2Y).verify(TEST_PASSWORD.toCharArray(), hash2aStr);
        assertTrue("$2y$ verifier should verify $2a$ hashes", result.verified);
    }
}
