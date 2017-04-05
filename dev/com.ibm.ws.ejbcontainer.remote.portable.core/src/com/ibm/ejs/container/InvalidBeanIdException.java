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
 * This exception is thrown to indicate an attempt has been made to use
 * an invalid BeanId. <p>
 */

public class InvalidBeanIdException
                extends ContainerException
{
    private static final long serialVersionUID = -9118212346389086271L;

    /**
     * Create a new <code>InvalidBeanIdException</code> instance. <p>
     */
    public InvalidBeanIdException() {
        super();
    } // InvalidBeanIdException

    /**
     * Create a new <code>InvalidBeanIdException</code> instance
     * with the specified nested excpeption. <p>
     */
    // d356676.1
    public InvalidBeanIdException(Throwable exception)
    {
        super(exception);
    } // InvalidBeanIdException

} // InvalidBeanIdException
