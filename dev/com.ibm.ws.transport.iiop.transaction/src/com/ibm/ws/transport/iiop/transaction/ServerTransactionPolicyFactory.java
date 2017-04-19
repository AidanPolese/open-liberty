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

import org.omg.CORBA.Any;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;
import org.omg.PortableInterceptor.PolicyFactory;

/**
 * @version $Rev: 451417 $ $Date: 2006-09-29 13:13:22 -0700 (Fri, 29 Sep 2006) $
 */
public class ServerTransactionPolicyFactory extends LocalObject implements PolicyFactory {
    public final static int POLICY_TYPE = 0x41534602;

    public Policy create_policy(int type, Any value) throws PolicyError {
        if (type != POLICY_TYPE) {
            throw new PolicyError(org.omg.CORBA.BAD_POLICY.value);
        }
        return new ServerTransactionPolicy((ServerTransactionPolicyConfig) value.extract_Value());
    }
}
