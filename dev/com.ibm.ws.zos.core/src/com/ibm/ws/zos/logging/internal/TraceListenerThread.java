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

/**
 * Inner class for spawning and managing a separate thread to interact with
 * the native code to receive traces written by native code.
 */
class TraceListenerThread extends Thread {

    /**
     * The trace handler that we'll delegate to.
     */
    private final NativeTraceHandler nativeTraceHandler;

    /**
     * Flag that's checked to see if we're terminating.
     */
    private volatile boolean keepGoing = true;

    private long threadData_ptr;

    /**
     * Constructor.
     *
     * @param nativeTraceHandler the trace handler we delegate to
     */
    TraceListenerThread(NativeTraceHandler nativeTraceHandler) {
        this.nativeTraceHandler = nativeTraceHandler;
        this.setDaemon(true);
        this.setName("z/OS Native Trace Processing");
    }

    @Override
    public void run() {
        threadData_ptr = nativeTraceHandler.ntv_getThreadData();
        if (threadData_ptr == 0) {
            /* TODO tr.error or maybe ffdc */
        } else {
            while (keepGoing) {
                try {
                    byte[] outputArea = null;
                    try {
                        outputArea = nativeTraceHandler.ntv_getTraces(threadData_ptr);
                    } catch (Throwable t) {
                    }

                    NativeTraceData traceData = new NativeTraceData(outputArea);

                    nativeTraceHandler.writeNativeTrace(traceData.getTraceLevel(),
                                                        traceData.getTracePoint(),
                                                        traceData.getVarargListPointer(),
                                                        traceData.getCreateTime(),
                                                        traceData.getCreatingTcb(),
                                                        traceData.getCreateState(),
                                                        traceData.getCreateKey());
                    // wake up thread that requested the trace
                    nativeTraceHandler.ntv_traceWritten(threadData_ptr);
                } catch (Exception e) {
                }
            }
        }
    }

    public void end() {
        // Need to wake this thread up from a Native wait.
        keepGoing = false;
        nativeTraceHandler.ntv_stopListeningForTraces(threadData_ptr);
    } /* end end() */
} /* end TraceListenerThread */