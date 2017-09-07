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
package com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.faulttolerance.Asynchronous;

import com.ibm.ws.microprofile.faulttolerance_fat.cdi.AsyncServlet;

@ApplicationScoped
public class AsyncCallableBean implements Callable<Future<String>> {

    /** {@inheritDoc} */
    @Override
    @Asynchronous
    public Future<String> call() throws Exception {
        Thread.sleep(AsyncServlet.WORK_TIME);
        return CompletableFuture.completedFuture("Done");
    }

}
