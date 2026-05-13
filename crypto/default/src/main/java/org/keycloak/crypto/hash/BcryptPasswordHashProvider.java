package org.keycloak.crypto.hash;

import org.keycloak.credential.hash.PasswordHashProvider;
import org.keycloak.models.PasswordPolicy;
import org.keycloak.models.credential.PasswordCredentialModel;
import at.favre.lib.crypto.bcrypt.BCrypt;
import org.jboss.logging.Logger;

/**
 * Password hash provider for bcrypt algorithm.
 * Supports validation of bcrypt hashes from legacy systems (e.g., PHP password_hash with $2y$ prefix).
 * 
 * Uses at.favre.lib:bcrypt which supports cross-version verification:
 * - Generates new hashes with $2y$ prefix (PHP-compatible)
 * - Verifies both $2y$ (PHP) and $2a$/$2b$ (Java) hashes
 */
public class BcryptPasswordHashProvider implements PasswordHashProvider {

    private static final Logger logger = Logger.getLogger(BcryptPasswordHashProvider.class);
    
    public static final String ID = "bcrypt";

    @Override
    public PasswordCredentialModel encodedCredential(String rawPassword, int iterations) {
        try {
            // Generate new hashes with $2y$ prefix (PHP-compatible)
            // Cost parameter range: 4-31
            int cost = Math.min(Math.max(iterations, 4), 31);
            String hash = BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(cost, rawPassword.toCharArray());
            
            return PasswordCredentialModel.createFromValues(
                ID,
                null,
                cost,
                hash
            );
        } catch (Exception e) {
            logger.error("Error encoding bcrypt password", e);
            throw new RuntimeException("Failed to encode bcrypt password", e);
        }
    }

    @Override
    public boolean verify(String rawPassword, PasswordCredentialModel credential) {
        try {
            if (credential == null || credential.getPasswordSecretData() == null) {
                logger.debug("Credential or secret data is null");
                return false;
            }
            
            String hash = credential.getPasswordSecretData().getValue();
            if (hash == null || hash.isEmpty()) {
                logger.debug("Hash value is null or empty");
                return false;
            }
            
            // Use default verifier which accepts all bcrypt versions ($2y$, $2a$, $2b$)
            // This allows verification of both PHP-generated ($2y$) and Java-generated hashes
            BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toCharArray(), hash);
            return result.verified;
        } catch (IllegalArgumentException e) {
            logger.debug("Invalid bcrypt hash format: " + e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Error verifying bcrypt password", e);
            return false;
        }
    }

    @Override
    public boolean policyCheck(PasswordPolicy policy, PasswordCredentialModel credential) {
        // Bcrypt hashes are always considered valid if they verify correctly
        // No additional policy checks needed for bcrypt
        return true;
    }

    @Override
    public void close() {
        // No resources to close
    }
}
