/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2009
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.wsspi.http;

import java.util.List;
import java.util.Locale;

/**
 * EncodingUtils provides various methods for manipulating and retrieving
 * information related to charsets, locales, and other encoding data.
 */
public interface EncodingUtils {

    /**
     * Query the default encoding.
     * 
     * @return String
     */
    String getDefaultEncoding();

    /**
     * Basically returns everything after ";charset=". If no charset specified,
     * return null.
     * 
     * @param type to extract the charset from.
     * 
     * @return The charset encoding.
     */
    String getCharsetFromContentType(String type);

    /**
     * Returns a list of locales from the passed in Accept-Language header.
     * 
     * @param acceptLangHdr
     * @return List<Locale>
     */
    List<Locale> getLocales(String acceptLangHdr);

    /**
     * Get the encoding for a passed in locale.
     * 
     * @param locale
     * @return The encoding.
     */
    String getEncodingFromLocale(Locale locale);

    /**
     * Get the JVM Converter for the specified encoding.
     * 
     * @param encoding
     * @return The converter if it exists, otherwise return the encoding.
     */
    String getJvmConverter(String encoding);

    /**
     * Tests whether the specified charset is supported on the server.
     * 
     * @param charset we want to test
     * @return boolean indicating if supported
     */
    boolean isCharsetSupported(String charset);

    /**
     * Utility method to trim off any leading or trailing quotes (single or
     * double).
     * 
     * @param value
     * @return String (null if null input provided)
     */
    String stripQuotes(String value);
}
