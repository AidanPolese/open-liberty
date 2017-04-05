/*
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 * @(#) 1.2 SERV1/ws/code/security.crypto/src/com/ibm/wsspi/ssl/KeyManagerExtendedInfo.java, WAS.security.crypto, WASX.SERV1, pp0919.25 1/4/06 10:06:58 [5/15/09 18:04:37]
 *
 * Date         Defect        CMVC ID    Description
 *
 * 08/19/05     LIDB3557-1.1  pbirk      3557 Initial Code Drop
 *
 */

package com.ibm.wsspi.ssl;

import java.security.KeyStore;
import java.util.Properties;

import javax.net.ssl.X509KeyManager;

/**
 * <p>
 * KeyManager Extended Info Interface. This interface is extended by custom Key
 * Managers which need information about the current SSL configuration to make
 * decisions about whether to change the key information.
 * </p>
 * 
 * @author IBM Corporation
 * @version 1.0
 * @since WAS 6.1
 * @see com.ibm.websphere.ssl.JSSEHelper
 * @ibm-spi
 **/
public interface KeyManagerExtendedInfo {
    /**
     * Method called by WebSphere Application Server runtime to set the custom
     * properties configured for the custom KeyManager.
     * 
     * @param customProperties
     * @ibm-spi
     */
    void setCustomProperties(Properties customProperties);

    /**
     * Method called by WebSphere Application Server runtime to set the SSL
     * configuration properties being used for this connection.
     * 
     * @param config
     *            - contains a property for the SSL configuration.
     * @ibm-spi
     */
    void setSSLConfig(Properties config);

    /**
     * Method called by WebSphere Application Server runtime to set the default
     * X509KeyManager created by the IbmX509 KeyManagerFactory using the KeyStore
     * information present in this SSL configuration. This allows some delegation
     * to the default IbmX509 KeyManager to occur.
     * 
     * @param defaultX509KeyManager
     *            - default IbmX509 key manager for delegation
     * @ibm-spi
     */
    void setDefaultX509KeyManager(X509KeyManager defaultX509KeyManager);

    /**
     * Method called by WebSphere Application Server runtime to set the SSL
     * KeyStore used for this connection.
     * 
     * @param keyStore
     *            - the KeyStore currently configured
     * @ibm-spi
     */
    void setKeyStore(KeyStore keyStore);

    /**
     * Method called by WebSphere Application Server runtime to set the SSL
     * KeyStore certificate alias configured for use by server configurations.
     * This method is only called when the alias is configured using the
     * com.ibm.ssl.keyStoreServerAlias property.
     * 
     * @param serverAlias
     *            - the KeyStore server certificate alias currently configured
     * @ibm-spi
     */
    void setKeyStoreServerAlias(String serverAlias);

    /**
     * Method called by WebSphere Application Server runtime to set the SSL
     * KeyStore certificate alias configured for use by client configurations.
     * This method is only called when the alias is configured using the
     * com.ibm.ssl.keyStoreClientAlias property.
     * 
     * @param clientAlias
     *            - the KeyStore client certificate alias currently configured
     * @ibm-spi
     */
    void setKeyStoreClientAlias(String clientAlias);

}
