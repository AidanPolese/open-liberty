/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxws.security.internal;

import java.util.ResourceBundle;

import com.ibm.websphere.ssl.Constants;

public class JaxWsSecurityConstants {
    public static final String TR_GROUP = "JaxWsSecurity";

    public static final String TR_RESOURCE_BUNDLE = "com.ibm.ws.jaxws.security.internal.resources.JaxWsSecurityMessages";

    public static final ResourceBundle messages = ResourceBundle.getBundle(TR_RESOURCE_BUNDLE);

    public static final String SERVER_DEFAULT_SSL_CONFIG_ALIAS = "defaultSSLConfig";

    public static final String CLIENT_KEY_STORE_ALIAS = Constants.SSLPROP_KEY_STORE_CLIENT_ALIAS;
}
