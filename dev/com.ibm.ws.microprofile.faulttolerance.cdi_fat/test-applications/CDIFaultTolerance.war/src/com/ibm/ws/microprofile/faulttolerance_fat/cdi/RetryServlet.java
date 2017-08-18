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

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans.RetryBeanB;
import com.ibm.ws.microprofile.faulttolerance_fat.util.ConnectException;
import com.ibm.ws.microprofile.faulttolerance_fat.util.DisconnectException;

import componenttest.app.FATServlet;

/**
 * Servlet implementation class Test
 */
@WebServlet("/retry")
public class RetryServlet extends FATServlet {
    private static final long serialVersionUID = 1L;

    @Inject
    RetryBeanB beanB;

    public void testRetry(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //should be retried 3 times as per default
        try {
            beanB.connectB();
            throw new AssertionError("Exception not thrown");
        } catch (ConnectException e) {
            String expected = "ConnectException: RetryBeanB Connect: 4";
            String actual = e.getMessage();
            if (!expected.equals(actual)) {
                throw new AssertionError("Expected: " + expected + ", Actual: " + actual);
            }
        }
    }

    public void testInheritedAnnotations(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //should be retried 2 times due to class level annotation on beanB
        try {
            beanB.connectA();
            throw new AssertionError("Exception not thrown");
        } catch (ConnectException e) {
            String expected = "ConnectException: RetryBeanA Connect: 3";
            String actual = e.getMessage();
            if (!expected.equals(actual)) {
                throw new AssertionError("Expected: " + expected + ", Actual: " + actual);
            }
        }
        //should be retried 4 times due to method level annotation in beanA
        try {
            beanB.disconnectA();
            throw new AssertionError("Exception not thrown");
        } catch (DisconnectException e) {
            String expected = "DisconnectException: RetryBeanA Disconnect: 5";
            String actual = e.getMessage();
            if (!expected.equals(actual)) {
                throw new AssertionError("Expected: " + expected + ", Actual: " + actual);
            }
        }
    }

}
