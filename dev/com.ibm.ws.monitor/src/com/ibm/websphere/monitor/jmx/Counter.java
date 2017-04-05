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

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * A counter is used to track and record metrics related to counted items
 * such as processed requests, bytes read, or bytes written. Counted data
 * <em>must</em> be monotonically increasing. Counters cannot appear to
 * move backwards except on reset or wrap.
 * 
 * @ibm-api
 */
@Trivial
public class Counter extends Meter {

    long currentValue;
    CounterReading reading;

    /**
     * Default constructor.
     */
    public Counter() {
        super();
    }

    /**
     * Constructor used during construction of proxy objects for MXBeans.
     */
    @ConstructorProperties({ "reading", "currentValue", "description", "unit" })
    public Counter(CounterReading reading, long currentValue, String description, String unit) {
        this.reading = reading;
        this.currentValue = currentValue;

        setDescription(description);
        setUnit(unit);
    }

    /**
     * @return current counter value
     */
    public synchronized long getCurrentValue() {
        return currentValue;
    }

    /**
     * @return a snapshot of the counter value
     */
    public CounterReading getReading() {
        return reading;
    }

    /**
     * @return description of the counter
     */
    @Override
    public String getDescription() {
        return super.getDescription();
    }

    /**
     * @return unit of measurement for the counter
     */
    @Override
    public String getUnit() {
        return super.getUnit();
    }

}
