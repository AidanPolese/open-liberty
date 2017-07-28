/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.microprofile.config.interfaces;

/**
 * The class for all your favourite Config related constants.
 */
public class ConfigConstants {

    public static final String META_INF = "META-INF/";
    public static final String CONFIG_PROPERTIES = META_INF + "microprofile-config.properties";

    public static final String ORDINAL_PROPERTY = "config_ordinal";

    public static final int ORDINAL_SYSTEM_PROPERTIES = 400;
    public static final int ORDINAL_ENVIRONMENT_VARIABLES = 300;
    public static final int ORDINAL_PROPERTIES_FILE = 100;

    public static final int DEFAULT_CONVERTER_PRIORITY = 100;

    public static final String DYNAMIC_REFRESH_INTERVAL_PROP_NAME = "microprofile.config.refresh.rate";
    public static final long DEFAULT_DYNAMIC_REFRESH_INTERVAL = 500;
    public static final long MINIMUM_DYNAMIC_REFRESH_INTERVAL = 500;

}
