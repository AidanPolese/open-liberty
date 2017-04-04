/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.websphere.monitor.meters;

import java.util.concurrent.atomic.AtomicLong;

public final class Gauge extends Meter implements GaugeMXBean {

    private boolean observedFirstValue = false;

    private boolean bounded = false;

    long lowerBound = Long.MIN_VALUE;

    long upperBound = Long.MAX_VALUE;

    final AtomicLong currentValue = new AtomicLong();

    final AtomicLong maximumValue = new AtomicLong(Long.MIN_VALUE);

    final AtomicLong minimumValue = new AtomicLong(Long.MAX_VALUE);

    public Gauge() {
        super();
    }

    public long getCurrentValue() {
        return currentValue.get();
    }

    public long getMaximumValue() {
        if (observedFirstValue) {
            return maximumValue.get();
        }
        return 0;
    }

    public long getMinimumValue() {
        if (observedFirstValue) {
            return minimumValue.get();
        }
        return 0;
    }

    public void incrementCurrentValue(long increment) {
        long updated = currentValue.addAndGet(increment);
        updateMinMax(updated);
    }

    public void decrementCurrentValue(long decrement) {
        long updated = currentValue.addAndGet(-decrement);
        updateMinMax(updated);
    }

    public boolean compareAndSetCurrentValue(long expected, long updated) {
        boolean success = currentValue.compareAndSet(expected, updated);
        if (success) {
            updateMinMax(updated);
        }
        return success;
    }

    public void setCurrentValue(long value) {
        currentValue.set(value);
        updateMinMax(value);
    }

    public void setMaximumValue(long value) {
        maximumValue.set(value);
        updateMinMax(value);
    }

    public void setMinimumValue(long value) {
        minimumValue.set(value);
        updateMinMax(value);
    }

    public long getLowerBound() {
        return lowerBound;
    }

    public long getUpperBound() {
        return upperBound;
    }

    public boolean isBounded() {
        return bounded;
    }

    public void setBounds(long lowerBound, long upperBound) {
        this.lowerBound = Math.min(lowerBound, upperBound);
        this.upperBound = Math.max(lowerBound, upperBound);
    }

    private void updateMinMax(long updated) {
        long currentMin = minimumValue.get();
        while (updated < currentMin) {
            minimumValue.compareAndSet(currentMin, updated);
            currentMin = minimumValue.get();
        }

        long currentMax = maximumValue.get();
        while (updated > currentMax) {
            maximumValue.compareAndSet(currentMax, updated);
            currentMax = maximumValue.get();
        }

        if (!observedFirstValue) {
            observedFirstValue = true;
        }
    }

    public GaugeReading getReading() {
        long current = getCurrentValue();
        long min = Math.min(current, getMinimumValue());
        long max = Math.max(current, getMaximumValue());
        return new GaugeReading(current, min, max, bounded, lowerBound, upperBound, getUnit());
    }

    public String toString() {
        GaugeReading reading = getReading();

        StringBuilder sb = new StringBuilder();
        sb.append("current value = ").append(reading.currentValue);
        sb.append(" minimum value = ").append(reading.minimumValue);
        sb.append(" maximum value = ").append(reading.maximumValue);
        if (reading.bounded) {
            sb.append(" lower bound = ").append(reading.lowerBound);
            sb.append(" upper bound = ").append(reading.upperBound);
        }

        return sb.toString();
    }
}
