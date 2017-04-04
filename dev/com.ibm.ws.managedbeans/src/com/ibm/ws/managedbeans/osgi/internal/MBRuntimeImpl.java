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
package com.ibm.ws.managedbeans.osgi.internal;

import org.osgi.service.component.ComponentContext;

import com.ibm.ws.ejbcontainer.osgi.ManagedBeanRuntime;

/**
 * Provides the managed bean runtime environment which enables managed beans
 * in the core container.
 */
public class MBRuntimeImpl implements ManagedBeanRuntime {

    public void activate(ComponentContext cc) {
        // Nothing currently needs to be done during service activation,
        // just the presence of this service enables managed beans.
    }

    public void deactivate(ComponentContext cc) {}
}
