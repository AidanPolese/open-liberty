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
 * This error is thrown by the RESTHandlerContainer when a request is missing a required header.
 * 
 * @ibm-spi
 */
public class RESTHandlerMissingRequiredHeader extends RESTHandlerUserError {

    private static final long serialVersionUID = -3647481857680022528L;
    private static final TraceComponent tc = Tr.register(RESTHandlerMissingRequiredHeader.class);

    public RESTHandlerMissingRequiredHeader(String header) {
        super(Tr.formatMessage(tc, "MISSING_HEADER", header));
    }

}
