/*
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 * @(#) 1.3 SERV1/ws/code/security.crypto/src/com/ibm/wsspi/ssl/TrustManagerExtendedInfo.java, WAS.security.crypto, WASX.SERV1, pp0919.25 1/4/06 10:07:02 [5/15/09 18:04:37]
 *
 * Date         Defect        CMVC ID    Description
 *
 * 08/19/05     LIDB3557-1.1  pbirk      3557 Initial Code Drop
 *
 */

package com.ibm.wsspi.ssl;

import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * TrustManager Extended Info Interface. This interface is extended by custom
 * Trust Managers which need information about the remote connection information
 * to make decisions about whether to trust the remote connection.
 * </p>
 * 
 * @author IBM Corporation
 * @version 1.0
 * @since WAS 6.1
 * @see com.ibm.websphere.ssl.JSSEHelper
 * @ibm-spi
 **/

public interface TrustManagerExtendedInfo {
    /**
     * Method called by WebSphere Application Server runtime to set the custom
     * properties configured for the custom TrustManager.
     * 
     * @param customProperties
     * @ibm-spi
     */
    void setCustomProperties(Properties customProperties);

    /**
     * Method called by WebSphere Application Server runtime to set the target
     * host information and potentially other connection info in the future.
     * 
     * @param info
     *            - contains information about the target connection.
     * @ibm-spi
     */
    void setExtendedInfo(Map<String, Object> info);

    /**
     * Method called by WebSphere Application Server runtime to set the SSL
     * configuration properties being used for this connection.
     * 
     * @param config
     *            - contains a property for the SSL configuration.
     * @ibm-spi
     */
    void setSSLConfig(Properties config);

}
