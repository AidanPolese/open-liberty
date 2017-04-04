/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011,2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jca.adapter;

import javax.resource.spi.ManagedConnection;
import javax.transaction.xa.XAResource;

/**
 * XA resource that is associated with a ManagedConnection.
 */
public interface WSXAResource extends XAResource
{
    /**
     * Returns the managed connection that created this XA resource.
     * 
     * @return the managed connection that created this XA resource.
     */
    ManagedConnection getManagedConnection();
}