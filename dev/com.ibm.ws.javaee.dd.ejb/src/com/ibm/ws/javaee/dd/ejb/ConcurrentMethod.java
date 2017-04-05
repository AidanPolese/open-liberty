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
package com.ibm.ws.javaee.dd.ejb;

/**
 * Represents &lt;concurrent-method>.
 */
public interface ConcurrentMethod
{
    /**
     * Represents an unspecified value for {@link #getConcurrentLockTypeValue}.
     */
    int LOCK_TYPE_UNSPECIFIED = -1;

    /**
     * Represents "Read" for {@link #getConcurrentLockTypeValue}.
     * 
     * @see org.eclipse.jst.j2ee.ejb.ConcurrentLockType#READ
     */
    int LOCK_TYPE_READ = 0;

    /**
     * Represents "Write" for {@link #getConcurrentLockTypeValue}.
     * 
     * @see org.eclipse.jst.j2ee.ejb.ConcurrentLockType#WRITE
     */
    int LOCK_TYPE_WRITE = 1;

    /**
     * @return &lt;method>
     */
    NamedMethod getMethod();

    /**
     * @return &lt;lock>
     *         <ul>
     *         <li>{@link #LOCK_TYPE_UNSPECIFIED} if unspecified
     *         <li>{@link #LOCK_TYPE_READ} - Read
     *         <li>{@link #LOCK_TYPE_WRITE} - Write
     *         </ul>
     */
    int getLockTypeValue();

    /**
     * @return &lt;access-timeout>, or null if unspecified
     */
    AccessTimeout getAccessTimeout();
}
