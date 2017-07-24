/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.microprofile.faulttolerance.spi.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceException;

/**
 *
 */
public class ThreadUtils {

    public static ExecutorService getDefaultExecutorService() {
        ExecutorService defaultExecutorService;
        try {
            defaultExecutorService = (ExecutorService) new InitialContext().lookup("java:comp/DefaultManagedScheduledExecutorService");
        } catch (NamingException e) {
            if ("true".equalsIgnoreCase(System.getProperty("com.ibm.ws.microprofile.faulttolerance.jse"))) {
                //this is really intended for unittest
                defaultExecutorService = Executors.newScheduledThreadPool(5);
            } else {
                throw new FaultToleranceException(e);
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
