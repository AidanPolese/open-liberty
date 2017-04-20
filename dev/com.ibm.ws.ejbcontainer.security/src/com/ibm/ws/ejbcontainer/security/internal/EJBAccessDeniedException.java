/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.security.internal;

import javax.ejb.EJBAccessException;

/**
 *
 */
public class EJBAccessDeniedException extends EJBAccessException {

    /**
     * Create a new EJBAccessDeniedException with an empty description string. <p>
     */
    public EJBAccessDeniedException() {

    }

    /**
     * Create a new EJBAccessDeniedException with the associated string description. <p.
     * 
     * @param s the <code>String</code> describing the exception <p>
     */
    public EJBAccessDeniedException(String s) {

        super(s);

    }
}
