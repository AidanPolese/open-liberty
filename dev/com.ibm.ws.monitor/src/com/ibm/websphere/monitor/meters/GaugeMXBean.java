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

public interface GaugeMXBean {

    public String getDescription();

    public String getUnit();

    public long getCurrentValue();

    public long getMinimumValue();

    public long getMaximumValue();

    public boolean isBounded();

    public long getLowerBound();

    public long getUpperBound();

    public GaugeReading getReading();

}
