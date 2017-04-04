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
package com.ibm.ws.jaxrs20.server.internal;

import java.util.ResourceBundle;

import com.ibm.ws.jaxrs20.api.JaxRsFactoryBeanCustomizer;

/**
 *
 */
public class JaxRsServerConstants {

    public static final String TR_GROUP = "JaxRsServer";
    public static final String TR_RESOURCE_BUNDLE = "com.ibm.ws.jaxrs20.server.internal.resources.JaxRsServerMessages";
    public static final ResourceBundle messages = ResourceBundle.getBundle(TR_RESOURCE_BUNDLE);

    public static final String JAXRS_APPLICATION_PARAM = "javax.ws.rs.Application";
    public static final String APPLICATION_ROOT_CLASS_NAME = "javax.ws.rs.core.Application";
    public static final String LIBERTY_JAXRS_SERVLET_CLASS_NAME = "com.ibm.websphere.jaxrs.server.IBMRestServlet";

    /**
     * Identify the key value for SERVLET_CONTEXT instance while publishing the endpoints
     */
    public static final String SERVLET_CONTEXT = "SERVLET_CONTEXT";

    /**
     * The key value for a set of JaxRsFactoryBeanCustomizer. The expected type is Set of JaxRsFactoryBeanCustomizer.
     */
    public static final String BEAN_CUSTOMIZER = JaxRsFactoryBeanCustomizer.class.getName();

    /**
     * Identify the key value for IWebAppNameSpaceCollaborator instance, which is mostly used for EJB based Web Services.
     */

    public static final String NAMESPACE_COLLABORATOR = "NAMESPACE_COLLABORATOR";
}
