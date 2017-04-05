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

import java.io.File;

/**
 * The PluginUtilityConfigGenerator interface is meant to be implemented by MBEANS that will be leveraged
 * for auto generation of the plugin-cfg.xml file.
 */
public interface PluginUtilityConfigGenerator {

    /**
     * Each implementor needs to have a unique type returned when getPluginConfigType is called.
     */
    public enum Types {
        WEBCONTAINER,
        COLLECTIVE
    }

    /**
     * Generate the plugin-cfg.xml file.
     * 
     * @param name - The name of the server or collective that the plugin-cfg.xml file will be generated for.
     * @param writeDirectory - The directory to write the plugin-cfg.xml file to
     */
    public void generatePluginConfig(String name, File writeDirectory);

    /**
     * Return a unique identifier. In this case one of the values defined in the Types enum.
     * 
     * @return
     */
    public Types getPluginConfigType();
}
