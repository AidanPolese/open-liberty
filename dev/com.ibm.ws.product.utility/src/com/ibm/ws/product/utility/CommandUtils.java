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
package com.ibm.ws.product.utility;

import java.text.MessageFormat;

public class CommandUtils {

    public static String getMessage(String key, Object... args) {
        String message = CommandConstants.PRODUCT_MESSAGES.getString(key);
        return args.length == 0 ? message : MessageFormat.format(message, args);
    }

    public static String getOption(String key, Object... args) {
        String option = CommandConstants.PRODUCT_OPTIONS.getString(key);
        return args.length == 0 ? option : MessageFormat.format(option, args);
    }
}
