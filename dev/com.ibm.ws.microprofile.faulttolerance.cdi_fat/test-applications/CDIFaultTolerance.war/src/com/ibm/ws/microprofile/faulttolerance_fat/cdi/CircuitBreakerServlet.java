package com.ibm.ws.microprofile.faulttolerance_fat.cdi;

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

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException;
import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;

import com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans.CircuitBreakerBean;
import com.ibm.ws.microprofile.faulttolerance_fat.util.ConnectException;

import componenttest.app.FATServlet;

/**
 * Servlet implementation class Test
 */
@WebServlet("/circuitbreaker")
public class CircuitBreakerServlet extends FATServlet {
    private static final long serialVersionUID = 1L;

    @Inject
    CircuitBreakerBean bean;

    /**
     * @throws InterruptedException
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    public void testCBFailureThresholdWithTimeout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, InterruptedException {

        // FaultTolerance object with circuit breaker, should fail 3 times
        for (int i = 0; i < 3; i++) {
            try {
                bean.serviceA();
                throw new AssertionError("TimeoutException not caught");
            } catch (TimeoutException e) {
                //expected
            }
        }

        try {
            bean.serviceA();
            throw new AssertionError("CircuitBreakerOpenException not caught");
        } catch (CircuitBreakerOpenException e) {
            //expected
        }

        //allow time for the circuit to re-close
        Thread.sleep(3000);

        String res = bean.serviceA();
        if (!"serviceA: 4".equals(res)) {
            throw new AssertionError("Bad Result: " + res);
        }
    }

    /**
     * @throws InterruptedException
     * @throws ConnectException
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    public void testCBFailureThresholdWithException(HttpServletRequest request,
                                                    HttpServletResponse response) throws ServletException, IOException, InterruptedException, ConnectException {

        // FaultTolerance object with circuit breaker, should fail 3 times
        for (int i = 0; i < 3; i++) {
            try {
                bean.serviceB();
                throw new AssertionError("ConnectException not caught");
            } catch (ConnectException e) {
                if (!e.getMessage().equals("ConnectException: serviceB exception: " + (i + 1))) {
                    throw new AssertionError("ConnectException bad message: " + e.getMessage());
                }
            }
        }

        try {
            bean.serviceB();
            throw new AssertionError("CircuitBreakerOpenException not caught");
        } catch (CircuitBreakerOpenException e) {
            //expected
        }

        //allow time for the circuit to re-close
        Thread.sleep(3000);

        String res = bean.serviceB();
        if (!"serviceB: 4".equals(res)) {
            throw new AssertionError("Bad Result: " + res);
        }
    }
}
