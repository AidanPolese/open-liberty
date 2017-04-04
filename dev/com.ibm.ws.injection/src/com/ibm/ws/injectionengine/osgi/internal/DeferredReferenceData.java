/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.injectionengine.osgi.internal;

import java.io.PrintWriter;

import com.ibm.wsspi.injectionengine.InjectionException;

/**
 * Represents a set of reference data that can be processed just-in-time to
 * provide non-java:comp references.
 */
public interface DeferredReferenceData {
    /**
     * Processes any deferred reference data.
     *
     * @return true if any reference data was successfully processed
     * @throws InjectionException
     */
    boolean processDeferredReferenceData() throws InjectionException;

    /**
     * Method gets called when dump gets executed on a server, it will
     * traverse through the DeferredReferenceData and output useful data
     * that can help the user understand the current configuration of
     * the Java: Namespace
     *
     * @param writer the writer used to output
     * @param indent a String containing single or multiples "\t" for indenting purposes
     */
    void introspectDeferredReferenceData(PrintWriter writer, String indent);
}
