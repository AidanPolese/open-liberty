/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jmx.connector.server.rest;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.ws.rest.handler.helper.ServletRESTRequestImpl;
import com.ibm.ws.rest.handler.helper.ServletRESTResponseImpl;
import com.ibm.ws.rest.handler.internal.TraceConstants;
import com.ibm.wsspi.rest.handler.RESTHandlerContainer;

public class JMXRESTProxyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private transient RESTHandlerContainer REST_HANDLER_CONTAINER = null;

    /** {@inheritDoc} */
    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        // Setup service - will handle 
        getAndSetRESTHandlerContainer(request);

        // Request reached here meaning web.xml mapped the URL to us
        handleWithDelegate(request, response);
    }

    /**
     * For any request URL other than the context root, delegate to the
     * appropriate handler. If no handler is available, a 404 will be set
     * into the response.
     * 
     * @param request
     * @param response
     * @param pathInfo
     * @throws IOException
     */
    private void handleWithDelegate(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        //Delegate to handler
        boolean foundHandler = REST_HANDLER_CONTAINER.handleRequest(new ServletRESTRequestImpl(request), new ServletRESTResponseImpl(response));

        if (!foundHandler) {
            //No handler found, so we send back a 404 "not found" response.
            String errorMsg = TraceNLS.getFormattedMessage(this.getClass(),
                                                           TraceConstants.TRACE_BUNDLE_CORE,
                                                           "HANDLER_NOT_FOUND_ERROR",
                                                           new Object[] { request.getRequestURI() },
                                                           "CWWKO1000E: There are no registered handlers that match the requested URL {0}");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, errorMsg);
        }
    }

    /**
     * Grabs the RESTHandlerContainer from the OSGi service registry and stores
     * it to {@link #REST_HANDLER_CONTAINER}.
     * 
     * @param request The HttpServletRequest from which we'll get the OSGi BundleContext
     * @throws ServletException When the RESTHandlerContainer service is unavailable
     */
    private synchronized void getAndSetRESTHandlerContainer(HttpServletRequest request) throws ServletException {
        if (REST_HANDLER_CONTAINER == null) {
            //Get the bundle context
            HttpSession session = request.getSession();
            ServletContext sc = session.getServletContext();
            BundleContext ctxt = (BundleContext) sc.getAttribute("osgi-bundlecontext");

            ServiceReference<RESTHandlerContainer> ref = ctxt.getServiceReference(RESTHandlerContainer.class);

            if (ref == null) {
                // Couldn't find service, so throw the error.
                throw new ServletException(TraceNLS.getFormattedMessage(this.getClass(),
                                                                        TraceConstants.TRACE_BUNDLE_CORE,
                                                                        "OSGI_SERVICE_ERROR",
                                                                        new Object[] { "RESTHandlerContainer" },
                                                                        "CWWKO1001E: OSGi service {0} is not available."));
            } else {
                REST_HANDLER_CONTAINER = ctxt.getService(ref);
            }
        }
    }
}
