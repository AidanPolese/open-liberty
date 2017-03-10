/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging;

/**
 * A WsTraceHandler receives messages and LogRecords, and logs them.
 */
public interface WsTraceHandler {

    /**
     * Log the given log record.
     * 
     * @param routedMessage The LogRecord along with various message formats.
     */
    void publish(RoutedMessage routedMessage);
}
