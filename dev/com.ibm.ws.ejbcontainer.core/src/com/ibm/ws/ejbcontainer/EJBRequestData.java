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
package com.ibm.ws.ejbcontainer;

/**
 * Data specific to an EJB request.
 */
public interface EJBRequestData
{
    /**
     * The EJB method being called.
     */
    EJBMethodMetaData getEJBMethodMetaData();

    /**
     * The arguments passed to the EJB method. The returned array should not be
     * modified.
     */
    Object[] getMethodArguments();

    /**
     * The unique identifier of the bean instance being invoked. The object
     * implements {@link Object#hashCode} and {@link Object#equals}.
     */
    Object getBeanId();

    /**
     * The bean instance being invoked. This method may only be called by after
     * activation collaborators.
     */
    Object getBeanInstance();
}
