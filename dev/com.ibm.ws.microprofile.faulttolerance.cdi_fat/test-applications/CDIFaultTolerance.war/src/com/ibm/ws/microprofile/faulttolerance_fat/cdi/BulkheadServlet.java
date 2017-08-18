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

    //a basic unit of time, in ms, so that test timeouts and thresholds can be scaled together
    public static final long TEST_TIME_UNIT = 1000;

    //A tiny increment of time, used to ensure this test runs correctly on the test servers. 
    public static final long TEST_TWEAK_TIME_UNIT = TEST_TIME_UNIT / 10;

    //This is the ammount of time, in ms, that we allow for a Future to be returned. Normally it should come back almost instantly
    //but we are finding that it can take a bit longer when running in the build.
    // 13/07/17 - Initially it was 1000, increasing to 2000.
    public static final long FUTURE_THRESHOLD = 2 * TEST_TIME_UNIT;

    //the bulkhead timeout value.
    public static final long TIMEOUT = 2 * TEST_TIME_UNIT;

    //the amount of time that async tasks will sleep if they are simulating 'work'
    public static final long WORK_TIME = 5 * TEST_TIME_UNIT;

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
        //These sleep statements are fine tuning to ensure this test functions.
	//The increments are small enough that it shuld not impact the logic of this test. 
        Thread.sleep(TEST_TWEAK_TIME_UNIT);
        Future<Boolean> future2 = bean1.connectA("Two");
        Thread.sleep(TEST_TWEAK_TIME_UNIT);

        //next two should wait until the others have finished
        Future<Boolean> future3 = bean1.connectA("Three");
        Thread.sleep(TEST_TWEAK_TIME_UNIT);
        Future<Boolean> future4 = bean1.connectA("Four");
        Thread.sleep(TEST_TWEAK_TIME_UNIT);

        //total time should be just over 10s
        Thread.sleep((WORK_TIME * 2) + TEST_TIME_UNIT);

        if (!future1.get(FUTURE_THRESHOLD, TimeUnit.MILLISECONDS)) {
            throw new AssertionError("Future1 did not complete properly");
        }
        if (!future2.get(FUTURE_THRESHOLD, TimeUnit.MILLISECONDS)) {
            throw new AssertionError("Future2 did not complete properly");
        }
        if (!future3.get(FUTURE_THRESHOLD, TimeUnit.MILLISECONDS)) {
            throw new AssertionError("Future3 did not complete properly");
        }
        if (!future4.get(FUTURE_THRESHOLD, TimeUnit.MILLISECONDS)) {
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
        Thread.sleep(TEST_TWEAK_TIME_UNIT);
        Future<Boolean> future2 = bean3.connectB("Two"); //without timeout would take 5s
        Thread.sleep(TIMEOUT); //sleep until timeout has occurred

        //next two should run straight away and complete quickly
        Future<Boolean> future3 = bean3.connectB("Three");
        Future<Boolean> future4 = bean3.connectB("Four");
        Thread.sleep(TEST_TWEAK_TIME_UNIT * 2);

        try {
            future1.get(FUTURE_THRESHOLD, TimeUnit.MILLISECONDS);
            throw new AssertionError("Future1 did not timeout properly");
        } catch (org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException e) {
            //expected
        }

        try {
            future2.get(FUTURE_THRESHOLD, TimeUnit.MILLISECONDS);
            throw new AssertionError("Future2 did not timeout properly");
        } catch (org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException e) {
            //expected
        }

        if (!future3.get(FUTURE_THRESHOLD, TimeUnit.MILLISECONDS)) {
            throw new AssertionError("Future3 did not complete properly");
        }
        if (!future4.get(FUTURE_THRESHOLD, TimeUnit.MILLISECONDS)) {
            throw new AssertionError("Future4 did not complete properly");
        }
    }

}
