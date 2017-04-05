/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 2005
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

/**
 * This exception is thrown to indicate an error has occurred
 * while flushing the persistent state of an entity bean to the
 * persistent store. <p>
 */

public class CacheFlushFailure
                extends ContainerException
{
    private static final long serialVersionUID = -2647042063557834894L;

    /**
     * Create a new <code>CacheFlushFailure</code> instance. <p>
     */
    public CacheFlushFailure(Throwable ex) {
        super(ex);
    } // CacheFlushFailure

} // CacheFlushFailure
