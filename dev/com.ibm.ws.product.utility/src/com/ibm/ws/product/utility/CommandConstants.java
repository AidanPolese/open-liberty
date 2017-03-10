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

import java.util.ResourceBundle;

public class CommandConstants {

    public static final String COMMAND_OPTION_PREFIX = "--";

    public static final ResourceBundle PRODUCT_MESSAGES = ResourceBundle.getBundle("com.ibm.ws.product.utility.resources.UtilityMessages");

    public static final ResourceBundle PRODUCT_OPTIONS = ResourceBundle.getBundle("com.ibm.ws.product.utility.resources.UtilityOptions");

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static final String WLP_INSTALLATION_LOCATION = "WLP_INSTALLATION_LOCATION";

    public static final String SCRIPT_NAME = "SCRIPT_NAME";

    public static final String OUTPUT_FILE_OPTION = COMMAND_OPTION_PREFIX + "output";
}
