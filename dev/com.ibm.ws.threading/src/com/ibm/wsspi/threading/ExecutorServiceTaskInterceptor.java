/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.threading;

import java.util.concurrent.Callable;

/**
 * Provides a mechanism to implement generic preinvoke and postinvoke logic for all tasks submitted to the
 * Liberty default executor.
 * 
 * When a task is submitted to the Liberty default executor for execution, the task will be offered to the
 * wrap method of an ExecutorServiceTaskInterceptor in the service registry. The task returned by the
 * wrap method will replace the original task.
 * 
 * If there are additional implementations of ExecutorServiceTaskInterceptor in the service registry, this
 * process will continue iteratively, with the task returned by the first ExecutorServiceTaskInterceptor
 * being passed to the next ExecutorServiceTaskInterceptor until all registered ExecutorServiceTaskInterceptor
 * implementations are given a chance to wrap the task.  The task returned by the final interceptor will be
 * submitted for execution.
 * 
 * In the case of multiple implementations of ExecutorServiceTaskInterceptor, the order in which their wrap
 * methods are invoked is undefined.
 * 
 * Note that use of this interface should generally be avoided.  The primary use case is for embeddeders who
 * have a specific need to get control before and after every single task executed by the application server.
 * This is not a common need, and so careful consideration should be given before registering an
 * implementation. 
 */
public interface ExecutorServiceTaskInterceptor {
    public Runnable wrap(Runnable r);

    public <T> Callable<T> wrap(Callable<T> c);
}
