/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.threading;

import java.util.concurrent.ExecutorService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

import com.ibm.ws.threading.internal.PolicyExecutorImpl;

/**
 * <p>Provider class which can programmatically create policy executors.
 * The ability to create programmatically is provided for server components
 * which do not have any way of using a policyExecutor from server configuration.
 * Components with server configuration should instead rely on policyExecutor
 * instances from server config, which is the preferred approach, rather than
 * using PolicyExecutorProvider.</p>
 *
 * <p>Policy executors are backed by the Liberty global thread pool,
 * but allow concurrency constraints and various queue attributes
 * to be controlled independently of the global thread pool.</p>
 *
 * <p>For example, to create a policy executor that allows at most 3 tasks to
 * be active at any given point and can queue up to 20 tasks,</p>
 *
 * <code>
 * executor = PolicyExecutorProvider.create("AtMost3ConcurrentPolicy").maxConcurrency(3).maxQueueSize(20);
 * </code>
 */
@Component(configurationPolicy = ConfigurationPolicy.IGNORE, service = { PolicyExecutorProvider.class })
public class PolicyExecutorProvider {
    @Reference(target = "(component.name=com.ibm.ws.threading)")
    private ExecutorService globalExecutor;

    /**
     * Creates a new policy executor instance.
     *
     * @param identifier unique identifier for this instance, to be used for monitoring and problem determination.
     * @return a new policy executor instance.
     */
    public PolicyExecutor create(String identifier) {
        return new PolicyExecutorImpl(globalExecutor, identifier);
    }
}
