//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2008
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//@(#) 1.2 SERV1/ws/code/http.channel.impl/src/com/ibm/ws/http/channel/values/impl/AccessLogStartTime.java, WAS.channel.http, WASX.SERV1, ff1301.02 9/22/11 11:38:37 [1/15/13 18:11:13]
//Change History:
//Date     UserId      Defect          Description
//--------------------------------------------------------------------------------
//08262011	cmmeyer		F009742			This file added to CMVC

package com.ibm.ws.http.channel.internal.values;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.ibm.ws.http.channel.internal.HttpRequestMessageImpl;
import com.ibm.ws.http.dispatcher.internal.HttpDispatcher;
import com.ibm.wsspi.http.channel.HttpRequestMessage;
import com.ibm.wsspi.http.channel.HttpResponseMessage;

public class AccessLogStartTime extends AccessLogData {

    public AccessLogStartTime() {
        super("%t");
        // %T - Note the start time for the request
    }

    @Override
    public boolean set(StringBuilder accessLogEntry,
                       HttpResponseMessage response, HttpRequestMessage request,
                       Object data) {
        long startTime = getStartTime(response, request, data);

        if (startTime != 0) {
            Date startDate = new Date(startTime);
            accessLogEntry.append("[");
            accessLogEntry.append(HttpDispatcher.getDateFormatter().getNCSATime(startDate));
            accessLogEntry.append("]");
        } else {
            accessLogEntry.append("-");
        }

        return true;
    }

    public static long getStartTime(HttpResponseMessage response, HttpRequestMessage request, Object data) {
        HttpRequestMessageImpl requestMessageImpl = null;
        long startTime = 0;
        if (request != null) {
            requestMessageImpl = (HttpRequestMessageImpl) request;
        }

        if (requestMessageImpl != null) {
            long elapsedTime = System.nanoTime() - requestMessageImpl.getStartNanoTime();
            startTime = System.currentTimeMillis() - TimeUnit.NANOSECONDS.toMillis(elapsedTime);
        }
        return startTime;
    }

}
