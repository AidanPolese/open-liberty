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
package com.ibm.ws.config.admin;

import java.io.IOException;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Set;
import java.util.concurrent.Future;

import org.osgi.service.cm.Configuration;

/**
 *
 */
public interface ExtendedConfiguration extends Configuration {

    public void lock();

    public void unlock();

    public void fireConfigurationDeleted(Collection<Future<?>> futureList);

    public void fireConfigurationUpdated(Collection<Future<?>> futureList);

    public void delete(boolean fireNotifications);

    public Object getProperty(String key);

    public Dictionary<String, Object> getReadOnlyProperties();

    public void updateCache(Dictionary<String, Object> properties, Set<ConfigID> references, Set<String> newUniques) throws IOException;

    public void updateProperties(Dictionary<String, Object> properties) throws IOException;

    public Set<ConfigID> getReferences();

    public void setInOverridesFile(boolean inOverridesFile);

    public boolean isInOverridesFile();

    public Set<String> getUniqueVariables();

    /**
     * Set the ConfigID that this configuration is registered under
     * 
     * @param id
     */
    public void setFullId(ConfigID id);

    /**
     * 
     * @return
     */
    public ConfigID getFullId();

    /**
     * Returns true if the configuration has been deleted
     * 
     * @return true if the configuration has been deleted
     */
    public boolean isDeleted();

}
