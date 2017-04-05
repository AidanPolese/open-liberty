/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2008
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.ejbcontainer;

import java.util.List;

import com.ibm.websphere.csi.J2EEName;

/**
 * Provides a mechanism for associating Web Service application handlers
 * with particular EJB Components. <p>
 * 
 * This will allow any resources requested by the handler classes
 * (though annotations) to be bound into the component namespace
 * (java:comp/env), insuring the correct scoping. <p>
 * 
 * It is expected that a Web Services component that processes
 * Web Services metadata will provide an implementation of this
 * interface, and register an instance of it with the EJBContainer
 * service, with the method setWebServiceHandlerResolver. <p>
 */
public interface WSEJBHandlerResolver
{
    /**
     * Returns a list of all JAX-WS application handler classes that are
     * associated with the particular EJB represented by the specified
     * J2EEName. <p>
     * 
     * Note: JAX-RPC handlers do not support injection, so are not
     * included in the result of this method. <p>
     * 
     * @param j2eeName Java EE name that uniquely identifies an EJB
     *            within the server process.
     * 
     * @return a list of JAX-WSapplication handler classes associated with
     *         the specified EJB; null if no handler classes are
     *         associated with the EJB.
     */
    public List<Class<?>> retrieveJAXWSHandlers(J2EEName j2eeName);

}
