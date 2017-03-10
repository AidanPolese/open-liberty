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

package com.ibm.websphere.config;

/**
 * An exception representing an error occurred while parsing configuration
 * documents.
 */
public class ConfigValidationException extends Exception {
    private static final long serialVersionUID = -8341749732382155484L;
    public String docLocation = "";

    public ConfigValidationException() {
        super();
    }

    public ConfigValidationException(String message) {
        super(message);
    }

    public ConfigValidationException(String message, String doc) {
        super(message);
        this.docLocation = doc;
    }

}
