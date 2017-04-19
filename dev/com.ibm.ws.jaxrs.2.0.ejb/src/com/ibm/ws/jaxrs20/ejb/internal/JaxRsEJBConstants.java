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
package com.ibm.ws.jaxrs20.ejb.internal;

import java.util.ResourceBundle;

public class JaxRsEJBConstants {
    public static final String TR_GROUP = "JaxRs20EJB";

    public static final String TR_RESOURCE_BUNDLE = "com.ibm.ws.jaxrs20.ejb.internal.resources.JaxRsEJBMessages";

    public static final ResourceBundle messages = ResourceBundle.getBundle(TR_RESOURCE_BUNDLE);

    public static final String JAXRS_APPLICATION_PARAM = "javax.ws.rs.Application";
    public static final String APPLICATION_ROOT_CLASS_NAME = "javax.ws.rs.core.Application";
    public static final String JNDI_NAME = "JNDI_NAME";
    public static final String EJB_OBJECT_CACHE = "EJBObjCache";
    public static final String LIBERTY_JAXRS_SERVLET_CLASS_NAME = "com.ibm.websphere.jaxrs.server.IBMRestServlet";
    public static final String EJB_TYPE = "EJBType";
}
