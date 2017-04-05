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
 * This error is thrown by the RESTHandlerContainer when a user error is encountered by a RESTHandler.
 * 
 * @ibm-spi
 */
public class RESTHandlerUserError extends RuntimeException {

    private static final long serialVersionUID = -3647481857680022528L;

    private int statusCode = 400;

    public RESTHandlerUserError(Exception e) {
        super(e);
    }

    public RESTHandlerUserError(String msg) {
        super(msg);
    }

    public void setStatusCode(int code) {
        statusCode = code;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
