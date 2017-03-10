/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.config.ext;

/**
 *
 */
public class ConfigExtension {

    public static final boolean useDefaultConfigValidation = true;
    public static boolean forceEmbeddedConfigValidation = false;

    public static void setUseEmbeddedValidation(boolean b) {
        forceEmbeddedConfigValidation = b;
    }

}