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
 * validateEstablishedTrust of TrustAssociationIntercepter.
 * 
 * @ibm-spi
 */
public class WebTrustAssociationFailedException extends WebTrustAssociationException {

    private static final long serialVersionUID = -2991475097046505440L; //@vj1: Take versioning into account if incompatible changes are made to this class

    /**
     * Create a new WebTrustAssociationFailedException with an empty description string.
     */
    public WebTrustAssociationFailedException() {
        this("No message");
    }

    /**
     * Create a new WebTrustAssociationFailedException with the associated string description.
     * 
     * @param message the String describing the exception.
     */
    public WebTrustAssociationFailedException(String msg) {
        super(msg);
    }
}
