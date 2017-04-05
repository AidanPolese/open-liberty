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
 * This exception is thrown whenever an attempt to passivate a
 * <code>BeanO</code> instance fails. <p>
 * 
 */

public class BeanOPassivationFailureException
                extends ContainerException
{
    private static final long serialVersionUID = 8894821895189549685L;

    public BeanOPassivationFailureException() {
        super();
    }
}
