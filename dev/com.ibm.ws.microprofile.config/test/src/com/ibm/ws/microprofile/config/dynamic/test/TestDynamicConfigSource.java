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
package com.ibm.ws.microprofile.config.dynamic.test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.microprofile.config.spi.ConfigSource;

@SuppressWarnings("serial")
public class TestDynamicConfigSource extends ConcurrentHashMap<String, String> implements ConfigSource {

    /** {@inheritDoc} */
    @Override
    public ConcurrentMap<String, String> getProperties() {
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public int getOrdinal() {
        return Integer.MAX_VALUE;
    }

    /** {@inheritDoc} */
    @Override
    public String getValue(String propertyName) {
        return get(propertyName);
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return "DynamicConfigSourceTest";
    }
}
