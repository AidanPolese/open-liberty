/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corporation 2011, 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.wsspi.anno.targets;

import java.text.MessageFormat;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

public class AnnotationTargets_Exception extends Exception {
    private static final long serialVersionUID = 1L;

    public static final String CLASS_NAME = AnnotationTargets_Exception.class.getName();

    //

    public AnnotationTargets_Exception(String message) {
        super(message);
    }

    public AnnotationTargets_Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public static AnnotationTargets_Exception wrap(TraceComponent logger,
                                                   String callingClassName,
                                                   String callingMethodName,
                                                   String message, Throwable th) {
        AnnotationTargets_Exception wrappedException = new AnnotationTargets_Exception(message, th);

        if (logger.isDebugEnabled()) {
            Tr.debug(logger, MessageFormat.format(" [ {0} ] [ {1} ] Wrap [ {2} ] as [ {3} ]",
                                                  callingClassName, callingMethodName,
                                                  th.getClass().getName(),
                                                  wrappedException.getClass().getName()));

            Tr.debug(logger, th.getMessage(), th);
            Tr.debug(logger, message, wrappedException);
        }

        return wrappedException;
    }
}
