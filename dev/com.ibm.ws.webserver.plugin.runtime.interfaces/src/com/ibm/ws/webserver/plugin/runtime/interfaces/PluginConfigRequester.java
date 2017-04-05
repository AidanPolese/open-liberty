/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webserver.plugin.runtime.interfaces;

/**
 *
 */
public interface PluginConfigRequester {

    String OBJECT_NAME = "WebSphere:feature=PluginUtility,name=PluginConfigRequester";

    public boolean generateClusterPlugin(String cluster);

    public boolean generateAppServerPlugin();

}
