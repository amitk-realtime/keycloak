package org.keycloak.storage.tbluser;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TblUserStorageProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator {

    private static final Logger logger = Logger.getLogger(TblUserStorageProvider.class);

    private KeycloakSession session;
    private ComponentModel model;
    private DataSource dataSource;

    public TblUserStorageProvider(KeycloakSession session, ComponentModel model, DataSource dataSource) {
        this.session = session;
        this.model = model;
        this.dataSource = dataSource;
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        logger.debugf("Looking up user by username: %s", username);
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT UserID, UserName, FullName, FirstName, LastName, EMail, Active, Locked FROM tblUser WHERE UserName = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToUserModel(realm, rs);
                    }
                }
            }
        } catch (SQLException e) {
            logger.errorf(e, "Error looking up user by username: %s", username);
        }
        return null;
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        logger.debugf("Looking up user by ID: %s", id);
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT UserID, UserName, FullName, FirstName, LastName, EMail, Active, Locked FROM tblUser WHERE UserID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, Integer.parseInt(id));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToUserModel(realm, rs);
                    }
                }
            }
        } catch (SQLException | NumberFormatException e) {
            logger.errorf(e, "Error looking up user by ID: %s", id);
        }
        return null;
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        logger.debugf("Looking up user by email: %s", email);
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT UserID, UserName, FullName, FirstName, LastName, EMail, Active, Locked FROM tblUser WHERE EMail = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToUserModel(realm, rs);
                    }
                }
            }
        } catch (SQLException e) {
            logger.errorf(e, "Error looking up user by email: %s", email);
        }
        return null;
    }

    private UserModel mapResultSetToUserModel(RealmModel realm, ResultSet rs) throws SQLException {
        int userId = rs.getInt("UserID");
        String username = rs.getString("UserName");
        String fullName = rs.getString("FullName");
        String firstName = rs.getString("FirstName");
        String lastName = rs.getString("LastName");
        String email = rs.getString("EMail");
        boolean active = rs.getBoolean("Active");
        boolean locked = rs.getBoolean("Locked");

        return new TblUserAdapter(session, realm, model, userId, username, fullName, firstName, lastName, email, active, locked);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return "password".equals(credentialType);
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return "password".equals(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType())) {
            return false;
        }

        if (!(input instanceof org.keycloak.models.credential.PasswordCredentialModel)) {
            return false;
        }

        String password = input.getChallengeResponse();
        if (password == null) {
            return false;
        }

        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT Password FROM tblUser WHERE UserID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, Integer.parseInt(user.getId()));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("Password");
                        return password.equals(storedPassword);
                    }
                }
            }
        } catch (SQLException | NumberFormatException e) {
            logger.errorf(e, "Error validating password for user: %s", user.getId());
        }

        return false;
    }

    @Override
    public void close() {
    }
}
