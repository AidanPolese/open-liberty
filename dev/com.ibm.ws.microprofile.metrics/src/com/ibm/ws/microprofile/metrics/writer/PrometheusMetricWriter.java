/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.microprofile.metrics.writer;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.Histogram;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.Meter;
import org.eclipse.microprofile.metrics.Metric;
import org.eclipse.microprofile.metrics.MetricUnit;
import org.eclipse.microprofile.metrics.Timer;

import com.ibm.ws.microprofile.metrics.Constants;
import com.ibm.ws.microprofile.metrics.exceptions.EmptyRegistryException;
import com.ibm.ws.microprofile.metrics.exceptions.NoSuchMetricException;
import com.ibm.ws.microprofile.metrics.exceptions.NoSuchRegistryException;
import com.ibm.ws.microprofile.metrics.helper.PrometheusBuilder;
import com.ibm.ws.microprofile.metrics.helper.Tag;
import com.ibm.ws.microprofile.metrics.helper.Util;

/**
 *
 */
public class PrometheusMetricWriter implements OutputWriter {

    private final Writer writer;

    public PrometheusMetricWriter(Writer writer) {
        this.writer = writer;
    }

    /**
     * {@inheritDoc}
     *
     * @throws EmptyRegistryException
     */
    @Override
    public void write(String registryName, String metricName) throws NoSuchMetricException, NoSuchRegistryException, IOException, EmptyRegistryException {
        StringBuilder builder = new StringBuilder();
        writeMetricsAsPrometheus(builder, registryName, metricName);
        serialize(builder);
    }

    /** {@inheritDoc} */
    @Override
    public void write(String registryName) throws NoSuchRegistryException, EmptyRegistryException, IOException {
        StringBuilder builder = new StringBuilder();
        writeMetricsAsPrometheus(builder, registryName);
        serialize(builder);
    }

    /** {@inheritDoc} */
    @Override
    public void write() throws IOException {
        StringBuilder builder = new StringBuilder();
        for (String registryName : Constants.REGISTRY_NAMES_LIST) {
            try {
                writeMetricsAsPrometheus(builder, registryName);
            } catch (NoSuchRegistryException e) { // Ignore
            } catch (EmptyRegistryException e) { // Ignore
            }
        }
        serialize(builder);
    }

    private void writeMetricsAsPrometheus(StringBuilder builder, String registryName) throws NoSuchRegistryException, EmptyRegistryException {
        writeMetricMapAsPrometheus(builder, registryName, Util.getMetricsAsMap(registryName), Util.getMetricsMetadataAsMap(registryName));
    }

    private void writeMetricsAsPrometheus(StringBuilder builder, String registryName,
                                          String metricName) throws NoSuchRegistryException, NoSuchMetricException, EmptyRegistryException {
        writeMetricMapAsPrometheus(builder, registryName, Util.getMetricsAsMap(registryName, metricName), Util.getMetricsMetadataAsMap(registryName));
    }

