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
package com.ibm.ws.microprofile.faulttolerance_fat.cdi;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans.BulkheadMultiRequestBean;

/**
 * Support servlet for testing synchronous bulkhead methods
 * <p>
 * This test servlet works differently to most of the others. Rather than running most of the test here, it merely passes calls onto the bulkhead bean.
 * The client then validates the responses. This is to make it easier to fire lots of requests in parallel to a bean, without using the {@code Asynchronous} annotation.
 * <p>
 * Note that even if you use a ManagedExecutorService, you lose the request context, which is unhelpful as our other test beans are request scoped.
 */
@WebServlet("multi-request-bulkhead")
public class MultiRequestBulkheadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Inject
    BulkheadMultiRequestBean bean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        Date startTime = new Date();
        try {
            bean.connectC("data");
            long end = System.currentTimeMillis();
            long duration = end - start;
            resp.getWriter().println("Success - started " + startTime + " duration: " + duration);
        } catch (Throwable t) {
            long end = System.currentTimeMillis();
            long duration = end - start;
            resp.getWriter().println("Failure - started " + startTime + " duration: " + duration);
            t.printStackTrace(resp.getWriter());
        }
    }

}
