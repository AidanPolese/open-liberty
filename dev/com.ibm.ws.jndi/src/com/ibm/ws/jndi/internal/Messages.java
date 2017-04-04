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
package com.ibm.ws.jndi.internal;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;

public class Messages {
    public static final String RESOURCE_BUNDLE_NAME = "com.ibm.ws.jndi.internal.resources.JNDIMessages";
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);

    @FFDCIgnore(Exception.class)
    public static String formatMessage(final String key, final String defaultMessage, Object... inserts) {
        String message = defaultMessage;
        try {
            try {
                message = BUNDLE.getString(key);
            } catch (MissingResourceException e) {
                message = defaultMessage;
            }
            if (inserts.length != 0)
                message = MessageFormat.format(message, inserts);
            return message;
        } catch (Exception ignored) {
            return message;
        }
    }

}
