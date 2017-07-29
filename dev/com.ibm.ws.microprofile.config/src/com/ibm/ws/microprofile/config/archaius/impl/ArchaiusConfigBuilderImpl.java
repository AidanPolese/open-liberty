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

import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.Converter;

import com.ibm.ws.microprofile.config.impl.ConfigBuilderImpl;
import com.ibm.ws.microprofile.config.impl.SortedSources;

public class ArchaiusConfigBuilderImpl extends ConfigBuilderImpl implements ConfigBuilder {

    /**
     * Constructor
     *
     * @param classLoader
     * @param executor
     */
    public ArchaiusConfigBuilderImpl(ClassLoader classLoader, ScheduledExecutorService executor) {
        super(classLoader, executor);
    }

    /** {@inheritDoc} */
    @Override
    public ArchaiusConfigImpl build() {
        ArchaiusConfigImpl config = null;
        synchronized (this) {
            SortedSources sources = getSources();
            Map<Type, Converter<?>> converters = getConverters();
            ScheduledExecutorService executor = getScheduledExecutorService();
            long refreshInterval = getRefreshInterval();

            config = build(sources, converters, executor, refreshInterval);
        }
        return config;
    }

    //If Java 2 security is turned on then somewhere in the constructor for ArchaiusConfigImpl (probably one of the archaius super-classes)
    //it needs to have permission for "accessClassInPackage.com.ibm.oti.shared"
    //I don't know why and I don't much like putting a doPriv around a constructor with 3rd party code involved
    //The exception was...
    //
    //   java.lang.SecurityException: Exception creating permissions: class com.ibm.oti.shared.SharedClassPermission: Access denied ("java.lang.RuntimePermission" "accessClassInPackage.com.ibm.oti.shared")
    //
    //https://www.ibm.com/support/knowledgecenter/SSYKE2_6.0.0/com.ibm.java.api.60.doc/com.ibm.oti.shared/com/ibm/oti/shared/SharedClassPermission.html
    private ArchaiusConfigImpl build(SortedSources sources, Map<Type, Converter<?>> converters, ScheduledExecutorService executor, long refreshInterval) {
        ArchaiusConfigImpl config = AccessController.doPrivileged(new PrivilegedAction<ArchaiusConfigImpl>() {
            @Override
            public ArchaiusConfigImpl run() {
                return new ArchaiusConfigImpl(sources, new ConversionDecoder(converters), executor, refreshInterval);
            }
        });
        return config;
    }

}