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
package com.ibm.ws.microprofile.config.archaius.impl;

import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.microprofile.config.impl.AbstractConfig;
import com.ibm.ws.microprofile.config.impl.SortedSources;
import com.ibm.ws.microprofile.config.interfaces.WebSphereConfig;

public class ArchaiusConfigImpl extends AbstractConfig implements WebSphereConfig {

    private static final TraceComponent tc = Tr.register(ArchaiusConfigImpl.class);

    private final CompositeConfig composite;

    /**
     * The sources passed in should have already been wrapped up as an unmodifiable copy
     *
     * @param sources
     * @param converters
     * @param executor
     */
    public ArchaiusConfigImpl(SortedSources sources, ConversionDecoder decoder, ScheduledExecutorService executor, long refreshInterval) {
        super(sources, decoder);
        composite = new CompositeConfig(sources, decoder, executor, refreshInterval);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        super.close();
        composite.close();
    }

    /** {@inheritDoc} */
    @Override
    protected <T> T getTypedValue(String propertyName, Class<T> propertyType) {
        return composite.getTypedValue(propertyName, propertyType);
    }

    /** {@inheritDoc} */
    @Override
    protected Set<String> getKeySet() {
        return composite.getKeySet();
    }
}
