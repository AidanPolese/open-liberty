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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.microprofile.config.spi.ConfigSource;

public class PropertiesTestSource implements ConfigSource {

    private static final int DEFAULT_ORDINAL = 100;
    private final Properties properties;
    private final String name;

    public PropertiesTestSource(Properties properties) {
        this.properties = properties;
        name = properties != null ? properties.propertyNames().toString() : "null";
    }

    public PropertiesTestSource(String resourceName) {
        this(new Properties());
        InputStream s = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        try {
            properties.load(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @throws IOException
     *
     */
    public PropertiesTestSource() {
        this("META-INF/microprofile-config.properties");
    }

    /** {@inheritDoc} */
    @Override
    public int getOrdinal() {
        return DEFAULT_ORDINAL;
    }

    /** {@inheritDoc} */
    @Override
    public ConcurrentMap<String, String> getProperties() {
        ConcurrentMap<String, String> props = new ConcurrentHashMap<String, String>();
        for (String key : properties.stringPropertyNames()) {
            props.put(key, properties.getProperty(key));
        }
        return props;
    }

    /** {@inheritDoc} */
    @Override
    public String getValue(String propertyName) {
        return properties.getProperty(propertyName);
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }
}
