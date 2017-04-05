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
 * This exception is thrown when an attempt is made to install a bean
 * into the container with an illegal use of the TX_BEAN_MANAGED
 * transaction attribute. <p>
 * 
 * There are two cases where the TX_BEAN_MANAGED transaction attribute are
 * illegal. It cannot be used with entity beans and it cannot be mixed
 * with any other transaction attribute, i.e. if one method on the bean
 * is marked with it then all methods on the bean must be marked with
 * it. <p>
 */

public class IllegalBMTxAttrException
                extends ContainerException
{
    private static final long serialVersionUID = -4603137228938112818L;

    /**
     * Create a new <code>IllegalBMTxAttrException</code>
     * instance. <p>
     */

    public IllegalBMTxAttrException() {
        super();
    } // IllegalBMTxAttrException

} // IllegalBMTxAttrException
