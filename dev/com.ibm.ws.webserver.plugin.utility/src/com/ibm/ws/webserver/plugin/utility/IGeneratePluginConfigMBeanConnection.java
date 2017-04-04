/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.webserver.plugin.utility;

import java.io.IOException;

public interface IGeneratePluginConfigMBeanConnection {

    public void generateWebServerPlugin(String serverName, String targetPluginPath) throws IOException;

}
