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
package com.ibm.websphere.config;

/**
 *
 */
public class ConfigUpdateException extends Exception {

    /**  */
    private static final long serialVersionUID = -4540509541009993L;

    /**
     * @param formatMessage
     */
    public ConfigUpdateException(String formatMessage) {
        super(formatMessage);
    }

    /**
     * 
     * @param ex
     */
    public ConfigUpdateException(Exception ex) {
        super(ex.getMessage(), ex);
    }

}
