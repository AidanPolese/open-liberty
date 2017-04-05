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
 * This exception is thrown when an attempt is made to invoke
 * getPrimaryKey() or remove(Object primaryKey) on a session bean. <p>
 */

public class IllegalSessionMethodException
                extends ContainerException
{
    private static final long serialVersionUID = 3020565125536306536L;

    /**
     * Create a new <code>IllegalSessionMethodException</code> instance. <p>
     */

    public IllegalSessionMethodException() {
        super();
    } // IllegalSessionMethodException

} // IllegalSessionMethodException
