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

import org.eclipse.microprofile.config.spi.ConfigSource;

import com.ibm.ws.microprofile.config.interfaces.ConfigConstants;

public class SystemPropertiesTestSource extends PropertiesTestSource implements ConfigSource {

    private static final int DEFAULT_SYSTEM_ORDINAL = ConfigConstants.ORDINAL_SYSTEM_PROPERTIES;

    public SystemPropertiesTestSource() {
        super(System.getProperties());
    }

    /** {@inheritDoc} */
    @Override
    public int getOrdinal() {
        return DEFAULT_SYSTEM_ORDINAL;
    }

}
