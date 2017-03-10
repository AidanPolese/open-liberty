/*
 * IBM Confidential
 * 
 * OCO Source Materials
 * 
 * Copyright IBM Corp. 2010
 * 
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 * 
 * Change activity:
 * 
 * Issue Date Name Description
 * ----------- ----------- -------- ------------------------------------
 * 
 */

package com.ibm.websphere.config;

/**
 * An exception representing an error occurred while parsing configuration
 * documents.
 */
public class ConfigParserException extends Exception {
    private static final long serialVersionUID = -8341749732382155484L;

    public ConfigParserException() {
        super();
    }

    public ConfigParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigParserException(String message) {
        super(message);
    }

    public ConfigParserException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
