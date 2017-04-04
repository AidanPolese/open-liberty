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

public class CounterReading extends com.ibm.websphere.monitor.jmx.CounterReading {

    public CounterReading(long count, String unit) {
        super(System.currentTimeMillis(), count, unit);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("count = ").append(count).append(" ").append(unit);
        sb.append(" at ").append(new Date(timestamp).toString());
        return sb.toString();
    }
}
