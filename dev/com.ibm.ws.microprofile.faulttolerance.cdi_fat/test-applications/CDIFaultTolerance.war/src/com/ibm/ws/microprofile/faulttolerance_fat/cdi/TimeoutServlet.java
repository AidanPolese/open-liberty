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

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;

import com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans.TimeoutBean;
import com.ibm.ws.microprofile.faulttolerance_fat.util.ConnectException;

import componenttest.app.FATServlet;

/**
 * Servlet implementation class Test
 */
@WebServlet("/timeout")
public class TimeoutServlet extends FATServlet {
    private static final long serialVersionUID = 1L;

    @Inject
    TimeoutBean bean;

    public void testTimeout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //should timeout after a second as per default
        long start = System.currentTimeMillis();
        try {
            bean.connectA();
            throw new AssertionError("TimeoutException not thrown");
        } catch (TimeoutException e) {
            //expected!
            long timeout = System.currentTimeMillis();
            long duration = timeout - start;
            if (duration > 2000) { //the default timeout is 1000ms, if it takes 2000ms to fail then there is something wrong
                throw new AssertionError("TimeoutException not thrown quickly enough: " + timeout);
            }
        } catch (ConnectException e) {
            throw new ServletException(e);
        }

    }

    public void testException(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //should just throw an exception (we're checking we get the right exception even thought it is async internally)
        try {
            bean.connectB();
        } catch (ConnectException e) {
            String expected = "ConnectException: A simple exception";
            String actual = e.getMessage();
            if (!expected.equals(actual)) {
                throw new AssertionError("Expected: " + expected + ", Actual: " + actual);
            }
        }
    }
}
