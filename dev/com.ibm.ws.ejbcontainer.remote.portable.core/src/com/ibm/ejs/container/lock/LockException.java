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
package com.ibm.ejs.container.lock;

import com.ibm.ejs.container.ContainerException;

/**
 * A <code>LockException</code> represents the root of all exceptions
 * raised by the <code>LockManager</code>. <p>
 */

public class LockException
                extends ContainerException
{
    private static final long serialVersionUID = 932849371367133006L;

    /**
     * Create a new <code>LockException</code> instance. <p>
     */

    public LockException() {
        super();
    } // LockException

    public LockException(String s, Throwable ex) {
        super(s, ex);
    } // LockException

} // LockException
