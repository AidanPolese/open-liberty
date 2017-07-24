/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.microprofile.config.sources;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.microprofile.config.spi.ConfigSource;

import com.ibm.ws.microprofile.config.interfaces.ConfigConstants;

/**
 *
 */
public class SystemConfigSource extends AbstractConfigSource implements ConfigSource {

    /**
     * @param ordinal
     */
    public SystemConfigSource() {
        super(getSystemOrdinal(), "System Properties Config Source");
    }

    /** {@inheritDoc} */
    @Override
    public ConcurrentMap<String, String> getProperties() {
        ConcurrentMap<String, String> props = new ConcurrentHashMap<>();
        Properties sysProps = getSystemProperties();
        Set<String> keys = sysProps.stringPropertyNames();
        for (String key : keys) {
            props.put(key, sysProps.getProperty(key));
        }

        return props;
    }

    public static int getSystemOrdinal() {
        String ordinalProp = getOrdinalSystemProperty();
        int ordinal = ConfigConstants.ORDINAL_SYSTEM_PROPERTIES;
        if (ordinalProp != null) {
            ordinal = Integer.parseInt(ordinalProp);
        }
        return ordinal;
    }

    private static String getOrdinalSystemProperty() {
        String prop = AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty(ConfigConstants.ORDINAL_PROPERTY);
            }
        });
        return prop;
    }

    private static Properties getSystemProperties() {
        Properties prop = AccessController.doPrivileged(new PrivilegedAction<Properties>() {
            @Override
            public Properties run() {
                return System.getProperties();
            }
        });
        return prop;
    }
}
