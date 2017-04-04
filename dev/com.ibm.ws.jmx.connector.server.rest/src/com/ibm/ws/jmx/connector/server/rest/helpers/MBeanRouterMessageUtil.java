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
package com.ibm.ws.jmx.connector.server.rest.helpers;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 *
 */
public class MBeanRouterMessageUtil {
    private static final ResourceBundle logMessages = ResourceBundle.getBundle("com.ibm.ws.jmx.connector.server.internal.resources.RouterServerMessages");

    // Router messages
    public static final String SSL_ERROR = "jmx.connector.server.rest.router.ssl.exception";
    public static final String SSL_NOT_CONFIGED = "jmx.connector.server.rest.router.ssl.keystore.error";
    public static final String REPOSITORY_JMXAUTH_NODE_DOES_NOT_EXIST = "jmx.connector.server.rest.router.jmxauth.node.not.found";
    public static final String REPOSITORY_JMXAUTH_HOSTNAME_DOES_NOT_EXIST = "jmx.connector.server.rest.router.jmxauth.hostname.not.found";
    public static final String REPOSITORY_JMXAUTH_PORT_DOES_NOT_EXIST = "jmx.connector.server.rest.router.jmxauth.port.not.found";
    public static final String COLLECTIVE_PLUGIN_NOT_AVAILABLE = "jmx.connector.server.rest.router.collectivePlugin.not.available";

    public static String getMessage(String messageName, Object... arguments) {
        if (arguments.length > 0)
            return MessageFormat.format(logMessages.getString(messageName), arguments);
        else
            return logMessages.getString(messageName);
    }

    public static String getObjID(Object obj) {
        return obj.getClass().getSimpleName() + "@" + System.identityHashCode(obj);
    }
}
