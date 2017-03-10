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
 * Message routing service. Routes messages to sundry logging streams.
 */
public interface MessageRouter {

    /**
     * Route the given message.
     * 
     * @param msg The fully formatted message.
     * @param logRecord The LogRecord associated with the message.
     * 
     * @return true if the message may be logged normally by the caller,
     *         (in addition to whatever logging was performed under this
     *         method), if desired.
     */
    public boolean route(String msg, LogRecord logRecord);
}