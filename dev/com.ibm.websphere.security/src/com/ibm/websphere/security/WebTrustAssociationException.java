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
package com.ibm.websphere.security;

/**
 * Thrown to indicate that a error occurred during the processing of
 * isTargetInterceptor of TrustAssociationIntercepter.
 * 
 * @ibm-spi
 */
public class WebTrustAssociationException extends Exception {

    private static final long serialVersionUID = -4068474794305197973L; //@vj1: Take versioning into account if incompatible changes are made to this class

    /**
     * Create a new WebTrustAssociationException with an empty description string.
     */
    public WebTrustAssociationException() {
        this("No Error Message");
    }

    /**
     * Create a new WebTrustAssociationException with the associated string description.
     * 
     * @param message the String describing the exception.
     */
    public WebTrustAssociationException(String err) {
        super(err);
    }

}
