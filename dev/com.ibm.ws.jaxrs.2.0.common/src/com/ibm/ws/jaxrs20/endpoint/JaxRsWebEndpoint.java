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
package com.ibm.ws.jaxrs20.endpoint;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ws.jaxrs20.api.JaxRsProviderFactoryService;

/**
 *
 */
public interface JaxRsWebEndpoint {

    public void init(ServletConfig servletConfig, JaxRsProviderFactoryService jaxRsProviderFactoryService) throws ServletException;

    public void invoke(HttpServletRequest request, HttpServletResponse response) throws ServletException;

    public void destroy();

    public void setEndpointInfoAddress(String add);
}
