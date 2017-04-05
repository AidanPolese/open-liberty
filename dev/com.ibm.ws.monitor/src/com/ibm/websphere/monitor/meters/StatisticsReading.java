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

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class StatisticsReading extends com.ibm.websphere.monitor.jmx.StatisticsReading {

    public StatisticsReading(long count, long min, long max, double total, double mean, double variance, double stddev, String unit) {
        super(System.currentTimeMillis(), count, min, max, total, mean, variance, stddev, unit);
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat("0.000");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        StringBuilder sb = new StringBuilder();

        sb.append("count=").append(count);
        sb.append(" min=").append(minimumValue);
        sb.append(" max=").append(maximumValue);
        sb.append(" mean=").append(decimalFormat.format(mean));
        sb.append(" variance=").append(decimalFormat.format(variance));
        sb.append(" stddev=").append(decimalFormat.format(standardDeviation));
        sb.append(" total=").append(Math.round(total));

        return sb.toString();
    }
}
