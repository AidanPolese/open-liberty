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

import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.osgi.service.component.annotations.Component;

import com.ibm.ws.microprofile.config.impl.ConfigBuilderImpl;
import com.ibm.ws.microprofile.config.impl.ConfigProviderResolverImpl;

@Component(name = "com.ibm.ws.microprofile.config.archaius.impl.ArchaiusConfigProviderResolverImpl", service = { ConfigProviderResolver.class }, property = { "service.vendor=IBM" }, immediate = true)
public class ArchaiusConfigProviderResolverImpl extends ConfigProviderResolverImpl {

    /** {@inheritDoc} */
    @Override
    protected ConfigBuilderImpl newBuilder(ClassLoader classLoader) {
        return new ArchaiusConfigBuilderImpl(classLoader, getScheduledExecutorService());
    }

}