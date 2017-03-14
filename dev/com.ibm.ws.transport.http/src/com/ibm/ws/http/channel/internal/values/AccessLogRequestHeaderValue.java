//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2008
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//@(#) 1.2 SERV1/ws/code/http.channel.impl/src/com/ibm/ws/http/channel/values/impl/AccessLogRequestHeaderValue.java, WAS.channel.http, WASX.SERV1, ff1301.02 9/20/11 15:47:01 [1/15/13 18:11:13]
//Change History:
//Date     UserId      Defect          Description
//--------------------------------------------------------------------------------
//08262011	cmmeyer		F009742			This file added to CMVC

package com.ibm.ws.http.channel.internal.values;

import com.ibm.wsspi.http.channel.HttpRequestMessage;
import com.ibm.wsspi.http.channel.HttpResponseMessage;

public class AccessLogRequestHeaderValue extends AccessLogData {

    public AccessLogRequestHeaderValue() {
        super("%i");
        //%{HeaderName}i
        // The contents of HeaderLine: header line(s) in the request sent to the server.
    }

    @Override
    public Object init(String rawToken) {
        // return the what is the header name
        if (rawToken != null && rawToken.length() == 0) {
            return null;
        }
        return rawToken;
    }

    @Override
    public boolean set(StringBuilder accessLogEntry,
                       HttpResponseMessage response, HttpRequestMessage request, Object data) {
        String headerName = (String) data;
        String headerValue = null;

        if (headerName != null) {
            headerValue = request.getHeader(headerName).asString();
        }

        if (headerValue != null) {
            accessLogEntry.append(headerValue);
        } else {
            accessLogEntry.append("-");
        }

        return true;
    }

}
