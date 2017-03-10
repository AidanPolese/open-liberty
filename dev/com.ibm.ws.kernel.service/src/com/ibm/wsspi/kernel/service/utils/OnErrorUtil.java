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
package com.ibm.wsspi.kernel.service.utils;

/**
 *
 */
public class OnErrorUtil {

    public final static String CFG_KEY_ON_ERROR = "onError";
    public final static String CFG_VALID_OPTIONS = "[IGNORE][WARN][FAIL]";

    public enum OnError {
        IGNORE, WARN, FAIL
    };

    public static OnError getDefaultOnError() {
        // Note: Metatype definitions should/must match this value, default="WARN"
        return OnError.WARN;
    }

    public static String getAttributeName() {
        return CFG_KEY_ON_ERROR;
    }
}
