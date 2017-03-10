/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.zos.logging;

import java.util.Hashtable;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.ibm.ws.logging.osgi.MessageRouterConfigListener;
import com.ibm.ws.zos.logging.internal.ZosLoggingConfigListener;

/**
 * Service that represents the <zosLogging> configuration element.
 * Updates to the config element are pushed to ZosLoggingConfigListener
 * and MessageRouterConfigListener services.
 */
@Component(name = "com.ibm.ws.zos.logging.config",
           service = ZosLoggingConfiguration.class,
           immediate = true,
           property = { "service.vendor = IBM" })
public class ZosLoggingConfiguration {

    /**
     * The latest values of the <zosLogging> config parameters.
     */
    private volatile Hashtable<String, Object> zosLoggingConfig = null;

    /**
     * Config listener used to pass updates to ZosLoggingBundleActivator.
     */
    private ZosLoggingConfigListener zosLoggingConfigListener;

    /**
     * Config listener used to pass updates to MessageRouterConfigurator.
     */
    private MessageRouterConfigListener msgRouterConfigListener;

    /**
     * Method used to register the ZosLoggingConfigListener.
     *
     * @param listener
     */
    @Reference(policyOption = ReferencePolicyOption.GREEDY,
               cardinality = ReferenceCardinality.OPTIONAL)
    protected void setZosLoggingConfigListener(ZosLoggingConfigListener listener) {
        this.zosLoggingConfigListener = listener;
        updateListeners();
    }

    /**
     * Method used to register the MessageRouterConfigListener.
     *
     * @param listener
     */
    @Reference(policyOption = ReferencePolicyOption.GREEDY,
               cardinality = ReferenceCardinality.OPTIONAL)
    protected void setMessageRouterConfigListener(MessageRouterConfigListener listener) {
        this.msgRouterConfigListener = listener;
        updateListeners();
    }

    /**
     * Activate and store initial config values, then update listener services.
     *
     * @param config
     */
    protected void activate(Map<String, Object> config) {
        zosLoggingConfig = new Hashtable<String, Object>(config);
        updateListeners();
    }

    /**
     * Deactivate by clearing saved config and listener references.
     *
     * @param config
     * @param reason
     */
    protected void deactivate(Map<String, Object> config, int reason) {
        zosLoggingConfig = null;
        msgRouterConfigListener = null;
        zosLoggingConfigListener = null;
    }

    /**
     * Store new config values and update listener services.
     *
     * @param config
     */
    protected void modified(Map<String, Object> config) {
        zosLoggingConfig = new Hashtable<String, Object>(config);
        updateListeners();
    }

    /**
     * Push the current config values to all listeners we have references to.
     */
    private void updateListeners() {
        if (zosLoggingConfig != null) {

            // Initialize enableLogToMVS to its default, false, because
            // autounboxing causes NPEs later down the line if it's not set.
            // It might not be set in cases where we tear down the server
            // early, for example, when an angel is required, but not connected.
            Object tmpLogMVS = zosLoggingConfig.get("enableLogToMVS");
            if (null == tmpLogMVS) {
                zosLoggingConfig.put("enableLogToMVS", false);
            }

            if (zosLoggingConfigListener != null) {
                zosLoggingConfigListener.updated(zosLoggingConfig);
            }

            if (msgRouterConfigListener != null) {
                String wtoMessages = (String) zosLoggingConfig.get("wtoMessage");
                if (wtoMessages != null) {
                    msgRouterConfigListener.updateMessageListForHandler(wtoMessages, "WTO");
                }

                String hardcopyMessages = (String) zosLoggingConfig.get("hardCopyMessage");
                if (hardcopyMessages != null) {
                    msgRouterConfigListener.updateMessageListForHandler(hardcopyMessages, "HARDCOPY");
                }
            }
        }
    }

}
