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
package com.ibm.wsspi.rest.handler.helper;

public class RESTHandlerJsonException extends RuntimeException {

    private static final long serialVersionUID = -3647481857680022528L;

    private int statusCode;

    private boolean isMessageContentJson;

    public RESTHandlerJsonException(Exception e, int statusCode) {
        super(e);
        this.statusCode = statusCode;
    }

    public RESTHandlerJsonException(String msg, int statusCode) {
        super(msg);
        this.statusCode = statusCode;
    }

    public RESTHandlerJsonException(Exception e, int statusCode, boolean isMessageContentJSON) {
        super(e);
        this.statusCode = statusCode;
        this.isMessageContentJson = isMessageContentJSON;
    }

    public RESTHandlerJsonException(String msg, int statusCode, boolean isMessageContentJSON) {
        super(msg);
        this.statusCode = statusCode;
        this.isMessageContentJson = isMessageContentJSON;
    }

    public void setStatusCode(int code) {
        statusCode = code;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isMessageContentJSON() {
        return isMessageContentJson;
    }
}
