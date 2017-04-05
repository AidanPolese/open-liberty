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
 * Thrown when an object is not found. <p>
 */

public class ContainerObjectNotFoundException extends ContainerException
{
    private static final long serialVersionUID = 4916404802059115748L;

    public ContainerObjectNotFoundException(String s) {
        super(s);
    }

    public ContainerObjectNotFoundException() {
        super();
    }
} // ContainerObjectNotFoundException
