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
 * This exception is thrown whenever an attempt is made to get a bean
 * via an unknown bean association. <p>
 * 
 */

public class UnknownBeanAssociationException
                extends ContainerException
{
    private static final long serialVersionUID = 7877119018214074490L;

    /**
     * Create a new <code>UnknownBeanAssociationException</code>
     * instance. <p>
     */

    public UnknownBeanAssociationException(String s) {
        super(s);
    } // UnknownBeanAssociationException

} // UnknownBeanAssociationException
