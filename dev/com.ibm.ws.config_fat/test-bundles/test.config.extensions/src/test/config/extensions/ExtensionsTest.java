/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package test.config.extensions;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

/**
 *
 */
public class ExtensionsTest implements ManagedServiceFactory, ConfigPropertiesProvider {

    //timeout if the service hasn't been called in 15 seconds
    private final long TIMEOUT = 15000;

    Map<String, Dictionary<String, ?>> propSets = new ConcurrentHashMap<String, Dictionary<String, ?>>();

    public Dictionary<String, ?> getPropertiesForId(String id) {
        Dictionary<String, ?> props = getPropsForId(id);
        synchronized (propSets) {
            while (props == null) {
                try {
                    propSets.wait(TIMEOUT);
                    props = getPropsForId(id);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Interrupted");
                }
                if (props == null)
                    throw new RuntimeException("Extensions test timed out waiting for ManagedServiceFactory updated call starting with config id " + id);
            }
        }
        return props;
    }

    Dictionary<String, ?> getPropsForId(String id) {
        return propSets.get(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.cm.ManagedServiceFactory#deleted(java.lang.String)
     */
    @Override
    public void deleted(String pid) {
    // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.cm.ManagedServiceFactory#updated(java.lang.String, java.util.Dictionary)
     */
    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        System.out.println("ExtensionsTest (mock ManagedServiceFactory) updated called with pid: " + pid + " and properties: " + properties);
        if (pid != null && properties != null) {
            synchronized (propSets) {
                String id;
                propSets.put(((id = (String) properties.get("id")) == null) ? pid : id, properties);
                propSets.notifyAll();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.cm.ManagedServiceFactory#getName()
     */
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }
}
