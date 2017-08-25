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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.security.mp.jwt.TraceConstants;
import com.ibm.wsspi.security.tai.TAIResult;

/**
 * need to be thread-safe
 * otherwise, need to enhance the code
 */
public class ErrorHandlerImpl implements ErrorHandler {

    private static final TraceComponent tc = Tr.register(ErrorHandlerImpl.class, TraceConstants.TRACE_GROUP, TraceConstants.MESSAGE_BUNDLE);

    public static ErrorHandler getInstance() {
        return new ErrorHandlerImpl();
    }

    public ErrorHandlerImpl() {
    }

    @Override
    public void handleErrorResponse(HttpServletResponse response) {
        handleErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED);
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

        String errorHeader = "HTTP Error 403 - Forbidden";
        if (httpErrorCode != 403) {
            errorHeader = "HTTP Error " + httpErrorCode;
        }

        String errorMessage = Tr.formatMessage(tc, "SOCIAL_LOGIN_FRONT_END_ERROR"); // CWWKS5489E
        writeErrorHtml(response, errorHeader, errorMessage);
    }

    protected void writeErrorHtml(HttpServletResponse response, String errorHeader, String errorMessage) {
        StringBuffer sb = new StringBuffer();
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<meta http-equiv=\"Pragma\" content=\"no-cache\"/>");
        sb.append("<title>");
        sb.append("HTTP Error Message");
        sb.append("</title>");
        sb.append(createCssContentString());
        sb.append("</head>");

        sb.append("<body>");
        sb.append("<div class=\"wrapper\"></div>");
        sb.append("<div id=\"top\"></div>");
        sb.append("<div id=\"top-middle\"></div>");
        sb.append("<div class=\"container\">");
        sb.append("<h1>");
        sb.append(errorHeader);
        sb.append("</h1>");
        sb.append("<span class=\"error\">");
        sb.append("Error: " + errorMessage);
        sb.append("</span>");
        sb.append("</div>");
        sb.append("<div id=\"bottom-middle\"></div>");
        sb.append("<div id=\"bottom\"></div>");
        sb.append("</div>");
        sb.append("</body>");

        sb.append("</html>");

        try {
            PrintWriter out = response.getWriter();
            out.println(sb.toString());
            out.flush();
        } catch (IOException e) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "Failed to obtain writer for response to write exception: " + e);
            }
        }
    }

    /**
     * Creates the CSS content string to be used to format page.
     *
     * @return
     */
    protected String createCssContentString() {
        StringBuilder css = new StringBuilder();
        css.append("<style>");

        // body
        css.append("body {");
        css.append("background-color: #001e3d;");
        css.append("font-family: serif;");
        css.append("margin: 0;");
        css.append("}\n");
        // #top, #bottom
        css.append("#top, #bottom {");
        css.append("padding: 20px;");
        css.append("}\n");
        css.append("");
        // #top-middle, #bottom-middle
        css.append("#top-middle, #bottom-middle {");
        css.append("background-color: #001428;");
        css.append("padding: 10px;");
        css.append("}\n");
        css.append("");
        // .container
        css.append(".container {");
        css.append("background-color: white;");
        css.append("padding: 20px 50px;");
        css.append("}\n");
        // .error
        css.append(".error {");
        css.append("color: red;");
        css.append("font-weight: bold;");
        css.append("}\n");

        css.append("</style>");
        return css.toString();
    }
}