    private void writeMetricMapAsPrometheus(StringBuilder builder, String registryName, Map<String, Metric> metricMap, Map<String, Metadata> metricMetadataMap) {
        for (Entry<String, Metric> entry : metricMap.entrySet()) {
            String metricNamePrometheus = registryName + ":" + entry.getKey();
            Metric metric = entry.getValue();
            String entryName = entry.getKey();

            //description
            Metadata metricMetaData = metricMetadataMap.get(entryName);
            String description = metricMetaData.getDescription();

            HashMap<String, String> extractedTags = metricMetaData.getTags();
            ArrayList<Tag> tags = new ArrayList<Tag>();

            for (HashMap.Entry<String, String> e : extractedTags.entrySet()) {
                Tag t = new Tag(e.getKey(), e.getValue());
                tags.add(t);
            }

            //appending unit to the metric name
            String unit = metricMetaData.getUnit();

            //Unit determination / translation
            double conversionFactor = 0;
            String appendUnit = null;

            if (unit.equals(MetricUnit.NANOSECONDS)) {

                conversionFactor = Constants.NANOSECONDCONVERSION;
                appendUnit = Constants.APPENDEDSECONDS;

            } else if (unit.equals(MetricUnit.MICROSECONDS)) {

                conversionFactor = Constants.MICROSECONDCONVERSION;
                appendUnit = Constants.APPENDEDSECONDS;

            } else if (unit.equals(MetricUnit.SECONDS)) {

                conversionFactor = Constants.SECONDCONVERSION;
                appendUnit = Constants.APPENDEDSECONDS;

            } else if (unit.equals(MetricUnit.MINUTES)) {

                conversionFactor = Constants.MINUTECONVERSION;
                appendUnit = Constants.APPENDEDSECONDS;

            } else if (unit.equals(MetricUnit.HOURS)) {

                conversionFactor = Constants.HOURCONVERSION;
                appendUnit = Constants.APPENDEDSECONDS;

            } else if (unit.equals(MetricUnit.DAYS)) {

                conversionFactor = Constants.DAYCONVERSION;
                appendUnit = Constants.APPENDEDSECONDS;

            } else if (unit.equals(MetricUnit.PERCENT)) {

                conversionFactor = Double.NaN;
                appendUnit = Constants.APPENDEDPERCENT;

            } else if (unit.equals(MetricUnit.BYTES)) {

                conversionFactor = Constants.BYTECONVERSION;
                appendUnit = Constants.APPENDEDBYTES;

            } else if (unit.equals(MetricUnit.KILOBYTES)) {

                conversionFactor = Constants.KILOBYTECONVERSION;
                appendUnit = Constants.APPENDEDBYTES;

            } else if (unit.equals(MetricUnit.MEGABYTES)) {

                conversionFactor = Constants.MEGABYTECONVERSION;
                appendUnit = Constants.APPENDEDBYTES;

            } else if (unit.equals(MetricUnit.GIGABYTES)) {

                conversionFactor = Constants.GIGABYTECONVERSION;
                appendUnit = Constants.APPENDEDBYTES;

            } else if (unit.equals(MetricUnit.KILOBITS)) {

                conversionFactor = Constants.KILOBITCONVERSION;
                appendUnit = Constants.APPENDEDBYTES;

            } else if (unit.equals(MetricUnit.MEGABITS)) {

                conversionFactor = Constants.MEGABITCONVERSION;
                appendUnit = Constants.APPENDEDBYTES;

            } else if (unit.equals(MetricUnit.GIGABITS)) {

                conversionFactor = Constants.GIGABITCONVERSION;
                appendUnit = Constants.APPENDEDBYTES;

            } else if (unit.equals(MetricUnit.KIBIBITS)) {

                conversionFactor = Constants.KIBIBITCONVERSION;
                appendUnit = Constants.APPENDEDBYTES;

            } else if (unit.equals(MetricUnit.MEBIBITS)) {

                conversionFactor = Constants.MEBIBITCONVERSION;
                appendUnit = Constants.APPENDEDBYTES;

            } else if (unit.equals(MetricUnit.GIBIBITS)) {

                conversionFactor = Constants.GIBIBITCONVERSION;
                appendUnit = Constants.APPENDEDBYTES;

            } else if (unit.equals(MetricUnit.NONE)) {

                conversionFactor = Double.NaN;
                appendUnit = null;

            } else if (unit.equals(MetricUnit.MILLISECONDS)) {
                conversionFactor = Constants.MILLISECONDCONVERSION;
                appendUnit = Constants.APPENDEDSECONDS;
            } else {

                throw new RuntimeException("Unsupported Unit Type");
            }

            if (Counter.class.isInstance(metric)) {
                PrometheusBuilder.buildCounter(builder, metricNamePrometheus, (Counter) metric, description, tags);
            } else if (Gauge.class.isInstance(metric)) {
                PrometheusBuilder.buildGauge(builder, metricNamePrometheus, (Gauge) metric, description, conversionFactor, tags, appendUnit);
            } else if (Timer.class.isInstance(metric)) {
                PrometheusBuilder.buildTimer(builder, metricNamePrometheus, (Timer) metric, description, tags);
            } else if (Histogram.class.isInstance(metric)) {
                PrometheusBuilder.buildHistogram(builder, metricNamePrometheus, (Histogram) metric, description, conversionFactor, tags, appendUnit);
            } else if (Meter.class.isInstance(metric)) {
                PrometheusBuilder.buildMeter(builder, metricNamePrometheus, (Meter) metric, description, tags);
            } else {
                throw new RuntimeException("Unsupported Metric Type");
            }
        }
    }

    private void serialize(StringBuilder builder) throws IOException {
        try {
            writer.write(builder.toString());
        } finally {
            writer.close();
        }
    }
}
