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
package com.ibm.ws.microprofile.archaius.impl.test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.microprofile.config.spi.ConfigSource;

import com.ibm.ws.microprofile.config.interfaces.ConfigConstants;

public class EnvTestSource implements ConfigSource {

    private static final int DEFAULT_ENV_ORDINAL = ConfigConstants.ORDINAL_ENVIRONMENT_VARIABLES;

    /** {@inheritDoc} */
    @Override
    public int getOrdinal() {
        return DEFAULT_ENV_ORDINAL;
    }

    /** {@inheritDoc} */
    @Override
    public ConcurrentMap<String, String> getProperties() {
        return new ConcurrentHashMap<>(System.getenv());
    }

    /** {@inheritDoc} */
    @Override
    public String getValue(String propertyName) {
        return System.getenv(propertyName);
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return "System.getenv(propertyName)";
    }
}
