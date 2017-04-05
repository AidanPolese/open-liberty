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
 * This exception is thrown if an attempt is made to install an EJB
 * into a container with the same JNDI name for its home as an
 * EJS that is already installed in the container. <p>
 * 
 * If this exception is thrown the EJB installation fails and the
 * already installed EJB remains usable. <p>
 */

public class DuplicateHomeNameException
                extends ContainerException
{
    private static final long serialVersionUID = 4878512837435795367L;

    public DuplicateHomeNameException(String s) {
        super(s);
    }
} // DuplicateHomeNameException
