/*******************************************************************************
* Copyright (c) 2017, 2018 IBM Corporation and others.
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
*******************************************************************************
* Copyright 2010-2013 Coda Hale and Yammer, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/
package com.ibm.ws.microprofile.metrics.impl;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import javax.enterprise.inject.Vetoed;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.Histogram;
import org.eclipse.microprofile.metrics.HitCounter;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetadataBuilder;
import org.eclipse.microprofile.metrics.Meter;
import org.eclipse.microprofile.metrics.Metric;
import org.eclipse.microprofile.metrics.MetricFilter;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.ParallelCounter;
import org.eclipse.microprofile.metrics.Timer;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;

/**
 * A registry of metric instances.
 */
@Vetoed
public class MetricRegistryImpl extends MetricRegistry {
    /**
     * Concatenates elements to form a dotted name, eliding any null values or empty strings.
     *
     * @param name the first element of the name
     * @param names the remaining elements of the name
     * @return {@code name} and {@code names} concatenated by periods
     */
    public static String name(String name, String... names) {
        final StringBuilder builder = new StringBuilder();
        append(builder, name);
        if (names != null) {
            for (String s : names) {
                append(builder, s);
            }
        }
        return builder.toString();
    }

    /**
     * Concatenates a class name and elements to form a dotted name, eliding any null values or
     * empty strings.
     *
     * @param klass the first element of the name
     * @param names the remaining elements of the name
     * @return {@code klass} and {@code names} concatenated by periods
     */
    public static String name(Class<?> klass, String... names) {
        return name(klass.getName(), names);
    }

    private static void append(StringBuilder builder, String part) {
        if (part != null && !part.isEmpty()) {
            if (builder.length() > 0) {
                builder.append('.');
            }
            builder.append(part);
        }
    }

    /**
     * Convert the metric class type into an enum
     * For MP Metrics 1.0, MetricType.from(Class in) does not support lambdas or proxy classes
     *
     * @param in The metric
     * @return the matching Enum
     */
    public static MetricType from(Metric in) {
        if (Gauge.class.isInstance(in))
            return MetricType.GAUGE;
        if (Counter.class.isInstance(in))
            return MetricType.COUNTER;
        if (Histogram.class.isInstance(in))
            return MetricType.HISTOGRAM;
        if (Meter.class.isInstance(in))
            return MetricType.METERED;
        if (Timer.class.isInstance(in))
            return MetricType.TIMER;
        return MetricType.INVALID;
    }

    protected final ConcurrentMap<String, Metric> metrics;
    protected final ConcurrentMap<String, Metadata> metadata;
    protected final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> applicationMap;

    /**
     * Creates a new {@link MetricRegistry}.
     */
    public MetricRegistryImpl() {
        this.metrics = buildMap();

        //initializing metadata in a separate list
        this.metadata = new ConcurrentHashMap<String, Metadata>();

        this.applicationMap = new ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>();
    }

    /**
     * Creates a new {@link ConcurrentMap} implementation for use inside the registry. Override this
     * to create a {@link MetricRegistry} with space- or time-bounded metric lifecycles, for
     * example.
     *
     * @return a new {@link ConcurrentMap}
     */
    protected ConcurrentMap<String, Metric> buildMap() {
        return new ConcurrentHashMap<String, Metric>();
    }

    /**
     * Given a {@link Metric}, registers it under the given name.
     *
     * @param name the name of the metric
     * @param metric the metric
     * @param <T> the type of the metric
     * @return {@code metric}
     * @throws IllegalArgumentException if the name is already registered
     */
    @Override
    public <T extends Metric> T register(String name, T metric) throws IllegalArgumentException {
        // For MP Metrics 1.0, MetricType.from(Class in) does not support lambdas or proxy classes
        Metadata metadata = Metadata.builder().withName(name).withType(from(metric)).build();
        return register(metadata, metric);
    }

