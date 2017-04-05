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
 * This exception is thrown whenever an error internal to the EJS container
 * has occurred. <p>
 * 
 * In general, this exception must not be masked and must at some point
 * cause a fatal error to occur. <p>
 * 
 */

public class ContainerInternalError
                extends ContainerException
{
    private static final long serialVersionUID = -1736348221294252132L;

    public ContainerInternalError(Throwable ex) {
        super("", ex);
    }

    public ContainerInternalError() {
        super("");
    }
} // ContainerInternalError
