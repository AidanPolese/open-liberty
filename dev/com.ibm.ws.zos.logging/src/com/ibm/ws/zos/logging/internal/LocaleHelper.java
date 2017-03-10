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

import java.util.Locale;
import java.util.logging.LogRecord;

import com.ibm.wsspi.logging.LogRecordExt;

/**
 * Simple helper class for translating messages to english
 * before writing them WTO or HARDCOPY.
 */
public class LocaleHelper {

    /**
     * Indicates whether or not we're in an ENGLISH locale
     */
    private final boolean isEnglishLocale = Locale.getDefault().getLanguage().equals(Locale.ENGLISH.getLanguage());

    /**
     * @return the msg in english. null if msg could not be translated.
     */
    public String translateToEnglish(String msg, LogRecord logRecord) {
        if (isEnglishLocale) {
            return msg;
        } else if (logRecord != null && logRecord instanceof LogRecordExt) {
            return ((LogRecordExt) logRecord).getFormattedMessage(Locale.ENGLISH);
        } else {
            return null;
        }
    }

}
