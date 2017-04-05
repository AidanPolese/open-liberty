/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2009
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.ssl.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.net.ssl.SSLSocketFactory;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ssl.Constants;
import com.ibm.websphere.ssl.JSSEHelper;
import com.ibm.websphere.ssl.JSSEProvider;
import com.ibm.websphere.ssl.SSLException;
import com.ibm.ws.kernel.feature.FeatureProvisioner;
import com.ibm.ws.ssl.JSSEProviderFactory;
import com.ibm.ws.ssl.config.KeyStoreManager;
import com.ibm.ws.ssl.config.SSLConfigManager;
import com.ibm.ws.ssl.config.WSKeyStore;
import com.ibm.ws.ssl.optional.SSLSupportOptional;
import com.ibm.ws.ssl.protocol.LibertySSLSocketFactory;
import com.ibm.wsspi.kernel.service.location.WsLocationAdmin;
import com.ibm.wsspi.kernel.service.location.WsLocationConstants;

/**
 * Component for the SSL configuration bundle.
 */
@Component(immediate = true,
           configurationPid = "com.ibm.ws.ssl.default",
           configurationPolicy = ConfigurationPolicy.REQUIRE,
           property = "service.vendor=IBM",
           xmlns = "http://felix.apache.org/xmlns/scr/v1.2.0-felix")
public class SSLComponent extends GenericSSLConfigService implements SSLSupportOptional {

    private static final TraceComponent tc = Tr.register(SSLComponent.class);

    protected static final String MY_ALIAS = "sslDefault";

    private final Map<String, RepertoireConfigService> repertoireMap = new HashMap<String, RepertoireConfigService>();
    private final Map<String, String> repertoirePIDMap = new HashMap<String, String>();
    private final Map<String, Map<String, Object>> repertoirePropertiesMap = new HashMap<String, Map<String, Object>>();
    private final Map<String, WSKeyStore> keystoreIdMap = new HashMap<String, WSKeyStore>();
    private final Map<String, WSKeyStore> keystorePidMap = new HashMap<String, WSKeyStore>();
    private volatile WsLocationAdmin locSvc;

    private boolean activated;
    private FeatureProvisioner provisionerService;
    private boolean transportSecurityEnabled;

