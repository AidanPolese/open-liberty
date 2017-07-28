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
package com.ibm.ws.microprofile.config.basic.test;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.config.spi.ConfigSource;

import com.ibm.ws.microprofile.config.AbstractConfigSource;

/**
 *
 */
public class HashMapConfigSource extends AbstractConfigSource implements ConfigSource {

    private final Map<String, String> properties = new HashMap<>();

    public HashMapConfigSource(String id) {
        this(100, id);
    }

    public HashMapConfigSource(int ordinal, String id) {
        super(ordinal, id);
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }
}
