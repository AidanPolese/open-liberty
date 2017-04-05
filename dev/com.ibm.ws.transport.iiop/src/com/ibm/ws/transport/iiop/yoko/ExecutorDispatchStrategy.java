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

import java.util.concurrent.Executor;

import org.apache.yoko.orb.OB.DispatchRequest;
import org.apache.yoko.orb.OB.DispatchStrategy;
import org.omg.CORBA.Any;
import org.omg.CORBA.LocalObject;

/**
 *
 */
public class ExecutorDispatchStrategy extends LocalObject implements DispatchStrategy {

    private final Executor executor;

    /**
     * @param executor
     */
    public ExecutorDispatchStrategy(Executor executor) {
        this.executor = executor;
    }

    /** {@inheritDoc} */
    @Override
    public void dispatch(final DispatchRequest req) {

        executor.execute(new Runnable() {

            @Override
            public void run() {
                req.invoke();
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public int id() {
        return 4;
    }

    /** {@inheritDoc} */
    @Override
    public Any info() {
        return new org.apache.yoko.orb.CORBA.Any();
    }

}
