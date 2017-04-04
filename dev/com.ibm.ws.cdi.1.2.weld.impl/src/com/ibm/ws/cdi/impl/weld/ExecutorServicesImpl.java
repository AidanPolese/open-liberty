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
package com.ibm.ws.cdi.impl.weld;

import java.util.concurrent.ExecutorService;

import org.jboss.weld.executor.AbstractExecutorServices;
import org.jboss.weld.manager.api.ExecutorServices;

public class ExecutorServicesImpl extends AbstractExecutorServices implements ExecutorServices
{

    private final ExecutorService executorService;

    public ExecutorServicesImpl(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /** {@inheritDoc} */
    @Override
    public ExecutorService getTaskExecutor() {
        return executorService;
    }

    /** {@inheritDoc} */
    @Override
    protected int getThreadPoolSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void cleanup() {
        //no-op
    }
}
