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

import java.util.Locale;

/**
 * An extension to java.util.logging.LogRecord.
 * 
 * This interface is implemented by LogRecords cut from the WLP logging code.
 */
public interface LogRecordExt {

    /**
     * Retrieve the formatted message for this LogRecord from the given Locale,
     * using the resource bundle, message, and parameters associated with this
     * LogRecord.
     * 
     * Note: this method assumes the LogRecord message (as returned by getMessage())
     * is a key that references a message in the resource bundle.
     * 
     * @param locale The desired Locale.
     * 
     * @return The formatted message for this LogRecord in the given Locale.
     */
    public String getFormattedMessage(Locale locale);
}
