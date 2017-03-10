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
package com.ibm.ws.logging;

import java.util.logging.LogRecord;

/**
 * Encapsulates a LogRecord and its various message formats.
 */
public interface RoutedMessage {

    /**
     * TODO
     */
    public String getFormattedMsg();

    /**
     * 
     */
    public String getFormattedVerboseMsg();

    /**
     * 
     */
    public String getMessageLogFormat();

    /**
     * 
     */
    public LogRecord getLogRecord();
}
