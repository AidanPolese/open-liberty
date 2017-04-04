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

import java.util.Date;

public final class GaugeReading {

    final long timestamp;

    final long currentValue;
    final long minimumValue;
    final long maximumValue;

    final boolean bounded;
    final long lowerBound;
    final long upperBound;

    final String unit;

    public GaugeReading(long currentValue, long minimumValue, long maximumValue, boolean bounded, long lowerBound, long upperBound, String unit) {
        this.timestamp = System.currentTimeMillis();
        this.currentValue = currentValue;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.bounded = bounded;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.unit = unit;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getCurrentValue() {
        return currentValue;
    }

    public long getMinimumValue() {
        return minimumValue;
    }

    public long getMaximumValue() {
        return maximumValue;
    }

    public boolean isBounded() {
        return bounded;
    }

    public long getLowerBound() {
        return lowerBound;
    }

    public long getUpperBound() {
        return upperBound;
    }

    public String getUnit() {
        return unit;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("current = ").append(currentValue);
        sb.append(" min = ").append(minimumValue);
        sb.append(" max = ").append(maximumValue);
        if (bounded) {
            sb.append(" lower bound = ").append(lowerBound);
            sb.append(" upper bound = ").append(upperBound);
        }
        sb.append(" ").append(unit);
        sb.append(" at ").append(new Date(timestamp).toString());
        return sb.toString();
    }

}
