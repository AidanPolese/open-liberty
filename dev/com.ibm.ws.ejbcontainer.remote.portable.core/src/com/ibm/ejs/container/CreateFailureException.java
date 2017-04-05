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
 * This exception is thrown to indicate a container error has occurred
 * while trying to creat a new EJB instance. <p>
 */

public class CreateFailureException
                extends ContainerException
{
    private static final long serialVersionUID = 8258962263818787828L;

    /**
     * Create a new <code>CreateFailureException</code> instance. <p>
     */

    public CreateFailureException(Throwable ex) {
        super(ex);
    } // CreateFailureException

} // CreateFailureException
