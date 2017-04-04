/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corporation 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.wsspi.anno.util;

import java.text.MessageFormat;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

public class Util_Exception extends Exception {
    private static final long serialVersionUID = 1L;

    public static final String CLASS_NAME = Util_Exception.class.getName();

    //

    public Util_Exception(String message) {
        super(message);
    }

    public Util_Exception(String message, Throwable cause) {
        super(message, cause);
    }

    //

    public static Util_Exception wrap(TraceComponent tc, String callingClassName,
                                      String callingMethodName, String message, Throwable th) {

        Util_Exception wrappedException = new Util_Exception(message, th);

        if (tc.isEventEnabled()) {

            Tr.event(tc, MessageFormat.format("[ {0} ] [ {1} ] Wrap [ {2} ] as [ {3} ]",
                                              new Object[] { callingClassName, callingMethodName,
                                                            th.getClass().getName(),
                                                            wrappedException.getClass().getName() }));

            Tr.event(tc, th.getMessage(), th);
            Tr.event(tc, message, wrappedException);
        }

        return wrappedException;
    }
}
