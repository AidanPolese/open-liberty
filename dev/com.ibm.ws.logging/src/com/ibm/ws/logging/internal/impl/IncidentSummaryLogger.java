/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2006, 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging.internal.impl;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * An IncidentStreamImpl is a lightweight implementation of an Incident stream
 * 
 */
public final class IncidentSummaryLogger {
    public void logIncidentSummary(OutputStream os, List<IncidentImpl> incidents) {
        PrintStream ps = new PrintStream(os);
        try {
            ps.println();
            ps.println(" Index  Count  Time of first Occurrence    Time of last Occurrence     Exception SourceId ProbeId");
            ps.println("------+------+---------------------------+---------------------------+---------------------------");
            int i = -1;
            for (IncidentImpl incident : incidents) {
                ++i;
                ps.println(incident.formatSummaryEntry(i));
            }
            ps.println("------+------+---------------------------+---------------------------+---------------------------");
        } finally {
            ps.flush();
        }
    }
}