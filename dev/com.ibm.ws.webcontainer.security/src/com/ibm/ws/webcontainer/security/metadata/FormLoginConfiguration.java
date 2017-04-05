/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.security.metadata;

/**
 * Represents a form-login-config element in web.xml.
 * <p/>
 * <pre>
 * &lt;form-login-config&gt;
 * &lt;form-login-page&gt;form-login-page&lt;/form-login-page&gt;
 * &lt;form-error-page>form-error-page&lt;/form-error-page&gt;
 * &lt;/form-login-config&gt;
 * </pre>
 */
public interface FormLoginConfiguration {

    /**
     * Gets the form login page.
     */
    public String getLoginPage();

    /**
     * Gets the form error page.
     */
    public String getErrorPage();
}
