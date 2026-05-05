package org.keycloak.storage.tbluser;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.storage.UserStorageProviderFactory;

import org.jboss.logging.Logger;

public class TblUserStorageProviderFactory implements UserStorageProviderFactory<TblUserStorageProvider> {

    private static final Logger logger = Logger.getLogger(TblUserStorageProviderFactory.class);

    public static final String PROVIDER_ID = "tbluser";
    public static final String DATASOURCE_JNDI = "datasource-jndi";
    public static final String DATASOURCE_JNDI_DEFAULT = "java:jboss/datasources/ExampleDS";

    @Override
    public TblUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        logger.infof("Creating TblUserStorageProvider for model: %s", model.getName());
        
        String jndiName = model.getConfig().getFirst(DATASOURCE_JNDI);
        if (jndiName == null) {
            jndiName = DATASOURCE_JNDI_DEFAULT;
        }
        
        DataSource dataSource = lookupDataSource(jndiName);
        if (dataSource == null) {
            throw new RuntimeException("Could not find DataSource: " + jndiName);
        }
        
        return new TblUserStorageProvider(session, model, dataSource);
    }

    private DataSource lookupDataSource(String jndiName) {
        try {
            javax.naming.InitialContext ctx = new javax.naming.InitialContext();
            return (DataSource) ctx.lookup(jndiName);
        } catch (Exception e) {
            logger.warnf(e, "Could not lookup DataSource %s", jndiName);
            return null;
        }
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getHelpText() {
        return "User storage provider that reads users from tblUser table";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        List<ProviderConfigProperty> props = new ArrayList<>();
        
        ProviderConfigProperty datasourceProp = new ProviderConfigProperty();
        datasourceProp.setName(DATASOURCE_JNDI);
        datasourceProp.setLabel("DataSource JNDI Name");
        datasourceProp.setHelpText("JNDI name of the DataSource containing tblUser table");
        datasourceProp.setType(ProviderConfigProperty.STRING_TYPE);
        datasourceProp.setDefaultValue(DATASOURCE_JNDI_DEFAULT);
        props.add(datasourceProp);
        
        return props;
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) 
            throws ComponentValidationException {
        String jndiName = config.getConfig().getFirst(DATASOURCE_JNDI);
        if (jndiName == null || jndiName.trim().isEmpty()) {
            throw new ComponentValidationException("DataSource JNDI name is required");
        }
        
        DataSource ds = lookupDataSource(jndiName);
        if (ds == null) {
            throw new ComponentValidationException("Could not find DataSource: " + jndiName);
        }
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }
}
