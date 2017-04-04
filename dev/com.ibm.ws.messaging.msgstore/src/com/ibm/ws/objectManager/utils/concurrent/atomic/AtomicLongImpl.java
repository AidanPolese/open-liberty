package com.ibm.ws.objectManager.utils.concurrent.atomic;

/*
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2013
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 */

/**
 * Native JVM implementation.
 */
public class AtomicLongImpl
                extends java.util.concurrent.atomic.AtomicLong
                implements AtomicLong {

    private static final long serialVersionUID = 1L;

    public AtomicLongImpl(long longValue) {
        super(longValue);
    }
} // class AtomicLong.