/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.utility.utils;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 *
 */
public class CommandUtils {

    public static final ResourceBundle messages = ResourceBundle.getBundle("com.ibm.ws.config.utility.resources.UtilityMessages");
    public static final ResourceBundle options = ResourceBundle.getBundle("com.ibm.ws.config.utility.resources.UtilityOptions");

    public static String getMessage(String key, Object... args) {
        String message = messages.getString(key);
        return args.length == 0 ? message : MessageFormat.format(message, args);
    }

    public static String getOption(String key, Object... args) {
        String option = options.getString(key);
        return args.length == 0 ? option : MessageFormat.format(option, args);
    }

    public static ResourceBundle getOptions() {
        return options;
    }
}
