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

/**
 * <p>Provider class which can programmatically build policy executors.
 * Policy executors are backed by the Liberty global thread pool,
 * but allow concurrency constraints and various queue attributes
 * to be controlled independently of the global thread pool.
 * For example, to build a policy executor that allows at most 3 tasks to
 * be active at any given point and can queue up to 20 tasks,</p>
 * <code>
 * executor = policyExecutorProvider.builder().maxConcurrency(3).maxQueueSize(20).build();
 * </code>
 */
@Component(configurationPolicy = ConfigurationPolicy.IGNORE)
public class PolicyExecutorProvider {
    @Reference(target = "(component.name=com.ibm.ws.threading)")
    private ExecutorService globalExecutor;

    /**
     * Creates a new builder instance.
     *
     * @return a new builder instance.
     */
    public PolicyExecutorBuilder builder() {
        return new PolicyExecutorBuilder(globalExecutor);
    }
}
