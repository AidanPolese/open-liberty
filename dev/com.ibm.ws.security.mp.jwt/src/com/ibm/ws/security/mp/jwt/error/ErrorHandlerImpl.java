/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.mp.jwt.error;

import javax.servlet.http.HttpServletResponse;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.security.mp.jwt.TraceConstants;
import com.ibm.wsspi.security.tai.TAIResult;

/**
 *
 */
public class ErrorHandlerImpl implements ErrorHandler {

    private static final TraceComponent tc = Tr.register(ErrorHandlerImpl.class, TraceConstants.TRACE_GROUP, TraceConstants.MESSAGE_BUNDLE);
    private static final String AUTH_HEADER = "WWW-Authenticate";
    private static final String REALM = "MP-JWT";
    private static final String ERROR_CODE = ", error=\"invalid_token\",";
    private static final String ERROR_DESCRIPTION = " error_description=\"";
    private static final String BEARER = "Bearer realm=\"" + REALM + "\"";

    public static ErrorHandler getInstance() {
        return new ErrorHandlerImpl();
    }

    public ErrorHandlerImpl() {
    }

    @Override
    public TAIResult handleErrorResponse(HttpServletResponse response, TAIResult result) {

        handleErrorResponse(response, result.getStatus());
        return result;
    }

    @Override
    public void handleErrorResponse(HttpServletResponse response, int httpErrorCode) {

        if (!response.isCommitted()) {
            response.setStatus(httpErrorCode);
        }

        String errorMessage = getErrorMessage();//Tr.formatMessage(tc, "SOCIAL_LOGIN_FRONT_END_ERROR"); // CWWKS5489E
        response.setHeader(AUTH_HEADER, errorMessage);

        //writeErrorHtml(response, errorHeader, errorMessage);
    }

    String getErrorMessage() {

        String message = getRealmMessage();

        message += ERROR_CODE;
        message += ERROR_DESCRIPTION;
        message += "Check the provided JWT token";
        message += "\"";
        return message;
    }

    String getRealmMessage() {
        return BEARER;
    }
}
