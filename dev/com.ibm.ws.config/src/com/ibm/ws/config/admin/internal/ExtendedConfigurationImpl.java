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
 *
 * Change activity:
 *
 * Issue Date Name Description
 * ----------- ----------- -------- ------------------------------------
 *
 */

package com.ibm.ws.config.admin.internal;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.config.admin.ConfigID;
import com.ibm.ws.config.admin.ConfigurationDictionary;
import com.ibm.ws.config.admin.ExtendedConfiguration;

/**
 * This represents a Configuration and implements Configuration.
 * It provides APIs to get configuration attributes, properties,
 * and to delete and update its configuration dictionary.
 * 
 * In addition to the standard OSGi Configuration value type support,
 * this implementation also supports Map of Strings as one of the value types.
 * 
 */
class ExtendedConfigurationImpl implements ExtendedConfiguration {

    /** bundle location */
    private String bundleLocation = null;
    private Bundle boundBundle;

    /** An instance of a factory used for creating ConfigurationAdmin instances. */
    private final ConfigAdminServiceFactory caFactory;

    /** factory PID (only set for those using ManagedServiceFactory). */
    private final String factoryPid;

    /** Service PID */
    private final String pid;

    /** Configuration dictionary */
    private ConfigurationDictionary properties = null;

    /** hash code */
    private int hashCode = 0;

    /** set to true when delete() is called to delete this configuration. */
    private boolean deleted = false;

    /**
     * set to true if this configuration was specified in server.xml file.
     */
    private boolean inOverridesFile = false;

    /**
     * Set of references to other configurations.
     */
    private Set<ConfigID> references;

    /**
     * Set of variables used for unique checks
     */
    private Set<String> uniqueVariables = Collections.emptySet();

    private final ReentrantLock lock = new ReentrantLock();

    private final AtomicLong changeCount = new AtomicLong();
    private ConfigID configId;

    private volatile boolean sendEvents;

    /**
     * Constructor to create an instance of Configuration.
     * 
     * @param caImpl
     * @param bndlLocation
     * @param factoryPid
     * @param pid
     * @param props
     * @param casf
     * @param uniqueVariables
     */
    public ExtendedConfigurationImpl(ConfigAdminServiceFactory casf,
                                     String bndlLocation,
                                     String factoryPid,
                                     String pid,
                                     Dictionary<String, Object> props,
                                     Set<ConfigID> references,
                                     Set<String> uniques) {
        this.caFactory = casf;
        this.bundleLocation = bndlLocation;
        this.factoryPid = factoryPid;
        this.pid = pid;
        setProperties(props);

        this.references = references;
        this.uniqueVariables = uniques;
        addPidMapping();
        addReferences();
    }

    @Override
    @Trivial
    public void lock() {
        lock.lock();
    }

    @Override
    @Trivial
    public void unlock() {
        if (!lock.isHeldByCurrentThread()) {
            throw new IllegalStateException("Thread not lock owner"); //$NON-NLS-1$
        }
        lock.unlock();
    }

    @Trivial
    protected void checkLocked() {
        if (!lock.isHeldByCurrentThread()) {
            throw new IllegalStateException("Thread not lock owner"); //$NON-NLS-1$
        }
    }

    @Trivial
    protected boolean bind(Bundle bundle) {
        lock.lock();
        try {
            if (boundBundle == null && (bundleLocation == null || bundleLocation.equals(bundle.getLocation())))
                boundBundle = bundle;
            return (boundBundle == bundle);
        } finally {
            lock.unlock();
        }
    }

