/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.zos.logging.internal;

import java.util.Vector;

/**
 * holds the last 50 errors encountered by a log handler
 */
public class LoggingHandlerDiagnosticsVector {

    protected static final int VECTOR_LIMIT = 50;

    /**
     * vector of errors
     */
    protected Vector<LoggingHandlerDianostics> savedDiagnostics = null;

    /**
     * constructor
     */
    public LoggingHandlerDiagnosticsVector() {
        savedDiagnostics = new Vector<LoggingHandlerDianostics>();
    }

    /**
     * Inserts the specified element at the beginning of the Vector.
     * If the number of components in this vector is greater than or equal to 50, the last element is removed
     * before the specified one is added at the beginning.
     * 
     */
    public void insertElementAtBegining(String englishMsg, int writeReturnCode) {
        // only save last 50. do not want a storage leak storage if we keep failing
        if (savedDiagnostics.size() >= VECTOR_LIMIT) {
            savedDiagnostics.removeElementAt(savedDiagnostics.size() - 1);
        }
        savedDiagnostics.add(0, new LoggingHandlerDianostics(englishMsg, writeReturnCode));
    }

}

/**
 * hold errors encountered by a log handler
 */
class LoggingHandlerDianostics {

    protected String msg;
    protected int rc;

    /**
     * @param message
     * @param returnCode
     * @param throwable
     */
    public LoggingHandlerDianostics(String message, int returnCode) {
        msg = message;
        rc = returnCode;
    }

}
