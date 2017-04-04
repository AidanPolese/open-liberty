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

import java.util.concurrent.ScheduledExecutorService;

import org.jboss.weld.resources.spi.ScheduledExecutorServiceFactory;

public class ScheduledExecutorServiceFactoryImpl implements ScheduledExecutorServiceFactory
{
    private ScheduledExecutorService scheduledExecutorService;

    public ScheduledExecutorServiceFactoryImpl(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    /** {@inheritDoc} */
    @Override
    public void cleanup() {
        scheduledExecutorService = null;
    }

    /** {@inheritDoc} */
    @Override
    public ScheduledExecutorService get() {
        return scheduledExecutorService;
    }
}
