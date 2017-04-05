/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.websphere.webcontainer;

/**
 * Management interface for the MBean "WebSphere:name=com.ibm.ws.jmx.mbeans.generatePluginConfig".
 * The Liberty profile makes this MBean available in its platform MBean server to allow users to generate a plugin-cfg.xml file which can be used to configure a web server plug-in
 * that forwards HTTP requests for dynamic resources to the Liberty profile. This interface can be used to request a proxy object via the {@link javax.management.JMX#newMBeanProxy}
 * method.
 * 
 * This class has been deprecated since 16.0.0.3 as the server now automatically generates the web server plugin
 * configuration file into the ${server.output.dir}/logs/state directory
 * 
 * @ibm-api
 * @deprecated
 */
public interface GeneratePluginConfigMBean {

    /**
     * Invokes the generateDefaultPluginConfig operation, which generates a plugin-cfg.xml file with default settings in the ${server.output.dir} directory.
     */
    public void generateDefaultPluginConfig();

    /**
     * Invokes the generatePluginConfig operation with root and name parameters, which generates a customized plugin-cfg.xml file in the ${server.output.dir} directory.
     * The input parameters are used to construct the plugin log file location.  The directory for the log file must exist on the web server host.  Values used to  
     * generate the file can be set using the Web Server Plugin (pluginConfiguration) properties in the server.xml file.
     * 
     * @param root plug-in install root directory
     * @param name web server name
     */
    public void generatePluginConfig(String pluginInstallRoot, String webServerName);
}