    @Trivial
    protected void unbind(Bundle bundle) {
        lock.lock();
        try {
            if (boundBundle == bundle)
                boundBundle = null;
        } finally {
            lock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.cm.Configuration#delete()
     */
    @Override
    @Trivial
    public void delete() throws IOException {
        delete(true);
    }

    @Override
    public void delete(boolean fireNotifications) {
        lock.lock();
        try {
            exceptionIfDeleted();
            deleted = true;

            if (fireNotifications) {
                fireConfigurationDeleted(null);
            }

            removePidMapping();
            removeReferences();
        } finally {
            lock.unlock();
        }

        caFactory.getConfigurationStore().removeConfiguration(pid);
    }

    @Override
    public void fireConfigurationDeleted(Collection<Future<?>> futureList) {
        Future<?> caFuture = caFactory.notifyConfigurationDeleted(this, factoryPid != null);
        Future<?> configFuture = caFactory.dispatchEvent(ConfigurationEvent.CM_DELETED, factoryPid, pid);
        if (futureList != null) {
            futureList.add(caFuture);
            futureList.add(configFuture);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.cm.Configuration#getBundleLocation()
     */
    @Override
    @Trivial
    public String getBundleLocation() {
        return getBundleLocation(true);
    }

    protected String getBundleLocation(boolean checkPermission) {
        lock.lock();
        try {
            exceptionIfDeleted();
            if (checkPermission)
                this.caFactory.checkConfigurationPermission();
            if (bundleLocation != null)
                return this.bundleLocation;
            if (boundBundle != null)
                return boundBundle.getLocation();
            return null;
        } finally {
            lock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.cm.Configuration#getFactoryPid()
     */
    @Override
    @Trivial
    public String getFactoryPid() {
        return getFactoryPid(true);
    }

    public String getFactoryPid(boolean checkDeleted) {
        lock.lock();
        try {
            if (checkDeleted)
                exceptionIfDeleted();
            return this.factoryPid;
        } finally {
            lock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.cm.Configuration#getPid()
     */
    @Override
    @Trivial
    public String getPid() {
        return getPid(true);
    }

    public String getPid(boolean checkDeleted) {
        lock.lock();
        try {
            if (checkDeleted)
                exceptionIfDeleted();
            return this.pid;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object getProperty(String key) {
        lock.lock();
        try {
            exceptionIfDeleted();
            // TODO: clone the value
            if (properties != null) {
                return properties.get(key);
            }

            return null;
        } finally {
            lock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.cm.Configuration#getProperties()
     */
    @Override
    public Dictionary<String, Object> getProperties() {
        lock.lock();
        try {
            exceptionIfDeleted();
            if (this.properties == null)
                return null;

            Dictionary<String, Object> copy = properties.copy();
            return copy;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Trivial
    public Dictionary<String, Object> getReadOnlyProperties() {
        lock.lock();
        try {
            exceptionIfDeleted();
            return properties;
        } finally {
            lock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.cm.Configuration#setBundleLocation(java.lang.String)
     */
    @Override
    @Trivial
    public void setBundleLocation(String bundleLocation) {
        setBundleLocation(bundleLocation, true);
    }

    private void setBundleLocation(String bundleLocation, boolean checkPerm) {
        lock.lock();
        try {
            exceptionIfDeleted();
            if (checkPerm)
                this.caFactory.checkConfigurationPermission();
            this.bundleLocation = bundleLocation;
            boundBundle = null;
        } finally {
            lock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.cm.Configuration#update()
     * 
     * The Configuration Admin service must first store the configuration
     * information
     * and then call a configuration target's updated method: either the
     * ManagedService.updated or ManagedServiceFactory.updated method.
     */
    @Override
    public void update() throws IOException {
        lock.lock();
        try {
            exceptionIfDeleted();
            caFactory.getConfigurationStore().saveConfiguration(pid, this);
            caFactory.notifyConfigurationUpdated(this, factoryPid != null);
        } finally {
            lock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.cm.Configuration#update(java.util.Dictionary)
     * 
     * The Configuration Admin service must first store the configuration
     * information
     * and then call a configuration target's updated method: either the
     * ManagedService.updated or ManagedServiceFactory.updated method.
     * 
     * Also initiates an asynchronous call to all ConfigurationListeners with a
     * ConfigurationEvent.CM_UPDATED event.
     */

    @Override
    public void update(Dictionary<String, ?> properties) throws IOException {
        lock.lock();
        try {
            doUpdateProperties(properties);

            fireConfigurationUpdated(null);
        } finally {
            lock.unlock();
        }
    }

    private void doUpdateProperties(Dictionary<String, ?> properties) throws IOException {
        exceptionIfDeleted();
        setProperties(properties);

        caFactory.getConfigurationStore().saveConfiguration(pid, this);
        changeCount.incrementAndGet();
        sendEvents = true;
    }

    /**
     * without other guards, separating updating the properties and sending configuration events
     * can result in missing and duplicate update events even if every update is eventually associated with an event.
     */
    @Override
    public void updateProperties(Dictionary<String, Object> properties) throws IOException {
        lock.lock();
        try {
            doUpdateProperties(properties);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void fireConfigurationUpdated(Collection<Future<?>> futureList) {
        if (sendEvents) {
            sendEvents = false;
            Future<?> caFuture = caFactory.notifyConfigurationUpdated(this, factoryPid != null);
            Future<?> configFuture = caFactory.dispatchEvent(ConfigurationEvent.CM_UPDATED, factoryPid, pid);
            if (futureList != null) {
                if (caFuture != null) {
                    futureList.add(caFuture);
                }
                if (configFuture != null) {
                    futureList.add(configFuture);
                }
            }
        }
    }

    /**
     * Updates ConfigurationAdmin's cache with current config properties.
     * If replaceProp is set to true, current config properties is replace with
     * the given properties before caching
     * and the internal pid-to-config table is updated to reflect the new config
     * properties.
     * 
     * @param properties
     * @param replaceProp
     * @param isMetaTypeProperties
     *            true if properties is MetaType converted properties
     * @param newUniques
     * @throws IOException
     */
    @Override
    public void updateCache(Dictionary<String, Object> properties, Set<ConfigID> references, Set<String> newUniques) throws IOException {
        lock.lock();
        try {
            removeReferences();

            setProperties(properties);
            this.references = references;
            this.uniqueVariables = newUniques;

            caFactory.getConfigurationStore().saveConfiguration(pid, this);
            changeCount.incrementAndGet();

            addReferences();
            sendEvents = true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ConfigID getFullId() {
        if (configId != null)
            return configId;

        if (factoryPid == null) {
            return new ConfigID(pid);
        } else {
            String id = (String) properties.get(ConfigAdminConstants.CFG_CONFIG_INSTANCE_ID);
            if (id == null)
                return new ConfigID(factoryPid, null);

            return ConfigID.fromProperty(id);
        }
    }

    @Override
    @Trivial
    public Set<ConfigID> getReferences() {
        lock.lock();
        try {
            return references;
        } finally {
            lock.unlock();
        }
    }

    @Trivial
    private void removeReferences() {
        if (properties != null && references != null) {
            ConfigID configId = getFullId();
            caFactory.removeReferences(references, configId);
        }
    }

    @Trivial
    private void addReferences() {
        if (properties != null && references != null) {
            ConfigID configId = getFullId();
            caFactory.addReferences(references, configId);
        }
    }

    @Trivial
    private void addPidMapping() {
        // save pid of factory configurations only
        if (properties != null && factoryPid != null && caFactory != null) {
            caFactory.registerConfiguration(getFullId(), this);
        }
    }

    @Trivial
    private void removePidMapping() {
        // remove pid of factory configurations only
        if (properties != null && factoryPid != null) {
            caFactory.unregisterConfiguration(getFullId());
        }
    }

    /**
     * Equals if PID of each Configuration objects are equal.
     */
    @Override
    @Trivial
    public boolean equals(Object o) {
        if ((o != null) && (o instanceof Configuration)) {
            String oPid = ((Configuration) o).getPid();
            if (this.pid == null) {
                return (oPid == null);
            }
            return this.pid.equals(oPid);
        }
        return false;
    }

    /**
     * Hashcode is generated based on PID.
     */
    @Override
    @Trivial
    public int hashCode() {
        if (hashCode == 0)
            hashCode = this.pid.hashCode();
        return hashCode;
    }

    /**
     * This is not part of Configuration interface.
     * It sets configuration dictionary with specified dictionary
     * and updates configuration attributes if they are not set
     * and found in given dictionary.
     * 
     * @param d
     */
    private void setProperties(Dictionary<String, ?> d) {
        if (d == null) {
            this.properties = null;
            return;
        }

        ConfigurationDictionary newDictionary = new ConfigurationDictionary();
        Enumeration<String> keys = d.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (newDictionary.get(key) == null) {
                Object value = d.get(key);
                if (value.getClass().isArray()) {
                    int arrayLength = Array.getLength(value);
                    Object copyOfArray = Array.newInstance(value.getClass().getComponentType(), arrayLength);
                    System.arraycopy(value, 0, copyOfArray, 0, arrayLength);
                    newDictionary.put(key, copyOfArray);
                } else if (value instanceof Collection) {
                    newDictionary.put(key, new Vector<Object>((Collection<?>) value));
                } else {
                    newDictionary.put(key, value);
                }
            } else
                throw new IllegalArgumentException(key + " is already present or is a case variant."); //$NON-NLS-1$
        }

        // fill in necessary properties
        if (this.factoryPid != null) {
            newDictionary.put(ConfigurationAdmin.SERVICE_FACTORYPID, this.factoryPid);
        }
        newDictionary.put(Constants.SERVICE_PID, this.pid);
        if (this.inOverridesFile) {
            newDictionary.put("config.overrides", "true");
        }

        this.properties = newDictionary;

        //we got new props so we should redo the mappings in case they changed
        addPidMapping();
    }

    @Override
    @Trivial
    public void setInOverridesFile(boolean inOverridesFile) {
        lock.lock();
        try {
            this.inOverridesFile = inOverridesFile;
            if (this.properties != null) {
                if (inOverridesFile) {
                    this.properties.put("config.overrides", "true");
                } else {
                    this.properties.remove("config.overrides");
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Trivial
    public boolean isInOverridesFile() {
        lock.lock();
        try {
            exceptionIfDeleted();
            // TODO: clone the value
            if (properties != null) {
                return properties.get("config.overrides") != null;
            }

            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Checks for deleted flag and throws an IllegalStateException if deleted.
     */
    @Trivial
    private void exceptionIfDeleted() {
        if (this.deleted)
            throw new IllegalStateException("Configuration pid " + pid + " was deleted.");
    }

    @Override
    @Trivial
    public boolean isDeleted() {
        return this.deleted;
    }

    @Override
    @Trivial
    public String toString() {
        return this.getClass().getSimpleName()
               + "[pid=" + pid
               + ",factoryPid=" + factoryPid
               + ",boundBundle=" + boundBundle
               + ",bundleLocation=" + bundleLocation
               + "]";

    }

    /**
     * @return the uniqueVariables
     */
    @Override
    @Trivial
    public Set<String> getUniqueVariables() {
        lock.lock();
        try {
            if (uniqueVariables == null)
                return Collections.emptySet();
            else
                return uniqueVariables;
        } finally {
            lock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.cm.Configuration#getChangeCount()
     */
    @Override
    @Trivial
    public long getChangeCount() {
        return changeCount.get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.config.admin.ExtendedConfiguration#setFullId(com.ibm.ws.config.admin.ConfigID)
     */
    @Override
    public void setFullId(ConfigID id) {
        this.configId = id;
    }

    @Trivial
    protected boolean matchesFilter(Filter filter) {
        if (this.properties != null)
            return properties.matches(filter);
        return false;
    }
}
