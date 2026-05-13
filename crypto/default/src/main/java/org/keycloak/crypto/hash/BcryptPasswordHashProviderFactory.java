package org.keycloak.crypto.hash;

import org.keycloak.Config;
import org.keycloak.credential.hash.PasswordHashProvider;
import org.keycloak.credential.hash.PasswordHashProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.jboss.logging.Logger;

/**
 * Factory for bcrypt password hash provider.
 * Provides bcrypt password hashing support for migrated legacy systems (e.g., PHP password_hash).
 */
public class BcryptPasswordHashProviderFactory implements PasswordHashProviderFactory {

    private static final Logger logger = Logger.getLogger(BcryptPasswordHashProviderFactory.class);
    
    public static final String ID = "bcrypt";

    @Override
    public PasswordHashProvider create(KeycloakSession session) {
        return new BcryptPasswordHashProvider();
    }

    @Override
    public void init(Config.Scope config) {
        logger.debug("Initializing bcrypt password hash provider factory");
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        logger.debug("Post-initializing bcrypt password hash provider factory");
    }

    @Override
    public void close() {
        logger.debug("Closing bcrypt password hash provider factory");
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public int order() {
        return 100; // Lower priority than default providers
    }
}
