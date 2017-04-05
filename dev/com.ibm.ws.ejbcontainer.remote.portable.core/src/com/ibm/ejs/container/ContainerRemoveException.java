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

public class ContainerRemoveException extends ContainerException
{
    private static final long serialVersionUID = 5955342514152542284L;

    public ContainerRemoveException(String s) {
        super(s);
    }

    public ContainerRemoveException(String s, java.lang.Throwable ex) {
        super(s, ex);
    }

    public ContainerRemoveException(java.lang.Throwable ex) {
        super(ex); //150727
    }
} // ContainerRemoveException
