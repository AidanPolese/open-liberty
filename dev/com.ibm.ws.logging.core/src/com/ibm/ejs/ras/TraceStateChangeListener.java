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
package com.ibm.ejs.ras;

/**
 * The TraceStateChangeListener interface is provided to allow other logging
 * packages that utilize the systems management and TraceComponent aspects of Tr
 * to be informed when the trace state changes.
 * 
 * Typically a logging package that is implemented on top of Tr will map a trace
 * domain to a Tr <code>TraceComponent</code> and will map that packages trace
 * types to the existing Tr types. JRas for example maps a Jras Logger to a
 * Trace component using a common name. Therefore JRas Loggers will show up in
 * the systems management GUI as trace objects that are indistinguishable from
 * normal Tr TraceComponents. When the user enables trace for a JRas logger, the
 * logger must be called back to translate the Tr trace enable event to the JRas
 * trace type and set the JRas loggers trace mask accordingly.
 */
public interface TraceStateChangeListener extends com.ibm.websphere.ras.TraceStateChangeListener {
    /**
     * Inform the object implementing this interface that the Tr trace state
     * managed by the TraceComponent object has changed
     */
    @Override
    public void traceStateChanged();

}
