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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.microprofile.config.spi.ConfigSource;

import com.ibm.ws.microprofile.config.interfaces.ConfigConstants;

/**
 *
 */
public class EnvConfigSource extends AbstractConfigSource implements ConfigSource {

    /**
     * @param ordinal
     */
    public EnvConfigSource() {
        super(getEnvOrdinal(), "Environment Variables Config Source");
    }

    /** {@inheritDoc} */
    @Override
    public ConcurrentMap<String, String> getProperties() {

        Map<String, String> props = AccessController.doPrivileged(new PrivilegedAction<Map<String, String>>() {
            @Override
            public Map<String, String> run() {
                return System.getenv();
            }
        });

        return new ConcurrentHashMap<>(props);
    }

    public static int getEnvOrdinal() {
        String ordinalProp = getOrdinalEnvVar();
        int ordinal = ConfigConstants.ORDINAL_ENVIRONMENT_VARIABLES;
        if (ordinalProp != null) {
            ordinal = Integer.parseInt(ordinalProp);
        }
        return ordinal;
    }

    private static String getOrdinalEnvVar() {
        String prop = AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getenv(ConfigConstants.ORDINAL_PROPERTY);
            }
        });
        return prop;
    }
}
