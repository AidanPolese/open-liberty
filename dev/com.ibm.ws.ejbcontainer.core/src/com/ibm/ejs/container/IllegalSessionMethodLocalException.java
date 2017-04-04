/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2001, 2005
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

/**
 * This exception is thrown when an attempt is made to invoke
 * getPrimaryKey() or remove(Object primaryKey) on a session bean.
 **/
public class IllegalSessionMethodLocalException
                extends ContainerLocalException
{
    private static final long serialVersionUID = 2467827007726460881L;

    /**
     * Create a new <code>IllegalSessionMethodLocalException</code> instance.
     */
    public IllegalSessionMethodLocalException()
    {
        super();
    }

} // IllegalSessionMethodLocalException
