/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.microprofile.metrics2.impl;

import java.util.NoSuchElementException;

import javax.enterprise.inject.Vetoed;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Histogram;
import org.eclipse.microprofile.metrics.HitCounter;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetadataBuilder;
import org.eclipse.microprofile.metrics.Meter;
import org.eclipse.microprofile.metrics.Metric;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.ParallelCounter;
import org.eclipse.microprofile.metrics.Timer;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.microprofile.metrics.impl.MetricRegistryImpl;

/**
 * A registry of metric instances.
 */
@Vetoed
public class MetricRegistry2Impl extends MetricRegistryImpl {

    /**
     * Creates a new {@link MetricRegistry}.
     */
    public MetricRegistry2Impl() {
        super();
    }

    @Override
    public <T extends Metric> T register(String name, T metric) throws IllegalArgumentException {
        // For MP Metrics 1.0, MetricType.from(Class in) does not support lambdas or proxy classes
        Metadata metadata = Metadata.builder((Metadata) metric).build();
        return register(metadata, metric);
    }

    @Override
    @Deprecated
    public <T extends Metric> T register(String name, T metric, Metadata metadata) throws IllegalArgumentException {
        return register(metadata, metric);
    }

    @Override
    @FFDCIgnore({ NoSuchElementException.class })
    public <T extends Metric> T register(Metadata metadata, T metric) throws IllegalArgumentException {
        //Create Copy of Metadata object so it can't be changed after its registered
        MetadataBuilder metadataCopyBuilder = Metadata.builder(metadata).reusable().addTag(metadata.getTagsAsString());

        //Append global tags to the metric
        Config config = ConfigProviderResolver.instance().getConfig(Thread.currentThread().getContextClassLoader());
        try {
            String[] globaltags = config.getValue("MP_METRICS_TAGS", String.class).split(",");
            String currentTags = metadata.getTagsAsString();
            for (String tag : globaltags) {
                if (!(tag == null || tag.isEmpty() || !tag.contains("="))) {
                    if (!currentTags.contains(tag.split("=")[0])) {
                        metadataCopyBuilder.addTag(tag);
                    }
                }
            }
        } catch (NoSuchElementException e) {
            //Continue if there is no global tags
        }
        Metadata metadataCopy = metadataCopyBuilder.build();
        final Metric existing = metrics.putIfAbsent(metadata.getName(), metric);
        this.metadata.putIfAbsent(metadata.getName(), metadataCopy);
        if (existing == null) {
        } else {
            throw new IllegalArgumentException("A metric named " + metadata.getName() + " already exists");
        }
        addNameToApplicationMap(metadata.getName());
        return metric;
    }

    public static MetricType from(Metric in) {
        MetricType result = MetricRegistryImpl.from(in);
        if (result != MetricType.INVALID)
            return result;
        else if (HitCounter.class.isInstance(in))
            return MetricType.HIT_COUNTER;
        else if (ParallelCounter.class.isInstance(in))
            return MetricType.PARALLEL_COUNTER;
        else
            return MetricType.INVALID;
    }

    @Override
    public Counter counter(String name) {
        return this.counter(Metadata.builder().withName(name).withType(MetricType.COUNTER).build());
    }

    @Override
    public HitCounter hitCounter(String name) {
        return this.hitCounter(Metadata.builder().withName(name).withType(MetricType.HIT_COUNTER).build());
    }

    @Override
    public ParallelCounter parallelCounter(String name) {
        return this.parallelCounter(Metadata.builder().withName(name).withType(MetricType.PARALLEL_COUNTER).build());
    }

    @Override
    public Histogram histogram(String name) {
        return this.histogram(Metadata.builder().withName(name).withType(MetricType.HISTOGRAM).build());
    }

    @Override
    public Meter meter(String name) {
        return this.meter(Metadata.builder().withName(name).withType(MetricType.METERED).build());
    }

    @Override
    public Timer timer(String name) {
        return this.timer(Metadata.builder().withName(name).withType(MetricType.TIMER).build());
    }

}
