/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.ws.event.internal;

import java.util.concurrent.ExecutorService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

import com.ibm.websphere.event.ExecutorServiceFactory;

@Component(configurationPolicy = ConfigurationPolicy.IGNORE,
           property = { "service.vendor=IBM" })
public class WorkStageExecutorServiceFactory implements ExecutorServiceFactory {

    ExecutorService executorService;

    @Reference
    protected void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    protected void unsetExecutorService(ExecutorService executorService) {
        if (executorService == this.executorService) {
            this.executorService = null;
        }
    }

    @Override
    public ExecutorService getExecutorService(String name) {
        return executorService;
    }
}
