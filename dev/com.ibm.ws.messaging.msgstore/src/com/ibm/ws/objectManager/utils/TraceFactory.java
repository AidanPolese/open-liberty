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
 * Make concrete instances of Trace and and control them.
 * 
 * @version @(#) 1/25/13
 * @author Andrew_Banks
 */
public abstract class TraceFactory {
    private static java.io.PrintWriter printWriter = null;

    // Names of components required to be active, JVM wide.
    protected static String activeNames = "";
    protected static int traceLevel = Trace.Level_None;
    protected static String fileName = "trace.txt";
    // The size of the ring buffer in bytes or lines depending on the implementation
    // for ringBuffer tracing, 0 means no ring buffer.
    protected static long ringBufferSize = 0;

    NLS nls;

    /**
     * Create a platform specific TraceFactory instance.
     * 
     * @param nls instance which holds the message catalogue to be used for info tracing.
     * @return TraceFactory instance loaded.
     */
    public static TraceFactory getTraceFactory(NLS nls) {
        return (TraceFactory) Utils.getImpl("com.ibm.ws.objectManager.utils.TraceFactoryImpl",
                                            new Class[] { NLS.class },
                                            new Object[] { nls });
    } // getTraceFactory().

    /**
     * Create a TraceFactory instance.
     * 
     * @param nls instance which holds the message catalog to be used for info tracing.
     */
    public TraceFactory(NLS nls) {
        this.nls = nls;
    } // TraceFactory().

    /**
     * Factory method to get the trace implementation.
     * 
     * @param sourceClass for which the Trace implementation is to be created.
     * @param traceGroup the name of the group to which the component belongs.
     * @return Trace implementation for the source class.
     */
    public abstract Trace getTrace(Class sourceClass, String traceGroup);

    /**
     * Specify what to trace and start tracing it.
     * 
     * @param activeNames of classes to be activated separated by ":"
     *            "*" represents any name.
     * @param traceLevel to be applied.
     * @throws java.io.IOException
     */
    public void setActiveTrace(String activeNames,
                               int traceLevel)
                    throws java.io.IOException {
        TraceFactory.activeNames = activeNames;
        TraceFactory.traceLevel = traceLevel;;
    }

    /**
     * Dump the current contents of the ring buffer to the current trace file.
     * 
     * @throws java.io.IOException
     */
    public void dumpRingBuffer()
                    throws java.io.IOException {
        // NOOP.
    }

    /**
     * @return String the fileName where trace is written.
     */
    public String getFileName()
    {
        return fileName;
    } // getFileName().

    /**
     * Set the name of the file where trace is to be written.
     * 
     * @param fileName of the file where trace is written.
     * @throws java.io.IOException
     */
    public void setFileName(String fileName)
                    throws java.io.IOException
    {
        TraceFactory.fileName = fileName;
    } // setFileName().

    /**
     * @return long the ringBufferSize in bytes. Zero means don't use a ring buffer.
     */
    public long getRingBufferSize()
    {
        return ringBufferSize;
    } // getRingBufferSize().

    /**
     * @param ringBufferSize of the rin buffer. Zero means don't use a ring buffer
     * @throws java.io.IOException
     */
    public void setRingBufferSize(long ringBufferSize)
                    throws java.io.IOException
    {
        TraceFactory.ringBufferSize = ringBufferSize;
    } // setRingBufferSize().

    /**
     * @return java.io.PrintWriter the printWriter.
     */
    public java.io.PrintWriter getPrintWriter()
    {
        return printWriter;
    }

    /**
     * Set a PrintWriter for trace output.
     * for example: setPrintWriter(new java.io.PrintWriter(System.out,true));
     * 
     * @param printWriter The printWriter to set, null disable output.
     */
    public void setPrintWriter(java.io.PrintWriter printWriter) {
        TraceFactory.printWriter = printWriter;
    }
} // class TraceFactory.