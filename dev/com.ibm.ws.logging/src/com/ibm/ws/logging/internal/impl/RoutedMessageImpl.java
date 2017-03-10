/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015, 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging.internal.impl;

import java.util.logging.LogRecord;

import com.ibm.ws.logging.RoutedMessage;

/**
 * Wrapper around a LogRecord plus its various message formats.
 */
public class RoutedMessageImpl implements RoutedMessage {

    private final String formattedMsg;
    private final String formattedVerboseMsg;
    private final String messageLogFormat;

    private final LogRecord logRecord;

    public RoutedMessageImpl(String formattedMsg,
                             String formattedVerboseMsg,
                             String messageLogFormat,
                             LogRecord logRecord) {
        this.formattedMsg = formattedMsg;
        this.formattedVerboseMsg = formattedVerboseMsg;
        this.messageLogFormat = messageLogFormat;
        this.logRecord = logRecord;
    }

    @Override
    public String getFormattedMsg() {
        return formattedMsg;
    }

    @Override
    public String getFormattedVerboseMsg() {
        return formattedVerboseMsg;
    }

    @Override
    public String getMessageLogFormat() {
        return messageLogFormat;
    }

    @Override
    public LogRecord getLogRecord() {
        return logRecord;
    }
}