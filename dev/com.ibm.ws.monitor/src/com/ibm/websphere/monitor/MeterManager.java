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

package com.ibm.websphere.monitor;

import java.util.Collection;

import com.ibm.websphere.monitor.meters.CounterReading;
import com.ibm.websphere.monitor.meters.GaugeReading;
import com.ibm.websphere.monitor.meters.StatisticsReading;

public interface MeterManager {

    public CounterReading getCounterReading(String counterName);

    public GaugeReading getGaugeReading(String gaugeName);

    public StatisticsReading getStatisticsReading(String statisticsMeterName);

    public MeterType getMeterType(String meterName);

    public Collection<String> getMeterNames();
}
