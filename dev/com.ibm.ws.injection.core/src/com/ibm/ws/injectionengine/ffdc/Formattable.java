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
package com.ibm.ws.injectionengine.ffdc;

import com.ibm.ws.ffdc.IncidentStream;

/**
 * This interface specifies how a class can customize the display
 * of it's instances within an ffdc incident report.
 */
public interface Formattable
{
    /**
     * Emit the customized human readable text to represent this object
     *
     * @param is the incident stream, the data will be written here
     */
    void formatTo(IncidentStream is);
}
