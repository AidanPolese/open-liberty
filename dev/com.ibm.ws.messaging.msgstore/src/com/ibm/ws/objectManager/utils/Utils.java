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
 */
/**
 * @author Andrew_Banks
 * 
 *         Load implementations of utils.
 *         Hold the TraceFactory for utils.
 *         Not traced because Trace depends on this.
 */
class Utils {
    protected static final NLS nls = new NLSImpl(UtilsConstants.MSG_BUNDLE);
    public static final TraceFactory traceFactory = new TraceFactoryImpl(nls);
    public static final FFDC ffdc = new FFDCImpl();

    /**
     * Create a platform specific instance of a utils class.
     * 
     * @param className the simple name of the class whois implementation is to be found.
     * @param types used to select the constructor.
     * @param args used to invoke the constructor.
     * 
     * @return Object the utils class loaded.
     */
    protected static Object getImpl(String className, Class[] types, Object[] args) {
        // No tracing as this is used to load the trace factory.

        Object Impl; // For return.
        try {
            Class classToInstantiate = Class.forName(className);
            java.lang.reflect.Constructor constructor = classToInstantiate.getDeclaredConstructor(types);
            constructor.setAccessible(true);
            Impl = constructor.newInstance(args);

        } catch (Exception exception) {
            // No FFDC Code Needed.
            // We may not have any FFDC instantiated so simply print the stack. 
            exception.printStackTrace(new java.io.PrintWriter(System.out, true));
            // Assume we have no chained exception support.
            throw new Error(exception.toString());
        } // catch.

        return Impl;
    } // getImpl().
} // class Utils.
