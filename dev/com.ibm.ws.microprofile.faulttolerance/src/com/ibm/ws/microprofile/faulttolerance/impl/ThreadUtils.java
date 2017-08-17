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
package com.ibm.ws.microprofile.faulttolerance.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 *
 */
public class ThreadUtils {

    private static final TraceComponent tc = Tr.register(ThreadUtils.class);

    public static ExecutorService getDefaultExecutorService() {
        ExecutorService defaultExecutorService;
        try {
            defaultExecutorService = (ExecutorService) new InitialContext().lookup("java:comp/DefaultManagedScheduledExecutorService");
        } catch (NamingException e) {
            if ("true".equalsIgnoreCase(System.getProperty("com.ibm.ws.microprofile.faulttolerance.jse"))) {
                //this is really intended for unittest
                defaultExecutorService = Executors.newScheduledThreadPool(5);
            } else {
                throw new FaultToleranceException(Tr.formatMessage(tc, "internal.error.CWMFT4998E", e), e);
            }
        }
        return defaultExecutorService;
    }

    public static ThreadFactory getThreadFactory() {
        //should use the liberty managed thread factory!
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        return threadFactory;
    }
}
