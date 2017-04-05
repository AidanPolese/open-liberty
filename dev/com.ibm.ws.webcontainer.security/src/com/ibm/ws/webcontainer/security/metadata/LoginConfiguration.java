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
 * Represents a login-config element in web.xml.
 * <p>
 * From the specification:
 * <pre>
 * 18. login-config Element
 * The login-config is used to configure the authentication method that should
 * be used, the realm name that should be used for this application, and the
 * attributes that are needed by the form login mechanism. The sub-element authmethod
 * configures the authentication mechanism for the Web application. The
 * element content must be either BASIC, DIGEST, FORM, CLIENT-CERT, or a
 * Chapter 14 Deployment Descriptor 157
 * vendor-specific authentication scheme.
 * </pre>
 * <p/>
 * <pre>
 * &lt;login-config&gt;
 * &lt;auth-method&gt;auth-method&lt;/auth-method&gt;
 * &lt;realm-name&gt;realm-name&lt;/realm-name&gt;
 * &lt;form-login-config&gt;
 * &lt;form-login-page&gt;form-login-page&lt;/form-login-page&gt;
 * &lt;form-error-page&gt;form-error-page&lt;/form-error-page&gt;
 * &lt;/form-login-config&gt;
 * &lt;/login-config>
 * </pre>
 */
public interface LoginConfiguration {

    /**
     * Basic authentication value for &lt;auth-method&gt;
     * and {@link javax.servlet.http.HttpServletRequest#getAuthType()}.
     * 
     * @see javax.servlet.http.HttpServletRequest#BASIC_AUTH
     * @see javax.servlet.http.HttpServletRequest#getAuthType()
     */
    public static final String BASIC = "BASIC";
    /**
     * Form login authentication value for &lt;auth-method&gt;
     * and {@link javax.servlet.http.HttpServletRequest#getAuthType()}.
     * 
     * @see javax.servlet.http.HttpServletRequest#FORM_AUTH
     * @see javax.servlet.http.HttpServletRequest#getAuthType()
     */
    public static final String FORM = "FORM";
    /**
     * Client certificate authentication value for {@link javax.servlet.http.HttpServletRequest#getAuthType()}.
     * 
     * @see javax.servlet.http.HttpServletRequest#CLIENT_CERT_AUTH
     * @see javax.servlet.http.HttpServletRequest#getAuthType()
     */
    public static final String CLIENT_CERT = "CLIENT_CERT";

    /**
     * Gets the authentication method.
     */
    public String getAuthenticationMethod();

    /**
     * Gets the realm name.
     */
    public String getRealmName();

    /**
     * Gets the form login configuration.
     */
    public FormLoginConfiguration getFormLoginConfiguration();
}
