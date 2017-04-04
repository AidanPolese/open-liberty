/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.persistence.internal;

import static org.eclipse.persistence.config.PersistenceUnitProperties.CREATE_OR_EXTEND;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_ACTION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_DROP_ACTION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_DROP_AND_CREATE_ACTION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPT_TERMINATE_STATEMENTS;
import static org.eclipse.persistence.config.PersistenceUnitProperties.TARGET_DATABASE_PROPERTIES;
import static org.eclipse.persistence.config.PersistenceUnitProperties.TARGET_SERVER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.WEAVING;

import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.sessions.DatabaseSession;

import com.ibm.wsspi.persistence.PersistenceServiceUnit;
import com.ibm.wsspi.persistence.PersistenceServiceUnitConfig;
import com.ibm.wsspi.persistence.internal.eclipselink.PsPersistenceProvider;
import com.ibm.wsspi.persistence.internal.eclipselink.TargetServer;

/**
 * PersistenceServiceUnit implementation
 */
public final class PersistenceServiceUnitImpl implements PersistenceServiceUnit {

    static {
        PrivilegedAccessHelper.setDefaultUseDoPrivilegedValue(true);
    }

    private final PsPersistenceProvider _provider;

    private final EntityManagerFactory _emf;
    private final PUInfoImpl _pui;
    private final Map<String, String> _serviceProperties;
    private final DataSource _schemaDataSource;
    private final SchemaManager _schemaMgr;

    private TransactionManager _tranMgr = null;

    public PersistenceServiceUnitImpl(PersistenceServiceUnitConfig conf, PsPersistenceProvider provider,
                                      InMemoryUrlStreamHandler inmemHandler, DatabaseManager dbManager, URL bundleRootUrl) {

        _provider = provider;
        // Favor priv, non, then jta
        _schemaDataSource =
                        (conf.getPrivilegedDataSource() != null ? conf.getPrivilegedDataSource()
                                        : (conf.getNonJaDataSource() != null ? conf.getNonJaDataSource() : conf.getJtaDataSource()));
        _pui = new PUInfoImpl(conf, inmemHandler, bundleRootUrl);

        _serviceProperties = new HashMap<String, String>();

        _serviceProperties.put(WEAVING, "false");
        _serviceProperties.put(TARGET_SERVER, TargetServer.class.getName());
        _serviceProperties.put(SCHEMA_GENERATION_SCRIPT_TERMINATE_STATEMENTS, "true");

        // Remaps String -> NVARCHAR (or equivalent)
        _serviceProperties.put(TARGET_DATABASE_PROPERTIES, "UseNationalCharacterVaryingTypeForString=true");

        _emf = _provider.createContainerEntityManagerFactory(_pui, _serviceProperties);
        dbManager.processUnicodeSettings(_emf, conf);

        _schemaMgr = new SchemaManager(_serviceProperties, _pui, _provider, dbManager);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.persistence.PersistenceUnit#createEntityManager()
     */
    @Override
    public EntityManager createEntityManager() {
        // TODO(151457) -- could keep track of these -- wrapper - yes! pool
        return _emf.createEntityManager();
    }

    @Override
    public void dropAndCreateTables() {
        _schemaMgr.generateSchema(_tranMgr, SCHEMA_GENERATION_DATABASE_ACTION, SCHEMA_GENERATION_DROP_AND_CREATE_ACTION);
    }

    @Override
    public void createTables() {
        _schemaMgr.generateSchema(_tranMgr, SCHEMA_GENERATION_DATABASE_ACTION, CREATE_OR_EXTEND);
    }

    @Override
    public void dropTables() {
        _schemaMgr.generateSchema(_tranMgr, SCHEMA_GENERATION_DATABASE_ACTION, SCHEMA_GENERATION_DROP_ACTION);

    }

    @Override
    public void generateDDL(Writer out) {
        _schemaMgr.generateSchema(_tranMgr, SCHEMA_GENERATION_SCRIPTS_ACTION, SCHEMA_GENERATION_CREATE_ACTION,
                                  SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, out);
    }

    @Override
    public void close() {
        _pui.close();
        _emf.close();
    }

    @Override
    public String getDatabaseTerminationToken() {
        return _emf.unwrap(DatabaseSession.class).getPlatform().getStoredProcedureTerminationToken();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.persistence.internal.PersistenceServiceInternal#setTransactionManager(com.ibm.ws.tx.embeddable.EmbeddableWebSphereTransactionManager)
     */
    void setTransactionManager(TransactionManager tranMgr) {
        _tranMgr = tranMgr;
    }
}
