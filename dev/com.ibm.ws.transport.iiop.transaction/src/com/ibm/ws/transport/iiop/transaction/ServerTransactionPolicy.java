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
package com.ibm.ws.transport.iiop.transaction;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;

/**
 * @version $Rev: 451417 $ $Date: 2006-09-29 13:13:22 -0700 (Fri, 29 Sep 2006) $
 */
public class ServerTransactionPolicy extends LocalObject implements Policy {

    private final ServerTransactionPolicyConfig serverTransactionPolicyConfig;

    public ServerTransactionPolicy(ServerTransactionPolicyConfig serverTransactionPolicyConfig) {
        this.serverTransactionPolicyConfig = serverTransactionPolicyConfig;
    }


    public int policy_type() {
        return ServerTransactionPolicyFactory.POLICY_TYPE;
    }

    public Policy copy() {
        return new ServerTransactionPolicy(serverTransactionPolicyConfig);
    }

    public void destroy() {

    }

    ServerTransactionPolicyConfig getServerTransactionPolicyConfig() {
        return serverTransactionPolicyConfig;
    }
}
