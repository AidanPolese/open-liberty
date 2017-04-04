/*
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 * @(#) 1.2 SERV1/ws/code/security.crypto/src/com/ibm/websphere/ssl/SSLConfigChangeListener.java, WAS.security.crypto, WASX.SERV1, pp0919.25 1/4/06 09:56:35 [5/15/09 18:04:31]
 *
 * Date         Defect        CMVC ID    Description
 *
 * 08/19/05     LIDB3557-1.1  pbirk      3557 Initial Code Drop
 *
 */
package com.ibm.websphere.ssl;

/**
 * <p>
 * This interface is for components and applications to receive notifications
 * of dynamic changes to the SSL configurations returned by the JSSEHelper API.
 * An SSLConfigChangeEvent will be sent with the previous SSL selection
 * information including the alias and type of selection (direct, dynamic).
 * It's up to the listener implementation to call JSSEHelper API
 * again if it's desired to dynamically refresh the SSL configuration.
 * </p>
 * 
 * @author IBM Corporation
 * @version 1.0
 * @since WAS 6.1
 * @see com.ibm.websphere.ssl.JSSEHelper
 * @ibm-api
 **/

public interface SSLConfigChangeListener {
    void stateChanged(SSLConfigChangeEvent e);
}
