/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.websphere.monitor.jmx;

import java.beans.ConstructorProperties;

/**
 * Represents a snapshot of a {@link Counter}. A CounterReading holds the current value of the Counter at the time
 * it was obtained and will not change.
 * 
 * @ibm-api
 */
public class CounterReading {

    protected long timestamp;
    protected long count;
    protected String unit;

    /**
     * Constructor used during construction of proxy objects for MXBeans.
     */
    @ConstructorProperties({ "timestamp", "count", "unit" })
    public CounterReading(long timestamp, long count, String unit) {
        this.timestamp = timestamp;
        this.count = count;
        this.unit = unit;
    }

    /**
     * @return timestamp of the counter reading
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @return counter value at time of snapshot
     */
    public long getCount() {
        return count;
    }

    /**
     * @return unit of measurement of the counter
     */
    public String getUnit() {
        return unit;
    }
}
