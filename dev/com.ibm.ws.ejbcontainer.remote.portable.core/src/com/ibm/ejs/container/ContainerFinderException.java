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
 * Thrown upon find failures.
 */

public class ContainerFinderException extends ContainerException
{
    private static final long serialVersionUID = -1949465464357313116L;

    public ContainerFinderException(String s) {
        super(s);
    }

    public ContainerFinderException() {
        super();
    }
} // ContainerFinderException
