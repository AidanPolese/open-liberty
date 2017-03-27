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

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ConfigurationPlugin;

public class ConfigurationPluginTest extends ManagedFactoryTest implements ConfigurationPlugin {

    /**  */
    private static final String VALUE = "VALUE";
    /**  */
    private static final String NEW_KEY = "NEW_KEY";
    /**  */
    private static final String INJECT = "inject";

    public ConfigurationPluginTest(String name) {
        super(name, 2);
    }

    @Override
    public String[] getServiceClasses() {
        String[] baseServices = super.getServiceClasses();
        String[] services = new String[baseServices.length + 1];
        services[0] = ConfigurationPlugin.class.getName();
        System.arraycopy(baseServices, 0, services, 1, baseServices.length);
        return services;
    }

    @Override
    public void configurationUpdated(String pid, Dictionary<String, ?> props) throws ConfigurationException {
        if (Boolean.TRUE.equals(props.get(INJECT))) {
            if (!props.get(NEW_KEY).equals(VALUE)) {
                throw new ConfigurationException(NEW_KEY, "Missing " + VALUE);
            }
        }
    }

    @Override
    public void modifyConfiguration(ServiceReference<?> ref, Dictionary<String, Object> props) {
        if (name.equals(ref.getProperty(Constants.SERVICE_PID)) && Boolean.TRUE.equals(props.get(INJECT))) {
            props.put(NEW_KEY, VALUE);
        }
    }

}
