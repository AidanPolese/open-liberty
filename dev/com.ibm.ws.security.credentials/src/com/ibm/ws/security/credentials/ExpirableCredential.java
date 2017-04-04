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
package com.ibm.ws.security.credentials;

/**
 * Interface used by credentials that can have an expiration set.
 */
public interface ExpirableCredential {

    /**
     * Sets the expiration of the credential in milliseconds.
     */
    public void setExpiration(long expirationInMilliseconds);

}
