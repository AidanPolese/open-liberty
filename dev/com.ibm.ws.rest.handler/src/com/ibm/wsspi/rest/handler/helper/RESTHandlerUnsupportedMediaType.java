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

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * This error is thrown by the RESTHandlerContainer when a request's media type is not supported.
 * 
 * @ibm-spi
 */
public class RESTHandlerUnsupportedMediaType extends RuntimeException {

    private static final long serialVersionUID = -3647481857680022528L;

    private static final TraceComponent tc = Tr.register(RESTHandlerUnsupportedMediaType.class);

    private int statusCode = 415; // Unsupported Media Type

    public RESTHandlerUnsupportedMediaType(String mediaType) {
        super(Tr.formatMessage(tc, "UNSUPPORTED_MEDIA_TYPE", mediaType));
    }

    public void setStatusCode(int code) {
        statusCode = code;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
