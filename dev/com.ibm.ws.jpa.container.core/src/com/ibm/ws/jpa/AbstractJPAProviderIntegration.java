/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014,2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.jpa;

import static com.ibm.ws.jpa.management.JPAConstants.JPA_RESOURCE_BUNDLE_NAME;
import static com.ibm.ws.jpa.management.JPAConstants.JPA_TRACE_GROUP;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.persistence.spi.PersistenceUnitInfo;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.container.service.app.deploy.ModuleInfo;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;

/**
 * Common JPAProviderIntegration implementation that provides general integration required for known
 * JPA providers.
 */
public abstract class AbstractJPAProviderIntegration implements JPAProviderIntegration {
    private static final TraceComponent tc = Tr.register(AbstractJPAProviderIntegration.class, JPA_TRACE_GROUP, JPA_RESOURCE_BUNDLE_NAME);

    // Known JPA provider implementation classes
    protected static final String PROVIDER_ECLIPSELINK = "org.eclipse.persistence.jpa.PersistenceProvider";
    protected static final String PROVIDER_HIBERNATE = "org.hibernate.jpa.HibernatePersistenceProvider";
    protected static final String PROVIDER_OPENJPA = "org.apache.openjpa.persistence.PersistenceProviderImpl";

    /**
     * As persistence providers are first used, they are added to this list so that version information is only logged once for them.
     */
    protected final ConcurrentSkipListSet<String> providersUsed = new ConcurrentSkipListSet<String>();

    /**
     * @see com.ibm.ws.jpa.JPAProviderIntegration#disablePersistenceUnitLogging(java.util.Map)
     */
    @Override
    public void disablePersistenceUnitLogging(Map<String, Object> integrationProperties) {
        integrationProperties.put("eclipselink.logging.level", "OFF");
        // Since we're disabling logging, we don't want this to conflict with other normally configured PUs. Give it a random
        // session-name such that when/if another EMF is created for this PU the EclipseLink JPAInitializer cache
        integrationProperties.put("eclipselink.session-name", // value of PersistenceUnitProperties.SESSION_NAME
                                  "disabled-logging-pu" + UUID.randomUUID().toString());
    }

    /**
     * Log version information about the specified persistence provider, if it can be determined.
     *
     * @param providerName fully qualified class name of JPA persistence provider
     * @param loader class loader with access to the JPA provider classes
     */
    @FFDCIgnore(Exception.class)
    private void logProviderInfo(String providerName, ClassLoader loader) {
        try {
            if (PROVIDER_ECLIPSELINK.equals(providerName)) {
                // org.eclipse.persistence.Version.getVersion(): 2.6.4.v20160829-44060b6
                Class<?> Version = loadClass(loader, "org.eclipse.persistence.Version");
                String version = (String) Version.getMethod("getVersionString").invoke(Version.newInstance());
                Tr.info(tc, "JPA_THIRD_PARTY_PROV_INFO_CWWJP0053I", "EclipseLink", version);
            } else if (PROVIDER_HIBERNATE.equals(providerName)) {
                // org.hibernate.Version.getVersionString(): 5.2.6.Final
                Class<?> Version = loadClass(loader, "org.hibernate.Version");
                String version = (String) Version.getMethod("getVersionString").invoke(null);
                Tr.info(tc, "JPA_THIRD_PARTY_PROV_INFO_CWWJP0053I", "Hibernate", version);
            } else if (PROVIDER_OPENJPA.equals(providerName)) {
                // OpenJPAVersion.appendOpenJPABanner(sb): OpenJPA #.#.#\n version id: openjpa-#.#.#-r# \n Apache svn revision: #
                StringBuilder version = new StringBuilder();
                Class<?> OpenJPAVersion = loadClass(loader, "org.apache.openjpa.conf.OpenJPAVersion");
                OpenJPAVersion.getMethod("appendOpenJPABanner", StringBuilder.class).invoke(OpenJPAVersion.newInstance(), version);
                Tr.info(tc, "JPA_THIRD_PARTY_PROV_INFO_CWWJP0053I", "OpenJPA", version);
            } else {
                Tr.info(tc, "JPA_THIRD_PARTY_PROV_NAME_CWWJP0052I", providerName);
            }
        } catch (Exception x) {
            Tr.info(tc, "JPA_THIRD_PARTY_PROV_NAME_CWWJP0052I", providerName);
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(this, tc, "unable to determine provider info", x);
        }
    }

    @FFDCIgnore(PrivilegedActionException.class)
    private static Class<?> loadClass(final ClassLoader cl, final String className) throws ClassNotFoundException {
        if (System.getSecurityManager() == null)
            return cl.loadClass(className);
        else
            try {
                return AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>() {
                    @Override
                    public Class<?> run() throws ClassNotFoundException {
                        return cl.loadClass(className);
                    }
                });
            } catch (PrivilegedActionException e) {
                if (e.getCause() instanceof ClassNotFoundException)
                    throw (ClassNotFoundException) e.getCause();
                else
                    throw new RuntimeException(e);
            }
    }

    @Override
    public void moduleStarting(ModuleInfo moduleInfo) {}

    @Override
    public void moduleStarted(ModuleInfo moduleInfo) {}

    @Override
    public void moduleStopping(ModuleInfo moduleInfo) {}

    @Override
    public void moduleStopped(ModuleInfo moduleInfo) {}

    /**
     * @see com.ibm.ws.jpa.JPAProviderIntegration#supportsEntityManagerPooling()
     */
    @Override
    public boolean supportsEntityManagerPooling() {
        return false;
    }

    /**
     * @see com.ibm.ws.jpa.JPAProvider#addIntegrationProperties(java.util.Properties)
     */
    @FFDCIgnore(ClassNotFoundException.class)
    @Override
    public void updatePersistenceProviderIntegrationProperties(PersistenceUnitInfo puInfo, java.util.Map<String, Object> props) {
        String providerName = puInfo.getPersistenceProviderClassName();
        if (PROVIDER_ECLIPSELINK.equals(providerName)) {
            props.put("eclipselink.target-server", "WebSphere_Liberty");
            if (puInfo instanceof com.ibm.ws.jpa.management.JPAPUnitInfo)
                props.put("eclipselink.application-id", ((com.ibm.ws.jpa.management.JPAPUnitInfo) puInfo).getApplName());
        }

        // Log third party provider name and version info once per provider
        if (providersUsed.add(providerName))
            logProviderInfo(providerName, puInfo.getClassLoader());
    };

    /**
     * @see com.ibm.ws.jpa.JPAProvider#modifyPersistenceUnitProperties(java.lang.String, java.util.Properties)
     */
    @Override
    public void updatePersistenceUnitProperties(String providerClassName, Properties props) {}
}
