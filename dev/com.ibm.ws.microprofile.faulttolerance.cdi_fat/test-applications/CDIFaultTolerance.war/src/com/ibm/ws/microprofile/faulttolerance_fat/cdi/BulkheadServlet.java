/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.microprofile.faulttolerance_fat.cdi;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.microprofile.faulttolerance.exceptions.BulkheadException;

import com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans.BulkheadBean;

import componenttest.app.FATServlet;

/**
 * Servlet implementation class Test
 */
@WebServlet("/bulkhead")
public class BulkheadServlet extends FATServlet {
    private static final long serialVersionUID = 1L;

    @Inject
    BulkheadBean bean1;
    @Inject
    BulkheadBean bean2;
    @Inject
    BulkheadBean bean3;

    public void testBulkheadSmall(HttpServletRequest request,
                                  HttpServletResponse response) throws ServletException, IOException, InterruptedException, ExecutionException, TimeoutException {
        //connectA has a poolSize of 2
        //first two should be run straight away, in parallel, each around 5 seconds
        Future<Boolean> future1 = bean1.connectA("One");
        Thread.sleep(100);
        Future<Boolean> future2 = bean1.connectA("Two");
        Thread.sleep(100);

        //next two should wait until the others have finished
        Future<Boolean> future3 = bean1.connectA("Three");
        Thread.sleep(100);
        Future<Boolean> future4 = bean1.connectA("Four");
        Thread.sleep(100);

        //total time should be just over 10s
        Thread.sleep(11000);

        if (!future1.get(1000, TimeUnit.MILLISECONDS)) {
            throw new AssertionError("Future1 did not complete properly");
        }
        if (!future2.get(1000, TimeUnit.MILLISECONDS)) {
            throw new AssertionError("Future2 did not complete properly");
        }
        if (!future3.get(1000, TimeUnit.MILLISECONDS)) {
            throw new AssertionError("Future3 did not complete properly");
        }
        if (!future4.get(1000, TimeUnit.MILLISECONDS)) {
            throw new AssertionError("Future4 did not complete properly");
        }

    }

    public void testBulkheadQueueFull(HttpServletRequest request,
                                      HttpServletResponse response) throws ServletException, IOException, InterruptedException, ExecutionException, TimeoutException {
        //connectA has a poolSize of 2
        //first two should be run straight away, in parallel, each around 5 seconds
        Future<Boolean> future1 = bean2.connectA("One");
        Future<Boolean> future2 = bean2.connectA("Two");
        Future<Boolean> future3 = bean2.connectA("Three");
        Future<Boolean> future4 = bean2.connectA("Four");

        try {
            Future<Boolean> future5 = bean2.connectA("Five");
            throw new AssertionError("BulkheadException not thrown");
        } catch (BulkheadException e) {
            //expected
        }

    }

    public void testBulkheadTimeout(HttpServletRequest request,
                                    HttpServletResponse response) throws ServletException, IOException, InterruptedException, TimeoutException, ExecutionException, AssertionError {
        //connectB has a poolSize of 2 but a timeout of 2s
        //first two should be run straight away, in parallel, but should timeout after 2s
        Future<Boolean> future1 = bean3.connectB("One"); //without timeout would take 5s
        Thread.sleep(100);
        Future<Boolean> future2 = bean3.connectB("Two"); //without timeout would take 5s
        Thread.sleep(2000); //sleep until timeout has occurred

        //next two should run straight away and complete quickly
        Future<Boolean> future3 = bean3.connectB("Three");
        Future<Boolean> future4 = bean3.connectB("Four");
        Thread.sleep(200);

        try {
            future1.get(1000, TimeUnit.MILLISECONDS);
            throw new AssertionError("Future1 did not timeout properly");
        } catch (org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException e) {
            //expected
        }

        try {
            future2.get(1000, TimeUnit.MILLISECONDS);
            throw new AssertionError("Future2 did not timeout properly");
        } catch (org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException e) {
            //expected
        }

        if (!future3.get(1000, TimeUnit.MILLISECONDS)) {
            throw new AssertionError("Future3 did not complete properly");
        }
        if (!future4.get(1000, TimeUnit.MILLISECONDS)) {
            throw new AssertionError("Future4 did not complete properly");
        }
    }

}
