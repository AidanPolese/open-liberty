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
package com.ibm.ws.microprofile.config.internals.test;

import java.util.concurrent.ConcurrentMap;

import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 *
 */
public class TestSource implements ConfigSource {

    private final int ordinal;
    private final String id;
    private int hashCode = -1;

    public TestSource(int ordinal, String id) {
        this.ordinal = ordinal;
        this.id = id;
    }

    public TestSource(int ordinal, String id, int hashCode) {
        this(ordinal, id);
        this.hashCode = hashCode;
    }

    /** {@inheritDoc} */
    @Override
    public ConcurrentMap<String, String> getProperties() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public int getOrdinal() {
        return ordinal;
    }

    /** {@inheritDoc} */
    @Override
    public String getValue(String propertyName) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return id;
    }

    @Override
    public int hashCode() {
        if (this.hashCode == -1) {
            return super.hashCode();
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return "TestSource: ordinal=" + ordinal + ", id=" + id + ", hashCode=" + hashCode();
    }

}
