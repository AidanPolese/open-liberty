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
/*
 * Some of the code was derived from code supplied by the Apache Software Foundation licensed under the Apache License, Version 2.0.
 */
package com.ibm.ws.transport.iiop.security;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;

import com.ibm.ws.transport.iiop.security.config.css.CSSConfig;


/**
 * @version $Rev: 451417 $ $Date: 2006-09-29 13:13:22 -0700 (Fri, 29 Sep 2006) $
 */
public class ClientPolicy extends LocalObject implements Policy {

    private final CSSConfig config;

    public ClientPolicy(CSSConfig ORBConfig) {
        this.config = ORBConfig;
    }

    public CSSConfig getConfig() {
        return config;
    }

    public int policy_type() {
        return ClientPolicyFactory.POLICY_TYPE;
    }

    public void destroy() {
    }

    public Policy copy() {
        return new ClientPolicy(config);
    }
}
