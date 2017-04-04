/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.rest.handler.helper;

/**
 * This error is thrown by the RESTHandlerContainer when a particular HTTP method is not allowed.
 * 
 * @ibm-spi
 */
public class RESTHandlerMethodNotAllowedError extends RuntimeException {

    private static final long serialVersionUID = -3647481857680022528L;

    private final int statusCode = 405; //"Method Not Allowed"
    private final String allowedMethods;

    public RESTHandlerMethodNotAllowedError(String allowedMethods) {
        super();
        this.allowedMethods = allowedMethods;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getAllowedMethods() {
        return allowedMethods;
    }

}
