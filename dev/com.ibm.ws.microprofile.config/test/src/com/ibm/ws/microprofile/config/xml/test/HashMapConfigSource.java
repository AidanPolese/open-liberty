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
package com.ibm.ws.microprofile.config.xml.test;

import java.util.concurrent.ConcurrentMap;

import org.eclipse.microprofile.config.spi.ConfigSource;

import com.ibm.ws.microprofile.config.AbstractConfigSource;

/**
 *
 */
public class HashMapConfigSource extends AbstractConfigSource implements ConfigSource {

    private final ConcurrentMap<String, String> properties;

    public HashMapConfigSource(ConcurrentMap<String, String> properties, int ordinal, String id) {
        super(ordinal, id);
        this.properties = properties;
    }

    @Override
    public ConcurrentMap<String, String> getProperties() {
        return properties;
    }

    public static HashMapConfigSource newInstance(ConcurrentMap<String, String> properties, String id) {
        int ordinal = 100;
        HashMapConfigSource source = new HashMapConfigSource(properties, ordinal, id);
        return source;
    }
}
