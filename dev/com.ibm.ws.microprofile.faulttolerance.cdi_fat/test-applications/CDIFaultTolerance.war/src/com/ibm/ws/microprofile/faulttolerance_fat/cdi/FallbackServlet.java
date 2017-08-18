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
import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans.FallbackBean;
import com.ibm.ws.microprofile.faulttolerance_fat.util.ConnectException;
import com.ibm.ws.microprofile.faulttolerance_fat.util.Connection;

import componenttest.app.FATServlet;

/**
 * Servlet implementation class Test
 */
@WebServlet("/fallback")
public class FallbackServlet extends FATServlet {
    private static final long serialVersionUID = 1L;

    @Inject
    FallbackBean bean;

    public void testFallback(HttpServletRequest request,
                             HttpServletResponse response) throws ServletException, IOException, ConnectException, NoSuchMethodException, SecurityException {
        //should be retried twice and then fallback and we get a result
        Connection connection = bean.connectA();
        String data = connection.getData();
        Method method = FallbackBean.class.getMethod("connectA");
        if (!("Fallback for: Execution Context: " + method + " - data!").equals(data)) {
            throw new AssertionError("Bad Data: " + data);
        }
    }
}
