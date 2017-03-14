//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2008
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//@(#) 1.2 SERV1/ws/code/http.channel.impl/src/com/ibm/ws/http/channel/values/impl/AccessLogRequestCookie.java, WAS.channel.http, WASX.SERV1, ff1301.02 9/16/11 14:27:34 [1/15/13 18:11:13]
//Change History:
//Date     UserId      Defect          Description
//--------------------------------------------------------------------------------
//08262011	cmmeyer		F009742			This file added to CMVC
//09162011	cmmeyer		F009742			Changed the processing of the cookies to properly account for no cookies

package com.ibm.ws.http.channel.internal.values;

import java.util.Iterator;
import java.util.List;

import com.ibm.wsspi.http.HttpCookie;
import com.ibm.wsspi.http.channel.HttpRequestMessage;
import com.ibm.wsspi.http.channel.HttpResponseMessage;

public class AccessLogRequestCookie extends AccessLogData {

    public AccessLogRequestCookie() {
        super("%C");
        //%{CookieName}C
        // The contents of cookie CookieName in the request sent to the server.
    }

    @Override
    public Object init(String rawToken) {
        if (rawToken != null && rawToken.length() == 0) {
            return null;
        }

        return rawToken;
    }

    @Override
    public boolean set(StringBuilder accessLogEntry,
                       HttpResponseMessage response, HttpRequestMessage request, Object data) {

        String cookieName = (String) data;
        HttpCookie headerCookie = null;
        List<HttpCookie> cookieValues = null;

        if (cookieName != null) {
            headerCookie = request.getCookie(cookieName);
        } else {
            cookieValues = request.getAllCookies();
        }

        if (headerCookie != null) {
            accessLogEntry.append(headerCookie.getName());
            accessLogEntry.append(":");
            accessLogEntry.append(headerCookie.getValue());
        } else if (cookieValues != null && !cookieValues.isEmpty()) {
            Iterator<HttpCookie> iter = cookieValues.iterator();
            while (iter.hasNext()) {
                HttpCookie token = iter.next();
                accessLogEntry.append(token.getName());
                accessLogEntry.append(":");
                accessLogEntry.append(token.getValue());
                if (iter.hasNext()) {
                    accessLogEntry.append(" ");
                }
            }
        } else {
            accessLogEntry.append("-");
        }

        return true;
    }

}
