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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

import test.server.BaseTest;

public class FactoryNestedTest extends BaseTest implements ManagedServiceFactory {

    private final Map<String, List<Dictionary>> dictionaries;

    public FactoryNestedTest(String name) {
        super(name);
        dictionaries = new HashMap<String, List<Dictionary>>();
    }

    @Override
    public String[] getServiceClasses() {
        return new String[] { ManagedServiceFactory.class.getName() };
    }

    @Override
    public void updated(String pid, Dictionary properties) throws ConfigurationException {
        notify(pid, properties);
    }

    @Override
    public void deleted(String pid) {
        notify(pid, null);
    }

    private void notify(String id, Dictionary properties) {
        List<Dictionary> list = getDictionaries(id);
        synchronized (list) {
            list.add(properties);
            list.notifyAll();
        }
    }

    private List<Dictionary> getDictionaries(String pid) {
        synchronized (dictionaries) {
            List<Dictionary> updates = dictionaries.get(pid);
            if (updates == null) {
                updates = new ArrayList<Dictionary>();
                dictionaries.put(pid, updates);
            }
            return updates;
        }
    }

    public Dictionary waitForUpdate(String pid) {
        List<Dictionary> list = getDictionaries(pid);
        synchronized (list) {
            while (list.isEmpty()) {
                try {
                    list.wait(TIMEOUT);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Interrupted");
                }
                if (list.isEmpty()) {
                    throw new RuntimeException("Timed out: " + pid);
                }
            }
            return list.remove(0);
        }
    }

}
