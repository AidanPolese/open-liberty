/*
 * @(#) 1.1 SERV1/ws/code/runtime.fw/src/com/ibm/ws/exception/WsRuntimeFwExceptionUtil.java, WAS.runtime.fw, WAS80.SERV1, kk1041.02 3/13/08 12:59:49 [10/22/10 00:56:57]
 *
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2008
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * CHANGE HISTORY:
 *
 * Reason         Version Date         User id   Description
 * ----------------------------------------------------------------------------
 * 420403.1       7.0     13-Mar-2008  bkail     New
 */

package com.ibm.ws.exception;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;

/**
 * Runtime framework internal use only. This class is intended to avoid
 * redundantly printing exception stack traces as <tt>WsRuntimeFwException</tt>s
 * pass through the runtime framework. Ideally,
 * the runtime framework would not print these exceptions at all, instead
 * relying on the components that throw them to print a message if necessary.
 */
public class WsRuntimeFwExceptionUtil {
    private static final TraceComponent tc = Tr.register(WsRuntimeFwException.class, "Runtime");

    private WsRuntimeFwExceptionUtil() {}

    /**
     * Sets the reported status of the exception to <tt>true</tt> and returns <tt>true</tt> if this exception should be reported.
     * 
     * @return <tt>true</tt> if this exception should be printed in a message
     */
    public static boolean report(WsRuntimeFwException ex) {
        if (ex.reported) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "previously reported exception", new Exception(ex));
            }
            return false;
        }

        ex.reported = true;
        return true;
    }

    /**
     * Sets the reported status of the exception to the specified value.
     * 
     * @param reported
     *            the reported status
     * @see #report()
     */
    public static void setReported(WsRuntimeFwException ex, boolean reported) {
        ex.reported = reported;
    }
}
