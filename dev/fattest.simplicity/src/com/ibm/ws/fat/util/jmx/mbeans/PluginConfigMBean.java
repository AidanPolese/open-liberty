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
package com.ibm.ws.fat.util.jmx.mbeans;

import javax.management.remote.JMXServiceURL;

import com.ibm.ws.fat.util.jmx.JmxException;
import com.ibm.ws.fat.util.jmx.SimpleJmxOperation;
import com.ibm.ws.fat.util.jmx.SimpleMBean;

/**
 * Convenience class to work with the PluginConfigMBean for a specific Liberty server
 * 
 * @author Tim Burns
 */
public class PluginConfigMBean extends SimpleMBean {

    /**
     * Encapsulate the PluginConfigMBean for a specific Liberty server
     * 
     * @param url the JMX connection URL where you want to find the MBean
     * @throws JmxException if the object name for the PluginConfigMBean cannot be constructed
     */
    public PluginConfigMBean(JMXServiceURL url) throws JmxException {
        super(url, getObjectName("WebSphere:name=com.ibm.ws.jmx.mbeans.generatePluginConfig"));
    }

    /**
     * Generates a default configuration file for the HTTP Plugin
     * 
     * @throws JmxException if plugin generation fails
     */
    protected void generateDefaultPluginConfig() throws JmxException {
        SimpleJmxOperation.invoke(this.getUrl(), this.getObjectName(), "generateDefaultPluginConfig", null, null);
    }

}
