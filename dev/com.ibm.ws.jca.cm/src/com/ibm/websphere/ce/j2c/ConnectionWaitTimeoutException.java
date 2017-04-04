/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1997, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.ce.j2c;

import javax.resource.spi.ResourceAllocationException;

/**
 * Indicates that a connection request has waited for ConnectionWaitTimeout but a
 * connection did become free, and MaxConnections has been reached.
 * 
 * @ibm-api
 */
public class ConnectionWaitTimeoutException extends ResourceAllocationException {
    private static final long serialVersionUID = 7973811692690774902L;

    /**
     * Constructs a ResourceAllocationException with the specified reason.
     * 
     * @param reason The reason for the timeout exception.
     */
    public ConnectionWaitTimeoutException(String reason) {
        super(reason);
    }

    /**
     * Constructs a ResourceAllocationException with the specified reason and error code
     * 
     * @param reason The reason for the timeout exception.
     * @param errorCode
     */
    public ConnectionWaitTimeoutException(String reason, String errorCode) {
        super(reason, errorCode);
    }
}
