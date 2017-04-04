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
 * getAuthenticatedUsername of TrustAssociationIntercepter.
 * 
 * @ibm-spi
 */
public class WebTrustAssociationUserException extends WebTrustAssociationException {

    private static final long serialVersionUID = -3409718364837759270L; //@vj1: Take versioning into account if incompatible changes are made to this class

    /**
     * Create a new WebTrustAssociationUserException with an empty description string.
     */
    public WebTrustAssociationUserException() {
        this("No message.");
    }

    /**
     * Create a new WebTrustAssociationUserException with the associated string description.
     * 
     * @param message the String describing the exception.
     */
    public WebTrustAssociationUserException(String msg) {
        super(msg);
    }

}
