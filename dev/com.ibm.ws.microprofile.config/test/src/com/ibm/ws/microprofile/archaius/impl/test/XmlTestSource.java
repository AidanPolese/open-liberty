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
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 *
 */
public class XmlTestSource implements ConfigSource {

    final Properties p = new Properties();
    private final int ordinal = 100;
    private String name;

    public XmlTestSource(String resourceName) {
        try {
            p.loadFromXML(Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public XmlTestSource() {
        this("META-INF/config.xml");
    }

    /** {@inheritDoc} */
    @Override
    public ConcurrentMap<String, String> getProperties() {
        return propertiesToMap(p);
    }

    /**
     * @return
     */
    private ConcurrentMap<String, String> propertiesToMap(Properties p) {
        ConcurrentMap<String, String> map = new ConcurrentHashMap<String, String>();
        Set<Entry<Object, Object>> entries = p.entrySet();
        for (Iterator<Entry<Object, Object>> iterator = entries.iterator(); iterator.hasNext();) {
            Entry<Object, Object> entry = iterator.next();
            map.put((String) entry.getKey(), (String) entry.getValue());
        }
        return map;
    }

    /** {@inheritDoc} */
    @Override
    public int getOrdinal() {
        return ordinal;
    }

    /** {@inheritDoc} */
    @Override
    public String getValue(String propertyName) {
        return p.getProperty(propertyName);
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }
};