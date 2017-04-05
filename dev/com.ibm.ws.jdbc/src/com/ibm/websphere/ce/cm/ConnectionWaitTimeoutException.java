/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.ce.cm;

import java.sql.SQLTransientConnectionException;

/**
 * Used as a chained exception when unable to allocate a connection before the connection timeout is reached.
 * Would like to get rid of this and combine with top level exception, but could any application code be relying
 * on the chained exception?
 */
public class ConnectionWaitTimeoutException extends SQLTransientConnectionException {
    private static final long serialVersionUID = 5958695928250441720L;

    /**
     * Make a new ConnectionWaitTimeoutException.
     * 
     * @param message the exception message.
     */
    public ConnectionWaitTimeoutException(String message) {
        super(message);
    }
}