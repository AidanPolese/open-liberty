//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2008
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//@(#) 1.2 SERV1/ws/code/http.channel.impl/src/com/ibm/ws/http/channel/values/impl/AccessLogCurrentTime.java, WAS.channel.http, WASX.SERV1, ff1301.02 9/22/11 11:38:34 [1/15/13 18:11:12]
//Change History:
//Date     UserId      Defect          Description
//--------------------------------------------------------------------------------
//03022015      mansal          PI36010                 This file added to CMVC

package com.ibm.ws.http.channel.internal.values;

import java.util.concurrent.TimeUnit;

import com.ibm.ws.http.channel.internal.HttpRequestMessageImpl;
import com.ibm.wsspi.http.channel.HttpRequestMessage;
import com.ibm.wsspi.http.channel.HttpResponseMessage;

public class AccessLogElapsedRequestTime extends AccessLogData {

    public AccessLogElapsedRequestTime() {
        super("%{R}W");
        // %{R}W - Elapsed time - in nanoseconds - of the request.
    }

    @Override
    public Object init(String rawToken) {
        // The only token supported is t, i.e. %{R}W
        if ("R".equals(rawToken)) {
            return null;
        }
        return rawToken;
    }

    @Override
    public boolean set(StringBuilder accessLogEntry,
                       HttpResponseMessage response, HttpRequestMessage request,
                       Object data) {

        long elapsedRequestTime = getElapsedRequestTime(response, request, data);

        if (elapsedRequestTime > 0) {
            accessLogEntry.append(elapsedRequestTime);

        } else {
            accessLogEntry.append("-");
        }

        return true;
    }

    public static long getElapsedRequestTime(HttpResponseMessage response, HttpRequestMessage request, Object data) {
        HttpRequestMessageImpl requestMessageImpl = null;
        long startTime = 0;
        long endTime = 0;
        if (request != null) {
            requestMessageImpl = (HttpRequestMessageImpl) request;
        }

        if (requestMessageImpl != null) {
            startTime = requestMessageImpl.getStartTime();
            endTime = requestMessageImpl.getServiceContext().getResponseStartTime();

        }

        if (startTime != 0 && endTime >= startTime) {
            long elapsedTime = endTime - startTime;
            return TimeUnit.NANOSECONDS.toMicros(elapsedTime);
        } else {
            return -1;
        }
    }

}
