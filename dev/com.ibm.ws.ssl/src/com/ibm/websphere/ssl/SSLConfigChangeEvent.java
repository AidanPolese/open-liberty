/*
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 * @(#) 1.4 SERV1/ws/code/security.crypto/src/com/ibm/websphere/ssl/SSLConfigChangeEvent.java, WAS.security.crypto, WASX.SERV1, pp0919.25 1/4/06 09:56:31 [5/15/09 18:04:31]
 *
 * Date         Defect        CMVC ID    Description
 *
 * 08/19/05     LIDB3557-1.1  pbirk      3557 Initial Code Drop
 *
 */
package com.ibm.websphere.ssl;

import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * This class is the event received by the SSLConfigChanceListener which
 * components and applications register with the JSSEHelper API.
 * An SSLConfigChangeEvent will be sent with old and new SSL alias. If the
 * old and new SSL alias is the same, then the properties within it changed.
 * It's up to the listener implementation to call JSSEHelper API again if it's
 * desired to dynamically refresh the Properties, SSLContext, SSL socket
 * factories, and/or HTTPS URLStreamHandlers.
 * </p>
 * 
 * @author IBM Corporation
 * @version 1.0
 * @since WAS 6.1
 * @see com.ibm.websphere.ssl.JSSEHelper
 * @ibm-api
 **/
public class SSLConfigChangeEvent {
    private String selectionType;
    private String sslAlias;
    private String sslState;
    private Properties originalSSLConfig;
    private Properties changedSSLConfig;
    private Map<String, Object> connectionInfo;

    /**
     * Constructor.
     * 
     * @param alias
     * @param config
     * @param selection
     * @param connInfo
     */
    public SSLConfigChangeEvent(String alias, Properties config, String selection, Map<String, Object> connInfo) {
        this.sslAlias = alias;
        this.originalSSLConfig = config;
        this.selectionType = selection;
        this.connectionInfo = connInfo;
    }

    /***
     * <p>
     * Returns the selection type made by JSSEHelper. This could be "direct" or
     * "dynamic". Refer to the JSSEHelper API JavaDoc for
     * information on these selection types.
     * </p>
     * 
     * @return String
     ***/
    public String getSelectionType() {
        return this.selectionType;
    }

    /***
     * <p>
     * Returns the connection info Map which was passed into the
     * JSSEHelper API, if present. This is an optional parameter so null
     * may be returned by this API. See JSSEHelper API for connection
     * info properties.
     * </p>
     * 
     * @return Map<String,Object - the connection info used by JSSEHelper.
     ***/
    public Map<String, Object> getConnectionInfo() {
        return this.connectionInfo;
    }

    /***
     * <p>
     * Returns the SSL alias used in the previous call to the JSSEHelper API.
     * </p>
     * 
     * @return String
     ***/
    public String getAlias() {
        return this.sslAlias;
    }

    /***
     * <p>
     * Returns the certificate alias used for outbound connections by the
     * SSL configuration returned from the JSSEHelper API.
     * </p>
     * 
     * @return String
     ***/
    public String getClientCertificateAlias() {
        if (getOriginalSSLConfig() != null)
            return getOriginalSSLConfig().getProperty(Constants.SSLPROP_KEY_STORE_CLIENT_ALIAS);

        return null;
    }

    /***
     * <p>
     * Returns the certificate alias used for inbound connections by the
     * SSL configuration returned from the JSSEHelper API.
     * </p>
     * 
     * @return String
     ***/
    public String getServerCertificateAlias() {
        if (getOriginalSSLConfig() != null)
            return getOriginalSSLConfig().getProperty(Constants.SSLPROP_KEY_STORE_SERVER_ALIAS);

        return null;
    }

    /***
     * <p>
     * Returns the state of the SSL configuration. This will be either
     * "changed" or "deleted". If the SSL configuration is deleted and
     * direct specification was the selection type, then a new alias will
     * need to be selected or group/dynamic selection should be used. If the
     * state is "deleted", this listener will be automatically de-registered
     * since the configuration has gone away.
     * </p>
     * 
     * @return String
     ***/
    public String getState() {
        return this.sslState;
    }

    /***
     * <p>
     * Return the SSL properties which this listener originally registered.
     * </p>
     * 
     * @return Properties
     ***/
    public Properties getOriginalSSLConfig() {
        return this.originalSSLConfig;
    }

    /***
     * <p>
     * Return the SSL properties effective after the most recent change. This
     * will be null if the getState() method returns "deleted".
     * </p>
     * 
     * @return Properties
     ***/
    public Properties getChangedSSLConfig() {
        return this.changedSSLConfig;
    }

    /***
     * <p>
     * Allows the runtime to set the new Properties after the change took place.
     * </p>
     * 
     * @param config
     ***/
    public void setChangedSSLConfig(Properties config) {
        this.changedSSLConfig = config;
    }

    /***
     * <p>
     * Allows the runtime to set the state after the change took place.
     * </p>
     * 
     * @param state
     ***/
    public void setState(String state) {
        this.sslState = state;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SSLConfigChangeEvent: selectionType=").append(this.selectionType);
        sb.append(", sslAlias=").append(this.sslAlias);
        sb.append(", sslState=").append(this.sslState);
        return sb.toString();
    }
}
