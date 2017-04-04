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
package com.ibm.ws.transport.iiop.yoko;

import org.apache.yoko.orb.OB.DispatchStrategy;
import org.apache.yoko.orb.OBPortableServer.DISPATCH_STRATEGY_POLICY_ID;
import org.apache.yoko.orb.OBPortableServer.DispatchStrategyPolicy;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;

/**
 *
 */
public class ExecutorDispatchPolicy extends LocalObject implements DispatchStrategyPolicy {

    /**  */
    private static final long serialVersionUID = 1L;
    private final transient DispatchStrategy dispatchStrategy;

    /**
     * @param dispatchStrategy
     */
    public ExecutorDispatchPolicy(DispatchStrategy dispatchStrategy) {
        this.dispatchStrategy = dispatchStrategy;
    }

    /** {@inheritDoc} */
    @Override
    public int policy_type() {
        return DISPATCH_STRATEGY_POLICY_ID.value;
    }

    /** {@inheritDoc} */
    @Override
    public Policy copy() {
        return new ExecutorDispatchPolicy(dispatchStrategy);
    }

    /** {@inheritDoc} */
    @Override
    public void destroy() {}

    /** {@inheritDoc} */
    @Override
    public DispatchStrategy value() {
        return dispatchStrategy;
    }

}
