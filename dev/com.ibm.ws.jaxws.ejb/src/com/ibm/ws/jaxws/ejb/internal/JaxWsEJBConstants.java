/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxws.ejb.internal;

import java.util.ResourceBundle;

public class JaxWsEJBConstants {
    public static final String TR_GROUP = "JaxWsEJB";

    public static final String TR_RESOURCE_BUNDLE = "com.ibm.ws.jaxws.ejb.internal.resources.JaxWsEJBMessages";

    public static final ResourceBundle messages = ResourceBundle.getBundle(TR_RESOURCE_BUNDLE);

    /**
     * This constant represents the parameter name used to store EJB instances on an Exchange
     */
    public static final String EJB_INSTANCE = "com.ibm.ws.jaxws.EXCHANGE_EJBINSTANCE";

    /**
     * This constant represents the parameter name used to store WSEJBEndpointManager instances on an Exchange
     */
    public static final String WS_EJB_ENDPOINT_MANAGER = "com.ibm.ws.jaxws.EXCHANGE_WSEJBENDPOINTMANAGER";

}
