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

import com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans.AsyncBean;
import com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans.AsyncBean2;
import com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans.AsyncBean3;
import com.ibm.ws.microprofile.faulttolerance_fat.util.Connection;

import componenttest.app.FATServlet;

/**
 * Servlet implementation class Test
 */
@WebServlet("/async")
public class AsyncServlet extends FATServlet {
    private static final long serialVersionUID = 1L;

    //a basic unit of time, in ms, so that test timeouts and thresholds can be scaled together
    public static final long TEST_TIME_UNIT = 1000;

    //This is the ammount of time, in ms, that we allow for a Future to be returned. Normally it should come back almost instantly
    //but we are finding that it can take a bit longer when running in the build.
    // 13/07/17 - Initially it was 1000, increasing to 2000.
    public static final long FUTURE_THRESHOLD = 2 * TEST_TIME_UNIT;

    //the FaultTolerance timeout used on async tasks
    public static final long TIMEOUT = 2 * TEST_TIME_UNIT;

    //the amount of time that async tasks will sleep if they are simulating 'work'
    public static final long WORK_TIME = 3 * TIMEOUT;

    public static final long EXECUTION_THRESHOLD = WORK_TIME + FUTURE_THRESHOLD;

    @Inject
    AsyncBean bean;

    @Inject
    AsyncBean2 bean2;

