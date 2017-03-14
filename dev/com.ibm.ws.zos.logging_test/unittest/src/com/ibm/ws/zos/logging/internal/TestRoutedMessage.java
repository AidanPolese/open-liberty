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
package com.ibm.ws.zos.logging.internal;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.ibm.ws.logging.RoutedMessage;

/**
 * Helper class, for test purposes only.
 */
public class TestRoutedMessage implements RoutedMessage {
    private final String formattedMsg;
    private final LogRecord logRecord;

    public TestRoutedMessage(String formattedMsg) {
        this(formattedMsg, new LogRecord(Level.INFO, formattedMsg));
    }

    public TestRoutedMessage(String formattedMsg, LogRecord logRecord) {
        this.formattedMsg = formattedMsg;
        this.logRecord = logRecord;
    }

    @Override
    public String getFormattedMsg() {
        return formattedMsg;
    }

    @Override
    public String getFormattedVerboseMsg() {
        return getFormattedMsg();
    }

    @Override
    public String getMessageLogFormat() {
        return getFormattedMsg();
    }

    @Override
    public LogRecord getLogRecord() {
        return logRecord;
    }
}