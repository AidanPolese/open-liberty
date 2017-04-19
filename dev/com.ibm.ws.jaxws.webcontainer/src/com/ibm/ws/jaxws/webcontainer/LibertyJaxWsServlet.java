/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxws.webcontainer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ws.jaxws.endpoint.JaxWsWebEndpoint;
import com.ibm.ws.runtime.metadata.ComponentMetaData;
import com.ibm.ws.threadContext.ComponentMetaDataAccessorImpl;
import com.ibm.wsspi.webcontainer.collaborator.IWebAppNameSpaceCollaborator;

/**
 *
 */
public class LibertyJaxWsServlet extends HttpServlet {

    private static final long serialVersionUID = -6835560282014155024L;

    private static final List<String> KNOWN_HTTP_VERBS = Arrays.asList(new String[] { "POST", "GET", "PUT", "DELETE", "HEAD", "OPTIONS", "TRACE" });

    private static final String HTML_CONTENT_TYPE = "text/html";

    private final transient JaxWsWebEndpoint endpoint;

    private final transient IWebAppNameSpaceCollaborator collaborator;

    public LibertyJaxWsServlet(JaxWsWebEndpoint endpoint) {
        this(endpoint, null);
    }

    public LibertyJaxWsServlet(JaxWsWebEndpoint endpoint, IWebAppNameSpaceCollaborator collaborator) {
        this.endpoint = endpoint;
        this.collaborator = collaborator;
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        endpoint.init(servletConfig);
    }

    /**
     * As AbstractHTTPServlet in CXF, with this, it will make sure that, all the request methods
     * will be routed to handleRequest method.
     */
    @Override
    public void service(ServletRequest req, ServletResponse res)
                    throws ServletException, IOException {

        HttpServletRequest request;
        HttpServletResponse response;

        try {
            request = (HttpServletRequest) req;
            if (collaborator != null) {
                ComponentMetaData componentMetaData = ComponentMetaDataAccessorImpl.getComponentMetaDataAccessor().getComponentMetaData();
                request = new JaxWsHttpServletRequestAdapter(request, collaborator, componentMetaData);
            }
            response = (HttpServletResponse) res;
        } catch (ClassCastException e) {
            throw new ServletException("Unrecognized HTTP request or response object", e);
        }

        String method = request.getMethod();
        if (KNOWN_HTTP_VERBS.contains(method)) {
            super.service(request, response);
        } else {
            handleRequest(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (null == request.getQueryString()) {
            reportAvailableService(request, response);
        } else {
            handleRequest(request, response);
        }
    }

    /**
     * Prints a welcome message for the endpoint
     * 
     * @param resquest
     * @param response
     */
    private void reportAvailableService(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        response.setContentType(HTML_CONTENT_TYPE);
        writer.println("<h2>" + request.getServletPath() + "</h2>");
        writer.println("<h3>Hello! This is a CXF Web Service!</h3>");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleRequest(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleRequest(request, response);
    }

    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleRequest(request, response);
    }

    protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        endpoint.invoke(request, response);
    }

    /** {@inheritDoc} */
    @Override
    public void destroy() {
        endpoint.destroy();
        super.destroy();
    }
}
