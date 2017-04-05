/*
 * @start_prolog@
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 * 
 * Change activity:
 * 
 * Issue       Date        Name        Description
 * ----------- ----------- --------    ------------------------------------
 */
package com.ibm.ws.app.manager.internal;

import com.ibm.wsspi.kernel.service.location.WsLocationConstants;

public interface AppManagerConstants {

    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String LOCATION = "location";
    public static final String AUTO_START = "autoStart";
    public static final String TRACE_GROUP = "app.manager";
    public static final String TRACE_MESSAGES = "com.ibm.ws.app.manager.internal.resources.AppManagerMessages";
    public static final String APPLICATIONS_PID = "com.ibm.ws.app.manager";
    public static final String MANAGEMENT_PID = "com.ibm.ws.app.management";
    public static final String MONITOR_PID = "com.ibm.ws.app.manager.monitor";
    public static final String APPLICATION_FACTORY_FILTER = "(service.factoryPid=" + APPLICATIONS_PID + ")";
    public static final String SERVER_APPS_DIR = WsLocationConstants.SYMBOL_SERVER_CONFIG_DIR + "apps/";
    public static final String EXPANDED_APPS_DIR = SERVER_APPS_DIR + "expanded/";
    public static final String SHARED_APPS_DIR = WsLocationConstants.SYMBOL_SHARED_APPS_DIR;
    public static final String AUTO_INSTALL_PROP = ".installedByDropins";
}
