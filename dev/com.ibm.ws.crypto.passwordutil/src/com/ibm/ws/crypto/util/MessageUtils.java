/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.crypto.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Message handling helper class.
 */
public class MessageUtils {
    public static final String RB = "com.ibm.ws.crypto.util.internal.resources.Messages";
    public static final ResourceBundle messages = ResourceBundle.getBundle(RB);

    public static String getMessage(String key, Object... args) {
        String message = messages.getString(key);
        return args.length == 0 ? message : MessageFormat.format(message, args);
    }
}
