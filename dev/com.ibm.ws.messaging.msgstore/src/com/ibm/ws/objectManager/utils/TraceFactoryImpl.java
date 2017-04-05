package com.ibm.ws.objectManager.utils;

/*
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2013
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 *
 * ============================================================================
 */

/**
 * @author Andrew_Banks
 * 
 *         Make concrete instances of Trace.
 */
public class TraceFactoryImpl
                extends TraceFactory {

    /**
     * @param nls for info tracing.
     */
    public TraceFactoryImpl(NLS nls) {
        super(nls);
    } // TraceFactoryImpl().

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.objectManager.utils.TraceFactory#getTrace(java.lang.Class, java.lang.String)
     */
    public Trace getTrace(Class sourceClass, String traceGroup) {
        return new TraceImpl(sourceClass, traceGroup, this);
    } // getTrace().

} // class TraceFactoryImpl.