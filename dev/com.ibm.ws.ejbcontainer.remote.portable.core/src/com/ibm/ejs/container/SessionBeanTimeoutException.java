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

import java.rmi.NoSuchObjectException;

/**
 * This exception is thrown whenever session bean timesout
 * 
 */

public class SessionBeanTimeoutException
                extends NoSuchObjectException
{
    private static final long serialVersionUID = 2591380441572291126L;

    /**
     * Create a new <code>SessionBeanTimeoutException</code>
     * instance. <p>
     */

    public SessionBeanTimeoutException(String s) {
        super(s);
    } // SessionBeanTimeoutException

} // SessionBeanTimeoutException
