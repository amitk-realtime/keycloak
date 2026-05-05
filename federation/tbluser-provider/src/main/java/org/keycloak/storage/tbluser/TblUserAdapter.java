package org.keycloak.storage.tbluser;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        return String.valueOf(userId);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        // Read-only from tblUser
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        // Read-only from tblUser
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        // Read-only from tblUser
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        // Read-only from tblUser
    }

    @Override
    public boolean isEmailVerified() {
        return true;
    }

    @Override
    public void setEmailVerified(boolean verified) {
        // Read-only from tblUser
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override
    public void setEnabled(boolean enabled) {
        // Read-only from tblUser
    }

    @Override
    public long getCreatedTimestamp() {
        return 0;
    }

    @Override
    public void setCreatedTimestamp(long timestamp) {
        // Read-only from tblUser
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public void setAccountNonLocked(boolean nonLocked) {
        // Read-only from tblUser
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        Map<String, List<String>> attrs = super.getAttributes();
        attrs.put("fullName", Collections.singletonList(fullName != null ? fullName : ""));
        return attrs;
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        // Read-only from tblUser
    }

    @Override
    public void removeAttribute(String name) {
        // Read-only from tblUser
    }

    @Override
    public String getFirstAttribute(String name) {
        if ("fullName".equals(name)) {
            return fullName;
        }
        return super.getFirstAttribute(name);
    }

    @Override
    public List<String> getAttribute(String name) {
        if ("fullName".equals(name)) {
            return Collections.singletonList(fullName != null ? fullName : "");
        }
        return super.getAttribute(name);
    }
}
