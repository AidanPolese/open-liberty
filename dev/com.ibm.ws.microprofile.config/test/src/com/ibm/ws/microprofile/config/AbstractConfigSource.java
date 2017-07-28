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
package com.ibm.ws.microprofile.config;

import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 *
 */
public abstract class AbstractConfigSource implements ConfigSource {

    private final int ordinal;
    private final String name;

    public AbstractConfigSource(int ordinal, String id) {
        this.ordinal = ordinal;
        this.name = id;
    }

    /** {@inheritDoc} */
    @Override
    public int getOrdinal() {
        return ordinal;
    }

    /** {@inheritDoc} */
    @Override
    public String getValue(String propertyName) {
        return getProperties().get(propertyName);
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    public void put(String key, String value) {
        getProperties().put(key, value);
    }

}
