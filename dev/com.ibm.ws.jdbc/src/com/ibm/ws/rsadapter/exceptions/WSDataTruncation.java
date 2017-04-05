/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2001,2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.rsadapter.exceptions;

import java.sql.DataTruncation;

/**
 * This class overrides java.sql.DataTruncation to allow a message to be specified.
 */
public class WSDataTruncation extends DataTruncation
{
    private static final long serialVersionUID = -2697519459662430640L;
    /** The exception message. */
    String message;

    /**
     * Construct a data truncation error.
     * 
     * @param message the exception message.
     * @param index the index of the parameter or column value.
     * @param isParameter indicates if the truncated value was a parameter (not a column).
     * @param isRead indicates if a read operation was truncated (vs a write operation)
     * @param dataSize the original size of the data.
     * @param transferSize the size after truncation.
     */
    public WSDataTruncation(String message, int index, boolean isParameter, boolean isRead, int dataSize, int transferSize) {
        super(index, isParameter, isRead, dataSize, transferSize);
        this.message = message;
    }

    /**
     * @return the exception message.
     */
    @Override
    public String getMessage() {
        return message;
    }
}
