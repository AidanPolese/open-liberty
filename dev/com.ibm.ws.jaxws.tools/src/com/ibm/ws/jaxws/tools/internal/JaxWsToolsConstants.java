/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxws.tools.internal;

import java.util.ResourceBundle;

public class JaxWsToolsConstants {
    public static final String PARAM_HELP = "-help";

    public static final String PARAM_VERSION = "-version";

    public static final String PARAM_TARGET = "-target";

    public static final String TR_GROUP = "JaxwsTools";

    public static final String TR_RESOURCE_BUNDLE = "com.ibm.ws.jaxws.tools.internal.resources.JaxWsToolsMessages";

    public static final String ERROR_PARAMETER_TARGET_MISSED_KEY = "error.parameter.target.missed";

    public static final ResourceBundle messages = ResourceBundle.getBundle(TR_RESOURCE_BUNDLE);
}
