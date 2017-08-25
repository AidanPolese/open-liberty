/*
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.mp.jwt.impl;

import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.security.mp.jwt.MicroProfileJwtService;
import com.ibm.ws.security.mp.jwt.TraceConstants;
import com.ibm.ws.security.mp.jwt.error.MpJwtProcessingException;
import com.ibm.ws.ssl.KeyStoreService;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;
import com.ibm.wsspi.ssl.SSLSupport;

public class MicroProfileJwtServiceImpl implements MicroProfileJwtService {
    public static final TraceComponent tc = Tr.register(MicroProfileJwtServiceImpl.class,
            TraceConstants.TRACE_GROUP,
            TraceConstants.MESSAGE_BUNDLE);

    static final String CONFIGURATION_ADMIN = "configurationAdmin";
    static final String KEY_UNIQUE_ID = "id";

    private final String uniqueId = "SocialLoginService";

    private ConfigurationAdmin configAdmin = null;

    public static final String KEY_SSL_SUPPORT = "sslSupport";
    protected AtomicServiceReference<SSLSupport> sslSupportRef = new AtomicServiceReference<SSLSupport>(KEY_SSL_SUPPORT);
    public static final String KEY_KEYSTORE_SERVICE = "keyStoreService";
    private final AtomicServiceReference<KeyStoreService> keyStoreServiceRef = new AtomicServiceReference<KeyStoreService>(KEY_KEYSTORE_SERVICE);

    SSLSupport sslSupport = null;

    protected void setConfigurationAdmin(ConfigurationAdmin configAdmin) {
        this.configAdmin = configAdmin;
    }

    protected void updateConfigurationAdmin(ConfigurationAdmin configAdmin) {
        this.configAdmin = configAdmin;
    }

    protected void unsetConfigurationAdmin(ServiceReference<ConfigurationAdmin> ref) {
        this.configAdmin = null;
    }

    protected void setSslSupport(ServiceReference<SSLSupport> ref) {
        sslSupportRef.setReference(ref);
        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "setSslSupport service.pid:" + ref.getProperty("service.pid"));
        }
    }

    protected void updatedSslSupport(ServiceReference<SSLSupport> ref) {
        sslSupportRef.setReference(ref);
        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "updatedtSslSupport service.pid:" + ref.getProperty("service.pid"));
        }
    }

    protected void unsetSslSupport(ServiceReference<SSLSupport> ref) {
        sslSupportRef.unsetReference(ref);
        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "unsetSslSupport service.pid:" + ref.getProperty("service.pid"));
        }
    }

    protected void setKeyStoreService(ServiceReference<KeyStoreService> ref) {
        keyStoreServiceRef.setReference(ref);
    }

    protected void unsetKeyStoreService(ServiceReference<KeyStoreService> ref) {
        keyStoreServiceRef.unsetReference(ref);
    }

    @Activate
    protected void activate(ComponentContext cc, Map<String, Object> props) throws MpJwtProcessingException {
        sslSupportRef.activate(cc);
        keyStoreServiceRef.activate(cc);
        this.sslSupport = sslSupportRef.getService();
        Tr.info(tc, "MPJWT_CONFIG_PROCESSED", uniqueId);
    }

    @Modified
    protected void modified(ComponentContext cc, Map<String, Object> props) throws MpJwtProcessingException {
        this.sslSupport = sslSupportRef.getService();
        Tr.info(tc, "MPJWT_CONFIG_MODIFIED", uniqueId);
    }

    @Deactivate
    protected void deactivate(ComponentContext cc) {
        sslSupportRef.deactivate(cc);
        keyStoreServiceRef.deactivate(cc);
        Tr.info(tc, "MPJWT_CONFIG_DEACTIVATED", uniqueId);
    }

    // This method is for unittesting.
    ConfigurationAdmin getConfigurationAdmin() {
        return configAdmin;
    }

    /**
     * @return the sslSupportRef
     */
    @Override
    public AtomicServiceReference<SSLSupport> getSslSupportRef() {
        return sslSupportRef;
    }

    /**
     * @return the configAdmin
     */
    @Override
    public ConfigurationAdmin getConfigAdmin() {
        return configAdmin;
    }

    /**
     * @return the sslSupport
     */
    @Override
    public SSLSupport getSslSupport() {
        return sslSupport;
    }

    /**
     * @return the keyStoreServiceRef
     */
    @Override
    public AtomicServiceReference<KeyStoreService> getKeyStoreServiceRef() {
        return keyStoreServiceRef;
    }

}
