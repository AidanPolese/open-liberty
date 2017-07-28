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
package com.ibm.ws.microprofile.config.loader.test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.microprofile.config.spi.ConfigSource;

@SuppressWarnings("serial")
public class ServiceLoaderConfigSource extends ConcurrentHashMap<String, String> implements ConfigSource {

    public ServiceLoaderConfigSource() {
        put("SLKey1", "SLValue1");
        put("SLKey2", "SLValue2");
        put("SLKey3", "SLValue3");
        put("SLKey4", "SLValue4");
    }

    /** {@inheritDoc} */
    @Override
    public ConcurrentMap<String, String> getProperties() {
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public int getOrdinal() {
        return 100;
    }

    /** {@inheritDoc} */
    @Override
    public String getValue(String propertyName) {
        return get(propertyName);
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return "ServiceLoaderConfigSource";
    }
}
