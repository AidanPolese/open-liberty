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
package com.ibm.ws.javaee.dd.ejb;

/**
 * Represents &lt;init-method>.
 */
public interface InitMethod
{
    /**
     * @return &lt;create-method>
     */
    NamedMethod getCreateMethod();

    /**
     * @return &lt;bean-method>
     */
    NamedMethod getBeanMethod();
}
