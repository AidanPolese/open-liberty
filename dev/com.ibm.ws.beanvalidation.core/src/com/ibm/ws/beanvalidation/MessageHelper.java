/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.beanvalidation;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Utility class to help with generation of messages
 */

public class MessageHelper {

    private static ResourceBundle resourceBundle;

    public static String getMessage(String key) {
        String message = " ";

        if (resourceBundle == null) {
            getResourceBundle();
        }

        try {
            message = resourceBundle.getString(key);
        } catch (Exception e) {
            message = " ";
        }

        return message;
    }

    public static String getMessage(String key, Object[] args) {
        if (resourceBundle == null) {
            getResourceBundle();
        }
        return MessageFormat.format(resourceBundle.getString(key), args);
    }

    private static ResourceBundle getResourceBundle() {
        resourceBundle = ResourceBundle.getBundle(BVNLSConstants.BV_RESOURCE_BUNDLE, Locale.getDefault());
        return resourceBundle;
    }
}
