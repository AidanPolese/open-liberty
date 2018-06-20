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
package com.ibm.ws.microprofile.metrics2.writer;

import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.Histogram;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.Meter;
import org.eclipse.microprofile.metrics.Metric;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.Timer;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.microprofile.metrics.Constants;
import com.ibm.ws.microprofile.metrics.helper.PrometheusBuilder;
import com.ibm.ws.microprofile.metrics.writer.PrometheusMetricWriter;

/**
 *
 */
public class PrometheusMetricWriter2 extends PrometheusMetricWriter {

    private static final TraceComponent tc = Tr.register(PrometheusMetricWriter.class);

    public PrometheusMetricWriter2(Writer writer, Locale locale) {
        super(writer, locale);
    }

    @Override
    protected void writeMetricMapAsPrometheus(StringBuilder builder, String registryName, Map<String, Metric> metricMap, Map<String, Metadata> metricMetadataMap) {
        for (Entry<String, Metric> entry : metricMap.entrySet()) {
            String metricNamePrometheus = registryName + ":" + entry.getKey();
            Metric metric = entry.getValue();
            String entryName = entry.getKey();

            //description
            Metadata metricMetaData = metricMetadataMap.get(entryName);

            String description = "";

            if (metricMetaData.getDescription() == null || metricMetaData.getDescription().get().trim().isEmpty()) {
                description = "";
            } else {
                description = Tr.formatMessage(tc, locale, metricMetaData.getDescription().get());
            }

            String tags = metricMetaData.getTagsAsString();

            //appending unit to the metric name
            String unit = metricMetaData.getUnit().get();

            //Unit determination / translation
            double conversionFactor = 0;
            String appendUnit = null;

            if (unit == null || unit.trim().isEmpty() || unit.equals(MetricUnits.NONE)) {

                conversionFactor = Double.NaN;
                appendUnit = null;

            } else if (unit.equals(MetricUnits.NANOSECONDS)) {

                conversionFactor = Constants.NANOSECONDCONVERSION;
                appendUnit = Constants.APPENDEDSECONDS;

            } else if (unit.equals(MetricUnits.MICROSECONDS)) {

                conversionFactor = Constants.MICROSECONDCONVERSION;
                appendUnit = Constants.APPENDEDSECONDS;

            } else if (unit.equals(MetricUnits.SECONDS)) {

                conversionFactor = Constants.SECONDCONVERSION;
                appendUnit = Constants.APPENDEDSECONDS;

            } else if (unit.equals(MetricUnits.MINUTES)) {

                conversionFactor = Constants.MINUTECONVERSION;
                appendUnit = Constants.APPENDEDSECONDS;

            } else if (unit.equals(MetricUnits.HOURS)) {

                conversionFactor = Constants.HOURCONVERSION;
                appendUnit = Constants.APPENDEDSECONDS;

            } else if (unit.equals(MetricUnits.DAYS)) {

                conversionFactor = Constants.DAYCONVERSION;
                appendUnit = Constants.APPENDEDSECONDS;

            } else if (unit.equals(MetricUnits.PERCENT)) {

                conversionFactor = Double.NaN;
                appendUnit = Constants.APPENDEDPERCENT;

            } else if (unit.equals(MetricUnits.BYTES)) {

                conversionFactor = Constants.BYTECONVERSION;
                appendUnit = Constants.APPENDEDBYTES;

            } else if (unit.equals(MetricUnits.KILOBYTES)) {

                conversionFactor = Constants.KILOBYTECONVERSION;
                appendUnit = Constants.APPENDEDBYTES;

            } else if (unit.equals(MetricUnits.MEGABYTES)) {

                conversionFactor = Constants.MEGABYTECONVERSION;
                appendUnit = Constants.APPENDEDBYTES;

            } else if (unit.equals(MetricUnits.GIGABYTES)) {

                conversionFactor = Constants.GIGABYTECONVERSION;
                appendUnit = Constants.APPENDEDBYTES;

            } else if (unit.equals(MetricUnits.KILOBITS)) {

                conversionFactor = Constants.KILOBITCONVERSION;
                appendUnit = Constants.APPENDEDBYTES;

            } else if (unit.equals(MetricUnits.MEGABITS)) {

                conversionFactor = Constants.MEGABITCONVERSION;
                appendUnit = Constants.APPENDEDBYTES;

            } else if (unit.equals(MetricUnits.GIGABITS)) {

                conversionFactor = Constants.GIGABITCONVERSION;
                appendUnit = Constants.APPENDEDBYTES;

            } else if (unit.equals(MetricUnits.KIBIBITS)) {

                conversionFactor = Constants.KIBIBITCONVERSION;
                appendUnit = Constants.APPENDEDBYTES;

            } else if (unit.equals(MetricUnits.MEBIBITS)) {

                conversionFactor = Constants.MEBIBITCONVERSION;
                appendUnit = Constants.APPENDEDBYTES;

            } else if (unit.equals(MetricUnits.GIBIBITS)) {

                conversionFactor = Constants.GIBIBITCONVERSION;
                appendUnit = Constants.APPENDEDBYTES;

            } else if (unit.equals(MetricUnits.MILLISECONDS)) {

                conversionFactor = Constants.MILLISECONDCONVERSION;
                appendUnit = Constants.APPENDEDSECONDS;

            } else {

                conversionFactor = Double.NaN;
                appendUnit = "_" + unit;
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
                Tr.event(tc, "Metric type '" + metric.getClass() + " for " + entryName + " is invalid.");
            }
        }
    }

}
