/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi;

import com.ibm.ws.ejbcontainer.JCDIHelper;
import com.ibm.wsspi.adaptable.module.Container;

/**
 * This is an abstraction for EJB container to be able to optionally depend on CDI.
 */
public interface JCDIHelperFactory {

    /**
     * Returns a JCDIHelper for the specified container representing an EJB module, or null if the module is not CDI-enabled or CDI is not enabled in the server
     * 
     * @param container The container that will be tested to see if it is CDI enabled
     * @return a JCDIHelper object if both the container and server are CDI enabled, null otherwise
     */
    public JCDIHelper getJCDIHelper(Container container);
}
