/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.zos.logging.internal;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * Class to hold data to be traced, the description of the data, and a
 * preferred formatted representation of the data for trace.
 */
@Trivial
class TracedData {
    final int traceKey;
    final String description;
    final Object item;
    final String formatted;

    TracedData(int traceKey, String description, Object item, String formatted) {
        this.traceKey = traceKey;
        this.description = description;
        this.item = item;
        this.formatted = formatted;
    }

    int getTraceKey() {
        return traceKey;
    }

    String getDescription() {
        return description;
    }

    Object getItem() {
        return item;
    }

    public String getFormatted() {
        return formatted;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("  ");
        sb.append(description);
        sb.append(": ").append(formatted);
        return sb.toString();
    }
}