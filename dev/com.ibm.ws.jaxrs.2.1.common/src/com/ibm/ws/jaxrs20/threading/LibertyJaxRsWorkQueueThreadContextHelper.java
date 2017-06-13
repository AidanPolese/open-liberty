/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.threading;

/**
 *
 */
public class LibertyJaxRsWorkQueueThreadContextHelper {

    private static ThreadLocal<LibertyJaxRsWorkQueueThreadContext> threadLocal = new ThreadLocal<LibertyJaxRsWorkQueueThreadContext>() {

        @Override
        protected LibertyJaxRsWorkQueueThreadContext initialValue() {
            LibertyJaxRsWorkQueueThreadContext wqtc = new LibertyJaxRsWorkQueueThreadContext();
            LibertyJaxRsWorkQueueThreadContextHelper.setThreadContext(wqtc);
            return wqtc;
        }
    };

    public static void setThreadContext(LibertyJaxRsWorkQueueThreadContext wqtc) {
        threadLocal.set(wqtc);
    }

    public static LibertyJaxRsWorkQueueThreadContext getThreadContext() {
        return threadLocal.get();
    }

    public static void destroyThreadContext() {
        threadLocal.remove();
    }
}
