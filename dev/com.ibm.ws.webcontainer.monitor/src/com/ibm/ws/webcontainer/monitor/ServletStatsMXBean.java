/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.monitor;

import com.ibm.websphere.monitor.meters.Counter;
import com.ibm.websphere.monitor.meters.StatisticsMeter;

/**
 * Servlet Stats MXBean
 * 
 */
public interface ServletStatsMXBean extends com.ibm.websphere.webcontainer.ServletStatsMXBean {

    @Override
    public Counter getRequestCountDetails();

    @Override
    public StatisticsMeter getResponseTimeDetails();
}