    public void testAsync(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException, InterruptedException, ExecutionException, TimeoutException {
        //should return straight away even though the method has a 5s sleep in it
        long start = System.currentTimeMillis();
        System.out.println(start + " - calling AsyncBean.connectA");
        Future<Connection> future = bean.connectA();
        long end = System.currentTimeMillis();
        System.out.println(end + " - got future");
        long duration = end - start;
        if (duration > FUTURE_THRESHOLD) { //should have returned almost instantly, if it takes FUTURE_THRESHOLD then there is something wrong
            throw new AssertionError("Method did not return quickly enough: " + duration);
        }
        if (future.isDone()) {
            throw new AssertionError("Future completed too fast");
        }

        Thread.sleep(EXECUTION_THRESHOLD); //long enough for the call to complete
        if (!future.isDone()) {
            throw new AssertionError("Future did not complete fast enough");
        }
        start = System.currentTimeMillis();
        System.out.println(start + " - calling future.get");
        //we shouldn't need the extra timeout but don't want the test to hang if it is broken
        Connection conn = future.get(TEST_TIME_UNIT, TimeUnit.MILLISECONDS);
        end = System.currentTimeMillis();
        System.out.println(end + " - got connection");

        duration = end - start;
        if (duration > FUTURE_THRESHOLD) { //should have returned almost instantly, if it takes FUTURE_THRESHOLD then there is something wrong
            throw new AssertionError("Method did not return quickly enough: " + duration);
        }
        if (conn == null) {
            throw new AssertionError("Result not properly returned: " + conn);
        }
        String data = conn.getData();
        if (!AsyncBean.CONNECT_A_DATA.equals(data)) {
            throw new AssertionError("Bad data: " + data);
        }
    }

    public void testAsyncVoid(HttpServletRequest request,
                              HttpServletResponse response) throws ServletException, IOException, InterruptedException, ExecutionException, TimeoutException {
        //should return straight away even though the method has a 5s sleep in it
        long start = System.currentTimeMillis();
        System.out.println(start + " - calling AsyncBean.connectC");
        Future<Void> future = bean.connectC();
        long end = System.currentTimeMillis();
        System.out.println(end + " - got future");
        long duration = end - start;
        if (duration > FUTURE_THRESHOLD) { //should have returned almost instantly, if it takes FUTURE_THRESHOLD then there is something wrong
            throw new AssertionError("Method did not return quickly enough: " + duration);
        }
        if (future.isDone()) {
            throw new AssertionError("Future completed too fast");
        }

        Thread.sleep(EXECUTION_THRESHOLD); //long enough for the call to complete
        if (!future.isDone()) {
            throw new AssertionError("Future did not complete fast enough");
        }
        start = System.currentTimeMillis();
        System.out.println(start + " - calling future.get");
        //we shouldn't need the extra timeout but don't want the test to hang if it is broken
        Object obj = future.get(TEST_TIME_UNIT, TimeUnit.MILLISECONDS);
        end = System.currentTimeMillis();
        System.out.println(end + " - got connection");

        duration = end - start;
        if (duration > FUTURE_THRESHOLD) { //should have returned almost instantly, if it takes 1000ms then there is something wrong
            throw new AssertionError("Method did not return quickly enough: " + duration);
        }
        if (obj != null) {
            throw new AssertionError("Result should be null: " + obj);
        }
    }

    public void testAsyncTimeout(HttpServletRequest request,
                                 HttpServletResponse response) throws ServletException, IOException, InterruptedException, ExecutionException, TimeoutException {
        //should return straight away even though the method has a 5s sleep in it
        long start = System.currentTimeMillis();
        System.out.println(start + " - calling AsyncBean.connectB");
        Future<Connection> future = bean.connectB();
        long end = System.currentTimeMillis();
        System.out.println(end + " - got future");

        long duration = end - start;
        if (duration > FUTURE_THRESHOLD) { //should have returned almost instantly, if it takes FUTURE_THRESHOLD then there is something wrong
            throw new AssertionError("Method did not return quickly enough: " + duration);
        }
        if (future.isDone()) {
            throw new AssertionError("Future completed too fast");
        }

        Thread.sleep(TIMEOUT + TEST_TIME_UNIT); //long enough for the call to timeout but not to complete normally
        if (!future.isDone()) {
            throw new AssertionError("Future did not timeout fast enough");
        }

        try {
            //FaultTolerance should have already timedout the future so we're expecting the FT TimeoutException
            Connection conn = future.get(TEST_TIME_UNIT, TimeUnit.MILLISECONDS);
            throw new AssertionError("Future did not timeout properly");
        } catch (org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException t) {
            //expected
        }
    }

    public void testAsyncMethodTimeout(HttpServletRequest request,
                                       HttpServletResponse response) throws ServletException, IOException, InterruptedException, ExecutionException, TimeoutException {
        //should return straight away even though the method has a 5s sleep in it
        long start = System.currentTimeMillis();
        System.out.println(start + " - calling AsyncBean.connectB");
        Future<Connection> future = bean.connectB();
        long end = System.currentTimeMillis();
        System.out.println(end + " - got future");

        long duration = end - start;
        if (duration > FUTURE_THRESHOLD) { //should have returned almost instantly, if it takes FUTURE_THRESHOLD then there is something wrong
            throw new AssertionError("Method did not return quickly enough: " + duration);
        }
        if (future.isDone()) {
            throw new AssertionError("Future completed too fast");
        }

        try {
            //FaultTolerance should NOT have timedout yet so the short timeout on the method should result in a concurrent TimeoutException
            Connection conn = future.get(100, TimeUnit.MILLISECONDS);
            throw new AssertionError("Future did not timeout properly");
        } catch (TimeoutException t) {
            //expected
        }
    }

    //AsyncBean2 calls AsyncBean3 so that's a double thread jump
    public void testAsyncDoubleJump(HttpServletRequest request,
                                    HttpServletResponse response) throws ServletException, IOException, InterruptedException, ExecutionException, TimeoutException {
        //should return straight away even though the method has a 5s sleep in it
        long start = System.currentTimeMillis();
        System.out.println(start + " - calling AsyncBean.connectA");
        Future<Connection> future = bean2.connectA();
        long end = System.currentTimeMillis();
        System.out.println(end + " - got future");
        long duration = end - start;
        if (duration > FUTURE_THRESHOLD) { //should have returned almost instantly, if it takes 1000ms then there is something wrong
            throw new AssertionError("Method did not return quickly enough: " + duration);
        }
        if (future.isDone()) {
            throw new AssertionError("Future completed too fast");
        }

        Thread.sleep(7000); //long enough for the call to complete
        if (!future.isDone()) {
            throw new AssertionError("Future did not complete fast enough");
        }
        start = System.currentTimeMillis();
        System.out.println(start + " - calling future.get");
        //we shouldn't need the extra timeout but don't want the test to hang if it is broken
        Connection conn = future.get(FUTURE_THRESHOLD, TimeUnit.MILLISECONDS);
        end = System.currentTimeMillis();
        System.out.println(end + " - got connection");

        duration = end - start;
        if (duration > FUTURE_THRESHOLD) { //should have returned almost instantly, if it takes 1000ms then there is something wrong
            throw new AssertionError("Method did not return quickly enough: " + duration);
        }
        if (conn == null) {
            throw new AssertionError("Result not properly returned: " + conn);
        }
        String data = conn.getData();
        if (!AsyncBean3.CONNECT_A_DATA.equals(data)) {
            throw new AssertionError("Bad data: " + data);
        }
    }
}
