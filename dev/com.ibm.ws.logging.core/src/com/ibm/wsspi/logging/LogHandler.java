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
package com.ibm.wsspi.logging;

import java.util.logging.LogRecord;

/**
 * A LogHandler receives messages and LogRecords, and logs them.
 */
public interface LogHandler {

    /**
     * Log the given log record.
     * 
     * @param msg The fully formatted message, derived from the given LogRecord.
     * @param logRecord The LogRecord.
     */
    void publish(String msg, LogRecord logRecord);
}
