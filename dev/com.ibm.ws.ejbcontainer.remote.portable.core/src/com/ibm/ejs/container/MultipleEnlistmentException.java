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
 * This exception is thrown whenever an attempt is made to
 * enlist a <code>BeanO</code> in a transaction it is already
 * enlisted with. <p>
 * 
 * During normal operation, this exception will not be thrown. It
 * indicates that an internal error occurred. <p>
 */

public class MultipleEnlistmentException
                extends ContainerInternalError
{
    private static final long serialVersionUID = 7592848777683128945L;

    /**
     * Create a new <code>MultipleEnlistmentException</code>
     * instance. <p>
     */

    public MultipleEnlistmentException() {
        super();
    } // MultipleEnlistmentException

} // MultipleEnlistmentException
