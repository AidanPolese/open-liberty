/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */
package test.server.config;

import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

public abstract class ManagedFactoryTest extends Test implements ManagedServiceFactory {

    public ManagedFactoryTest(String name) {
        this(name, 1);
    }

    public ManagedFactoryTest(String name, int count) {
        super(name, count);
    }

    @Override
    public String[] getServiceClasses() {
        return new String[] { ManagedServiceFactory.class.getName() };
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        try {
            configurationUpdated(pid, properties);
        } catch (Throwable e) {
            exception = e;
        } finally {
            latch.countDown();
        }
    }

    public abstract void configurationUpdated(String pid, Dictionary<String, ?> properties) throws Exception;

    @Override
    public void deleted(String pid) {}

}
