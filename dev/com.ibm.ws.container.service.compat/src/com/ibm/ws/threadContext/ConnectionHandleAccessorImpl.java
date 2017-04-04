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
package com.ibm.ws.threadContext;

import com.ibm.ejs.j2c.HandleListInterface;

/**
 * Stub until LazyAssociatableConnectionManager is designed with J2C.
 */
public class ConnectionHandleAccessorImpl {
    private static final ConnectionHandleAccessorImpl instance = new ConnectionHandleAccessorImpl();

    public static ConnectionHandleAccessorImpl getConnectionHandleAccessor() {
        return instance;
    }

    private final ThreadContext<HandleListInterface> threadContext = new ThreadContextImpl<HandleListInterface>();

    public ThreadContext<HandleListInterface> getThreadContext() {
        return threadContext;
    }
}
