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

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * This error is thrown by the RESTHandlerContainer when an OSGi service is not bound.
 * 
 * @ibm-spi
 */
public class RESTHandlerOSGiError extends RESTHandlerInternalError {

    private static final long serialVersionUID = -3647481857680022528L;
    private static final TraceComponent tc = Tr.register(RESTHandlerOSGiError.class);

    private int statusCode = 500;

    public RESTHandlerOSGiError(String missingOSGiService) {
        super(Tr.formatMessage(tc, "OSGI_SERVICE_ERROR", missingOSGiService));
    }

    @Override
    public void setStatusCode(int code) {
        statusCode = code;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }
}
