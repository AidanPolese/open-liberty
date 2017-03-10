/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.feature.internal.cmdline;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 *
 */
public class NLS {
    public static final ResourceBundle messages = ResourceBundle.getBundle("com.ibm.ws.kernel.feature.internal.resources.ProvisionerMessages");

    /**
     * Appends "tool." onto the front of the key and loads the message from the "com.ibm.ws.kernel.feature.internal.resources.ProvisionerMessages" bundle.
     * 
     * @param key
     * @param args
     * @return
     */
    public static String getMessage(String key, Object... args) {
        return getNonToolMessage("tool." + key, args);
    }

    /**
     * Loads the message from the "com.ibm.ws.kernel.feature.internal.resources.ProvisionerMessages" bundle.
     * 
     * @param key
     * @param args
     * @return
     */
    public static String getNonToolMessage(String key, Object... args) {
        String message = messages.getString(key);
        return args.length == 0 ? message : MessageFormat.format(message, args);
    }
}