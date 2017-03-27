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
package test.server.config.dynamic;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.logging.Logger;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import test.server.BaseTest;

public class SingletonTest extends BaseTest implements ManagedService {
    private final static String CLASS_NAME = SingletonTest.class.getName();
    private final static Logger LOGGER = Logger.getLogger(CLASS_NAME);

    private final List<Dictionary> dictionaries;

    public SingletonTest(String name) {
        super(name);
        dictionaries = new ArrayList<Dictionary>();
    }

    @Override
    public String[] getServiceClasses() {
        return new String[] { ManagedService.class.getName() };
    }

    @Override
    public void updated(Dictionary properties) throws ConfigurationException {
        synchronized (dictionaries) {
            LOGGER.finest("updated - " + properties);
            dictionaries.add(properties);
            dictionaries.notifyAll();
        }
    }

    public Dictionary waitForUpdate() {
        LOGGER.entering(CLASS_NAME, "waitForUpdate");
        synchronized (dictionaries) {
            while (dictionaries.isEmpty()) {
                try {
                    LOGGER.finest("waitForUpdate - waiting");
                    dictionaries.wait(TIMEOUT);
                } catch (InterruptedException e) {
                    LOGGER.finest("waitForUpdate - interrupted");
                    throw new RuntimeException("Interrupted");
                }
                if (dictionaries.isEmpty()) {
                    LOGGER.finest("waitForUpdate - timed out");
                    throw new RuntimeException("Timed out");
                }
            }
            Dictionary d = dictionaries.remove(0);
            LOGGER.exiting(CLASS_NAME, "waitForUpdate", d);
            return d;
        }
    }

    /**
     * 
     */
    public void reset() {
        synchronized (dictionaries) {
            dictionaries.clear();
        }

    }

}
