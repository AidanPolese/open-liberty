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
package com.ibm.ws.jca.adapter;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionRequestInfo;
import javax.security.auth.Subject;

/**
 * WebSphere Application Server extensions to the ConnectionRequestInfo interface.
 */
public abstract class WSConnectionRequestInfo implements ConnectionRequestInfo {
    /**
     * Populates the connection request with information from the Subject (for example, for trusted context).
     * 
     * @param sub the subject
     * @throws ResourceException if an error occurs
     */
    public void populateWithIdentity(Subject sub) throws ResourceException {}
}