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
 * Thrown when a malformed descriptor is detected by the container.<p>
 * 
 */

public class InvalidDescriptorException extends ContainerException
{
    private static final long serialVersionUID = 7008930165729679009L;

    public InvalidDescriptorException(String s) {
        super(s);
    }

    public InvalidDescriptorException() {
        super();
    }
} // InvalidDescriptorException
