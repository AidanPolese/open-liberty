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

/**
 * Abstract base class that serves as the foundation for all meter
 * implementations.
 */
public abstract class Meter {

    /**
     * The unit of measurement for this meter.
     */
    String unit = "UNKNOWN";

    /**
     * A description of the recorded metric.
     */
    String description = null;

    public Meter() {}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

}
