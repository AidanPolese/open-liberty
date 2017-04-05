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
 * Thrown when a duplicate primary key is found.
 */

public class ContainerDuplicateKeyException extends ContainerException
{
    private static final long serialVersionUID = 2461664244955831159L;

    public ContainerDuplicateKeyException(String s) {
        super(s);
    }

    public ContainerDuplicateKeyException() {
        super();
    }
} // ContainerDuplicateKeyException
