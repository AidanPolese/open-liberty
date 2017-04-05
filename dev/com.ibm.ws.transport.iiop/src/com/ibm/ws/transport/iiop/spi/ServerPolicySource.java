/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.transport.iiop.spi;

import java.util.List;

import org.omg.CORBA.Policy;


/**
 *
 */
public interface ServerPolicySource {

    /**
     * @return
     * @throws Exception
     */
    public abstract void addConfiguredPolicies(List<Policy> policies, ORBRef server) throws Exception;

}