    @Override
    @FFDCIgnore({ NoSuchElementException.class })
    public <T extends Metric> T register(Metadata metadata, T metric) throws IllegalArgumentException {
        //Create Copy of Metadata object so it can't be changed after its registered
        MetadataBuilder metadataCopyBuilder = Metadata.builder(metadata);

        //Append global tags to the metric
        Config config = ConfigProviderResolver.instance().getConfig(Thread.currentThread().getContextClassLoader());
        try {
            String[] globaltags = config.getValue("MP_METRICS_TAGS", String.class).split(",");
            Map<String, String> currentTags = metadata.getTags();
            for (String tag : globaltags) {
                if (!(tag == null || tag.isEmpty() || !tag.contains("="))) {
                    if (currentTags.get(tag.split("=")[0]) == null) {
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

    /**
     * Adds the metric name to an application map.
     * This map is not a complete list of metrics owned by an application,
     * produced metrics are managed in the MetricsExtension
     *
     * @param name
     */
    protected void addNameToApplicationMap(String name) {
        String appName = getApplicationName();

        // If it is a base metric, the name will be null
        if (appName == null)
            return;
        ConcurrentLinkedQueue<String> list = applicationMap.get(appName);
        if (list == null) {
            ConcurrentLinkedQueue<String> newList = new ConcurrentLinkedQueue<String>();
            list = applicationMap.putIfAbsent(appName, newList);
            if (list == null)
                list = newList;
        }
        list.add(name);
    }

    public void unRegisterApplicationMetrics(String appName) {
        ConcurrentLinkedQueue<String> list = applicationMap.remove(appName);
        if (list != null) {
            for (String metricName : list) {
                remove(metricName);
            }
        }
    }

    private String getApplicationName() {
        com.ibm.ws.runtime.metadata.ComponentMetaData metaData = com.ibm.ws.threadContext.ComponentMetaDataAccessorImpl.getComponentMetaDataAccessor().getComponentMetaData();
        if (metaData != null) {
            com.ibm.websphere.csi.J2EEName name = metaData.getJ2EEName();
            if (name != null) {
                return name.getApplication();
            }
        }
        return null;
    }

    /**
     * Return the {@link Counter} registered under this name; or create and register
     * a new {@link Counter} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Counter}
     */
    @Override
    public Counter counter(String name) {
        return this.counter(Metadata.builder().withName(name).withType(MetricType.COUNTER).build());
    }

    @Override
    public Counter counter(Metadata metadata) {
        return getOrAdd(metadata, MetricBuilder.COUNTERS);
    }

    /**
     * Return the {@link Histogram} registered under this name; or create and register
     * a new {@link Histogram} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Histogram}
     */
    @Override
    public Histogram histogram(String name) {
        return this.histogram(Metadata.builder().withName(name).withType(MetricType.HISTOGRAM).build());
    }

    @Override
    public Histogram histogram(Metadata metadata) {
        return getOrAdd(metadata, MetricBuilder.HISTOGRAMS);
    }

    /**
     * Return the {@link Meter} registered under this name; or create and register
     * a new {@link Meter} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Meter}
     */
    @Override
    public Meter meter(String name) {
        return this.meter(Metadata.builder().withName(name).withType(MetricType.METERED).build());
    }

    @Override
    public Meter meter(Metadata metadata) {
        return getOrAdd(metadata, MetricBuilder.METERS);
    }

    /**
     * Return the {@link Timer} registered under this name; or create and register
     * a new {@link Timer} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Timer}
     */
    @Override
    public Timer timer(String name) {
        return this.timer(Metadata.builder().withName(name).withType(MetricType.TIMER).build());
    }

    @Override
    public Timer timer(Metadata metadata) {
        return getOrAdd(metadata, MetricBuilder.TIMERS);
    }

    /**
     * Removes the metric with the given name.
     *
     * @param name the name of the metric
     * @return whether or not the metric was removed
     */
    @Override
    public boolean remove(String name) {
        final Metric metric = metrics.remove(name);
        metadata.remove(name);
        if (metric != null) {
            return true;
        }
        return false;
    }

    /**
     * Removes all metrics which match the given filter.
     *
     * @param filter a filter
     */
    @Override
    public void removeMatching(MetricFilter filter) {
        for (Map.Entry<String, Metric> entry : metrics.entrySet()) {
            if (filter.matches(entry.getKey(), entry.getValue())) {
                remove(entry.getKey());
            }
        }
    }

    /**
     * Returns a set of the names of all the metrics in the registry.
     *
     * @return the names of all the metrics
     */
    @Override
    public SortedSet<String> getNames() {
        return Collections.unmodifiableSortedSet(new TreeSet<String>(metrics.keySet()));
    }

    /**
     * Returns a map of all the gauges in the registry and their names.
     *
     * @return all the gauges in the registry
     */
    @Override
    public SortedMap<String, Gauge> getGauges() {
        return getGauges(MetricFilter.ALL);
    }

    /**
     * Returns a map of all the gauges in the registry and their names which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the gauges in the registry
     */
    @Override
    public SortedMap<String, Gauge> getGauges(MetricFilter filter) {
        return getMetrics(Gauge.class, filter);
    }

    /**
     * Returns a map of all the counters in the registry and their names.
     *
     * @return all the counters in the registry
     */
    @Override
    public SortedMap<String, Counter> getCounters() {
        return getCounters(MetricFilter.ALL);
    }

    /**
     * Returns a map of all the counters in the registry and their names which match the given
     * filter.
     *
     * @param filter the metric filter to match
     * @return all the counters in the registry
     */
    @Override
    public SortedMap<String, Counter> getCounters(MetricFilter filter) {
        return getMetrics(Counter.class, filter);
    }

    /**
     * Returns a map of all the histograms in the registry and their names.
     *
     * @return all the histograms in the registry
     */
    @Override
    public SortedMap<String, Histogram> getHistograms() {
        return getHistograms(MetricFilter.ALL);
    }

    /**
     * Returns a map of all the histograms in the registry and their names which match the given
     * filter.
     *
     * @param filter the metric filter to match
     * @return all the histograms in the registry
     */
    @Override
    public SortedMap<String, Histogram> getHistograms(MetricFilter filter) {
        return getMetrics(Histogram.class, filter);
    }

    /**
     * Returns a map of all the meters in the registry and their names.
     *
     * @return all the meters in the registry
     */
    @Override
    public SortedMap<String, Meter> getMeters() {
        return getMeters(MetricFilter.ALL);
    }

    /**
     * Returns a map of all the meters in the registry and their names which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the meters in the registry
     */
    @Override
    public SortedMap<String, Meter> getMeters(MetricFilter filter) {
        return getMetrics(Meter.class, filter);
    }

    /**
     * Returns a map of all the timers in the registry and their names.
     *
     * @return all the timers in the registry
     */
    @Override
    public SortedMap<String, Timer> getTimers() {
        return getTimers(MetricFilter.ALL);
    }

    /**
     * Returns a map of all the timers in the registry and their names which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the timers in the registry
     */
    @Override
    public SortedMap<String, Timer> getTimers(MetricFilter filter) {
        return getMetrics(Timer.class, filter);
    }

    @SuppressWarnings("unchecked")
    private <T extends Metric> T getOrAdd(Metadata metadata, MetricBuilder<T> builder) {
        final Metric metric = metrics.get(metadata.getName());
        if (builder.isInstance(metric)) {
            return (T) metric;
        } else if (metric == null) {
            try {
                return register(metadata, builder.newMetric());
            } catch (IllegalArgumentException e) {
                final Metric added = metrics.get(metadata.getName());
                if (builder.isInstance(added)) {
                    return (T) added;
                }
            }
        }
        throw new IllegalArgumentException(metadata.getName() + " is already used for a different type of metric");
    }

    @SuppressWarnings("unchecked")
    private <T extends Metric> SortedMap<String, T> getMetrics(Class<T> klass, MetricFilter filter) {
        final TreeMap<String, T> timers = new TreeMap<String, T>();
        for (Map.Entry<String, Metric> entry : metrics.entrySet()) {
            if (klass.isInstance(entry.getValue()) && filter.matches(entry.getKey(),
                                                                     entry.getValue())) {
                timers.put(entry.getKey(), (T) entry.getValue());
            }
        }
        return Collections.unmodifiableSortedMap(timers);
    }

    @Override
    public Map<String, Metric> getMetrics() {
        return Collections.unmodifiableMap(metrics);
    }

    @Override
    public Map<String, Metadata> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    public Metadata getMetadata(String name) {
        return metadata.get(name);
    }

    /**
     * A quick and easy way of capturing the notion of default metrics.
     */
    private interface MetricBuilder<T extends Metric> {
        MetricBuilder<Counter> COUNTERS = new MetricBuilder<Counter>() {
            @Override
            public Counter newMetric() {
                return new CounterImpl();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Counter.class.isInstance(metric);
            }
        };

        MetricBuilder<Histogram> HISTOGRAMS = new MetricBuilder<Histogram>() {
            @Override
            public Histogram newMetric() {
                return new HistogramImpl(new ExponentiallyDecayingReservoir());
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Histogram.class.isInstance(metric);
            }
        };

        MetricBuilder<Meter> METERS = new MetricBuilder<Meter>() {
            @Override
            public Meter newMetric() {
                return new MeterImpl();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Meter.class.isInstance(metric);
            }
        };

        MetricBuilder<Timer> TIMERS = new MetricBuilder<Timer>() {
            @Override
            public Timer newMetric() {
                return new TimerImpl();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Timer.class.isInstance(metric);
            }
        };

        T newMetric();

        boolean isInstance(Metric metric);
    }

    /** {@inheritDoc} */
    @Override
    public HitCounter hitCounter(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public HitCounter hitCounter(Metadata metadata) {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public ParallelCounter parallelCounter(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public ParallelCounter parallelCounter(Metadata metadata) {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean unregister(String name, MetricType type) {
        // TODO Auto-generated method stub
        return remove(name);
    }

    /** {@inheritDoc} */
    @Override
    public boolean unregister(Metadata metadata) {
        // TODO Auto-generated method stub
        return remove(metadata.getName());
    }

    /** {@inheritDoc} */
    @Override
    public SortedSet<String> getNames(MetricType type) {
        // TODO Auto-generated method stub
        switch (type) {
            case COUNTER:
                return new TreeSet<>(getCounters().keySet());
            case GAUGE:
                return new TreeSet<>(getCounters().keySet());
            case HIT_COUNTER:
                return new TreeSet<>(getHitCounters().keySet());
            case HISTOGRAM:
                return new TreeSet<>(getHistograms().keySet());
            case METERED:
                return new TreeSet<>(getCounters().keySet());
            case PARALLEL_COUNTER:
                return new TreeSet<>(getCounters().keySet());
            case TIMER:
                return new TreeSet<>(getTimers().keySet());
            default:
                break;
        }
        return new TreeSet<>();
    }

    /** {@inheritDoc} */
    @Override
    public SortedMap<String, HitCounter> getHitCounters() {
        return getHitCounters(MetricFilter.ALL);
    }

    /** {@inheritDoc} */
    @Override
    public SortedMap<String, ParallelCounter> getParallelCounters() {
        return getParallelCounters(MetricFilter.ALL);
    }

    /** {@inheritDoc} */
    @Override
    public SortedMap<String, HitCounter> getHitCounters(MetricFilter filter) {
        return getMetrics(HitCounter.class, filter);
    }

    /** {@inheritDoc} */
    @Override
    public SortedMap<String, ParallelCounter> getParallelCounters(MetricFilter filter) {
        return getMetrics(ParallelCounter.class, filter);
    }
}
