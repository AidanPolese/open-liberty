/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.wsspi.webcontainer.osgi.mbeans;

public interface GeneratePluginConfig {
	public static final String DEFAULT_VIRTUAL_HOST_GROUP = "default_host";
        public static final String DEFAULT_NODE_NAME = "default_node";
	
	public void generateDefaultPluginConfig();
	
	public void generatePluginConfig(String root, String name);

	public long getConnectTimeout();
	
	public long getIoTimeout();
	
	public boolean getExtendedHandshake();
	
	public boolean getWaitForContinue();
}