    /**
     * DS method to activate this component.
     *
     * @param context
     * @param properties
     */
    @Activate
    protected synchronized Map<String, Object> activate(Map<String, Object> properties) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "Activated: " + properties);
        }
        Set<String> installedFeatures = provisionerService.getInstalledFeatures();
        if (installedFeatures.contains("transportSecurity-1.0")) {
            transportSecurityEnabled = true;
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                Tr.event(tc, "transportSecurityEnable installed");
            }
        } else {
            transportSecurityEnabled = false;
            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                Tr.event(tc, "transportSecurityEnable is not installed");
            }
        }

        super.activate(MY_ALIAS, properties);

        activated = true;

        Map<String, Object> ret = processConfig(true);
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "activate return: " + ret);
        }
        return ret;
    }

    /**
     * DS method to deactivate this component.
     *
     * @param context
     */
    @Deactivate
    protected synchronized Map<String, Object> deactivate(int reason) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "Deactivated: " + reason);
        }

        super.deactivate(MY_ALIAS, reason);

        repertoireMap.clear();
        repertoirePIDMap.clear();
        keystoreIdMap.clear();
        keystorePidMap.clear();
        Map<String, Object> props = processConfig(true);
        activated = false;
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "deactivate return: " + props);
        }
        return props;
    }

    @Modified
    protected synchronized Map<String, Object> modified(Map<String, Object> properties) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "Modified: " + properties);
        }
        super.modified(MY_ALIAS, properties);

        Map<String, Object> ret = processConfig(true);
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "modified return: " + ret);
        }
        return ret;
    }

    /**
     * Method will be called for each keystore that is registered
     * in the OSGi service registry. We maintain an internal map of these for
     * easy access.
     *
     * @param config KeystoreConfig
     */
    //TODO bug in bnd requires setting service in @Reference annotation
    @Reference(service = KeystoreConfig.class, cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, target = "(id=*)")
    protected synchronized Map<String, Object> setKeyStore(KeystoreConfig config) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "Adding keystore: " + config.getId());
        }
        Map<String, Object> ret = addKeyStores(false, config);
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "setKeyStore return: " + ret);
        }
        return ret;
    }

    protected synchronized void updatedKeyStore(KeystoreConfig config) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "Updating keystore: " + config.getId());
        }
        addKeyStores(false, config);
    }

    /**
     * Method will be called for each KeyStoreConfigService that is unregistered
     * in the OSGi service registry. We must remove this instance from our
     * internal map.
     *
     * @param ref Reference to an unregistered KeyStoreConfigService
     */
    protected synchronized Map<String, Object> unsetKeyStore(KeystoreConfig config) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "Removing keystore: " + config.getId());
        }
        keystoreIdMap.remove(config.getId());
        keystorePidMap.remove(config.getPid());
        KeyStoreManager.getInstance().clearKeyStoreFromMap(config.getId());
        for (Iterator<Map.Entry<String, RepertoireConfigService>> it = repertoireMap.entrySet().iterator(); it.hasNext();) {

            RepertoireConfigService rep = it.next().getValue();
            if (rep.getKeyStore() == config || rep.getTrustStore() == config) {
                it.remove();
                repertoirePropertiesMap.remove(rep.getAlias());
                repertoirePIDMap.remove(rep.getPID());
            }
        }
        Map<String, Object> ret = processConfig(true);
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "unsetKeyStore return: " + ret);
        }
        return ret;
    }

    private Map<String, Object> addKeyStores(boolean updateSSLConfigManager, KeystoreConfig... keystores) {
        for (KeystoreConfig config : keystores) {
            WSKeyStore keystore = config.getKeyStore();
            if (keystore != keystoreIdMap.put(config.getId(), keystore)) {
                updateSSLConfigManager = true;
                keystorePidMap.put(config.getPid(), keystore);
            }
        }
        return processConfig(updateSSLConfigManager);
    }

    /**
     * Method will be called for each repertoire that is registered
     * in the OSGi service registry. We maintain an internal map of these for
     * easy access.
     *
     * @param config RepertoireConfigService
     */
    //TODO bug in bnd requires setting service
    @Reference(service = RepertoireConfigService.class, cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, target = "(id=*)")
    protected synchronized Map<String, Object> setRepertoire(RepertoireConfigService config) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "Adding repertoire: " + config.getAlias());
        }
        Map<String, Object> properties = config.getProperties();
        repertoireMap.put(config.getAlias(), config);
        repertoirePIDMap.put(config.getPID(), config.getAlias());
        repertoirePropertiesMap.put(config.getAlias(), properties);
        Map<String, Object> ret = addKeyStores(true, config.getKeyStore(), config.getTrustStore());
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "setRepertoire return: " + ret);
        }
        return ret;
    }

    protected synchronized Map<String, Object> updatedRepertoire(RepertoireConfigService config) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "Updating repertoire: " + config.getAlias());
        }
        Map<String, Object> properties = config.getProperties();
        repertoirePropertiesMap.put((String) properties.get(LibertyConstants.KEY_ID), properties);
        repertoirePIDMap.put(config.getPID(), config.getAlias());
        Map<String, Object> ret = addKeyStores(true, config.getKeyStore(), config.getTrustStore());
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "updatedRepertoire return: " + ret);
        }
        return ret;
    }

    /**
     * Method will be called for each RepertoireConfigService that is unregistered
     * in the OSGi service registry. We must remove this instance from our
     * internal map.
     *
     * @param config RepertoireConfigService
     */
    protected synchronized Map<String, Object> unsetRepertoire(RepertoireConfigService config) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "Removing repertoire: " + config.getAlias());
        }
        repertoireMap.remove(config.getAlias());
        repertoirePIDMap.remove(config.getPID());
        repertoirePropertiesMap.remove(config.getAlias());
        Map<String, Object> ret = processConfig(repertoirePropertiesMap.remove(config.getAlias()) != null);
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "unsetRepertoire return: " + ret);
        }
        return ret;
    }

    /**
     * Set the reference to the location manager.
     * Dynamic service: always use the most recent.
     *
     * @param locSvc Location service
     */
    @Reference(policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    protected void setLocMgr(WsLocationAdmin locSvc) {
        this.locSvc = locSvc;
    }

    /**
     * Remove the reference to the location manager:
     * required service, do nothing.
     */
    protected void unsetLocMgr(ServiceReference<WsLocationAdmin> ref) {}

    /**
     * Set the reference to the location manager.
     * Dynamic service: always use the most recent.
     *
     * @param locSvc Location service
     */

    @Reference(service = FeatureProvisioner.class)
    protected synchronized void setKernelProvisioner(FeatureProvisioner provisionerService) {
        this.provisionerService = provisionerService;
    }

    protected synchronized void unsetKernelProvisioner(FeatureProvisioner provisionerService) {
        transportSecurityEnabled = false;
        this.provisionerService = null;
    }

    /**
     * Process configuration information.
     *
     * @param properties
     */
    private Map<String, Object> processConfig(boolean updateSSLConfigManager) {
        if (!activated) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Not yet activated, can not process config");
            }
            return null;
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "Processing configuration");
        }

        boolean isServer = locSvc.resolveString(WsLocationConstants.SYMBOL_PROCESS_TYPE).equals(WsLocationConstants.LOC_PROCESS_TYPE_SERVER);

        Map<String, Object> serviceProps = new HashMap<String, Object>(config);
        serviceProps.put(REPERTOIRE_IDS, repertoireMap.keySet().toArray(new String[repertoireMap.size()]));
        serviceProps.put(KEYSTORE_IDS, keystoreIdMap.keySet().toArray(new String[keystoreIdMap.size()]));
        serviceProps.put(REPERTOIRE_PIDS, repertoirePIDMap.keySet().toArray(new String[repertoirePIDMap.size()]));
        if (updateSSLConfigManager) {
            try {
                // pass reinitialize=true to redo config
                SSLConfigManager.getInstance().initializeSSL(getGlobalProps(),
                                                             getRepertoireProps(),
                                                             getKeyStores(),
                                                             true,
                                                             isServer,
                                                             transportSecurityEnabled,
                                                             repertoirePIDMap);
            } catch (SSLException e) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                    Tr.event(tc, "Exception processing SSL configuration; " + e);
                }
            }
        }
        if (!repertoireMap.isEmpty() && !keystoreIdMap.isEmpty()) {
            serviceProps.put("SSLSupport", "active");
        }
        return serviceProps;
    }

    /**
     * Protected so it can be called via unit test.
     *
     * @return Map<String, String> filled with global properties from ssl config instance
     */
    Map<String, Object> getGlobalProps() {
        Map<String, Object> props = getProperties();

        String repertoire = (String) props.get(LibertyConstants.KEY_DEFAULT_REPERTOIRE);
        if (repertoire != null) {
            props.put(Constants.SSLPROP_DEFAULT_ALIAS, repertoire);
        } else {
            props.put(Constants.SSLPROP_DEFAULT_ALIAS, LibertyConstants.DEFAULT_SSL_CONFIG_ID);
        }

        String outBoundDefault = (String) props.get(LibertyConstants.KEY_OUTBOUND_DEFAULT_REPERTOIRE);
        if (outBoundDefault != null) {
            props.put(LibertyConstants.SSLPROP_OUTBOUND_DEFAULT_ALIAS, outBoundDefault);
        }

        return props;
    }

    /**
     * Protected so it can be called via unit test. The values in repertoirePropertiesMap are not changed
     * and this method is always called inside a synchronized block or from a unit test.
     *
     * @return Map<String, Map<String, String>> filled with collected repertoire properties
     */
    Map<String, Map<String, Object>> getRepertoireProps() {
        return repertoirePropertiesMap;
    }

    /**
     * Protected so it can be called via unit test.
     *
     * @return
     */
    Map<String, WSKeyStore> getKeyStores() {
        Map<String, WSKeyStore> ret = new HashMap<String, WSKeyStore>(keystoreIdMap);
        ret.putAll(keystorePidMap);
        return ret;
    }

    // You might ask, why is this synchronized?
    // Primarily, this is an attempt to keep SSL operations orderly.
    // Currently there is insufficient locking to ensure that operations
    // do not come in while processConfig is underway.
    @Override
    public synchronized JSSEHelper getJSSEHelper() {
        return JSSEHelper.getInstance();
    }

    @Override
    public JSSEProvider getJSSEProvider() {
        return JSSEProviderFactory.getInstance();
    }

    @Override
    public JSSEProvider getJSSEProvider(String providerName) {
        return JSSEProviderFactory.getInstance(providerName);
    }

    @Override
    public SSLSocketFactory getSSLSocketFactory() {
        return new LibertySSLSocketFactory();
    }

    @Override
    public SSLSocketFactory getSSLSocketFactory(String sslAlias) throws javax.net.ssl.SSLException {
        if (sslAlias != null)
            return new LibertySSLSocketFactory(sslAlias);
        return new LibertySSLSocketFactory();
    }

    @Override
    public SSLSocketFactory getSSLSocketFactory(Properties sslProps) throws javax.net.ssl.SSLException {
        if ((sslProps != null && sslProps.isEmpty()) || sslProps == null)
            return new LibertySSLSocketFactory();
        return new LibertySSLSocketFactory(sslProps);
    }

}
