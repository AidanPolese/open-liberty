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
import org.osgi.service.cm.ManagedService;

public abstract class ManagedTest extends Test implements ManagedService {

    public ManagedTest(String name) {
        super(name);
    }

    @Override
    public String[] getServiceClasses() {
        return new String[] { ManagedService.class.getName() };
    }

    @Override
    public void updated(Dictionary properties) throws ConfigurationException {
        try {
            configurationUpdated(properties);
        } catch (Throwable e) {
            exception = e;
        } finally {
            latch.countDown();
        }
    }

    public abstract void configurationUpdated(Dictionary properties) throws Exception;

}
