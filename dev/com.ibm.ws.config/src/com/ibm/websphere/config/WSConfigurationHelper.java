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
package com.ibm.websphere.config;

import java.io.InputStream;
import java.util.Dictionary;

/**
 *
 */
public interface WSConfigurationHelper {

    /**
     * Retrieve a dictionary containing the default metatype properties
     * 
     * @param pid The full pid or factoryPid value for the configuration
     * @return A Dictionary containing the default properties
     * @throws ConfigEvaluatorException
     */
    Dictionary<String, Object> getMetaTypeDefaultProperties(String pid) throws ConfigEvaluatorException;

    /**
     * Add a default configuration instance. If this affects existing configurations they
     * will be updated.
     * 
     * @param pid The full pid or factoryPid value for the configuration
     * @param properties A Dictionary that contains the configuration properties
     * @throws ConfigUpdateException
     */
    void addDefaultConfiguration(String pid, Dictionary<String, String> properties) throws ConfigUpdateException;

    /**
     * Add a default configuration instance using an InputStream that points to valid XML.
     * If this affects existing configurations they will be updated.
     * 
     * @param defaultConfig An InputStream that points to valid XML
     * @throws ConfigUpdateException
     */
    void addDefaultConfiguration(InputStream defaultConfig) throws ConfigUpdateException;

    /**
     * Remove all default configurations with the specified pid or factoryPid. This only affects
     * configurations that were added by this interface. If the removal of the default
     * configuration affects existing configurations, they will be updated.
     * 
     * @param pid The pid or factoryPid
     * @return true if any configurations were removed
     * @throws ConfigUpdateException
     */
    boolean removeDefaultConfiguration(String pid) throws ConfigUpdateException;

    /**
     * Remove default configurations with the specified pid or factoryPid and ID value. This only
     * affects configurations that were added by this interface. If the removal of the default
     * configuration affects existing configurations, they will be updated.
     * 
     * @param pid The pid or factoryPid
     * @param id The id property of the default configuration
     * @return true if any configurations were removed
     * @throws ConfigUpdateException
     */
    boolean removeDefaultConfiguration(String pid, String id) throws ConfigUpdateException;

}
