package org.keycloak.storage.tbluser;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

public class TblUserAdapter extends AbstractUserAdapterFederatedStorage {

    private int userId;
    private String username;
    private String fullName;
    private String firstName;
    private String lastName;
    private String email;
    private boolean active;
    private boolean locked;

    public TblUserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model,
                          int userId, String username, String fullName, String firstName,
                          String lastName, String email, boolean active, boolean locked) {
        super(session, realm, model);
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.active = active;
        this.locked = locked;
    }

    @Override
    public String getId() {
        return storageProviderModel.getId() + ":" + userId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        throw new org.keycloak.storage.ReadOnlyException("user is read only for this update");
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        throw new org.keycloak.storage.ReadOnlyException("user is read only for this update");
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        throw new org.keycloak.storage.ReadOnlyException("user is read only for this update");
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        throw new org.keycloak.storage.ReadOnlyException("user is read only for this update");
    }

    @Override
    public boolean isEmailVerified() {
        return true;
    }

    @Override
    public void setEmailVerified(boolean verified) {
        throw new org.keycloak.storage.ReadOnlyException("user is read only for this update");
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override
    public void setEnabled(boolean enabled) {
        throw new org.keycloak.storage.ReadOnlyException("user is read only for this update");
    }

    @Override
    public Long getCreatedTimestamp() {
        return System.currentTimeMillis();
    }

    @Override
    public void setCreatedTimestamp(Long timestamp) {
        throw new org.keycloak.storage.ReadOnlyException("user is read only for this update");
    }
}
