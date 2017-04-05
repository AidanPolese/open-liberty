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
 * Callback interface as EJB methods are being called.
 */
public interface EJBRequestCollaborator<T>
{
    /**
     * Called by container prior to executing an EJB method (see {@link EJBMethodInterface} for the possible method types). The timing of
     * the method call depends on how the collaborator is registered with the
     * container. If this method returns successfully, then {@link #postInvoke} will be called.
     * 
     * @param request the EJB request
     * @return a value that should be passed to {@link #postInvoke}
     */
    T preInvoke(EJBRequestData request)
                    throws Exception;

    /**
     * Called by the container after executing an EJB method. This method will
     * only be called if {@link #preInvoke} returns successfully.
     * 
     * @param request the EJB request
     * @param preInvokeResult the value returned by {@link #preInvoke}
     */
    void postInvoke(EJBRequestData request, T preInvokeResult)
                    throws Exception;
}
