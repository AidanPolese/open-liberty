/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel.wsbnd;

/**
 *
 */
public interface HttpPublishing {

    public static String CONTEXT_ROOT_ATTRIBUTE_NAME = "context-root";

    public static String WEBSERVICE_SECURITY_ELEMENT_NAME = "webservice-security";

    /**
     * @return context-root="..." attribute value
     */
    public String getContextRoot();

    /**
     * @return &lt;webservice-security>, or null if unspecified
     */
    public WebserviceSecurity getWebserviceSecurity();
}
