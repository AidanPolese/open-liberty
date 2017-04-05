/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi;

import com.ibm.ws.ejbcontainer.EJBReferenceFactory;

/**
 * A handle to a started system module.
 */
public interface EJBSystemModule {
    /**
     * This method must be called when the EJBs in the module should no longer
     * be accessible.
     */
    void stop();

    /**
     * Returns a factory for obtaining references to EJBs in the module
     *
     * @param ejbName the {@linkplain EJBSystemBeanConfig#getName EJB name}
     * @return the reference factory
     * @throws IllegalArgumentException if the EJB name is not valid
     */
    EJBReferenceFactory getReferenceFactory(String ejbName);
}
