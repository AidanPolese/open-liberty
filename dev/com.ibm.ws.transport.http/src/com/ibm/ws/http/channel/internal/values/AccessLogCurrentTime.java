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
//08262011	cmmeyer		F009742			This file added to CMVC
//03022015      mansal          PI36010                 Modified directive name from %U to %{t}W as done on tWAS

package com.ibm.ws.http.channel.internal.values;

import java.util.Date;

import com.ibm.ws.http.dispatcher.internal.HttpDispatcher;
import com.ibm.wsspi.http.channel.HttpRequestMessage;
import com.ibm.wsspi.http.channel.HttpResponseMessage;

public class AccessLogCurrentTime extends AccessLogData {

    public AccessLogCurrentTime() {
        super("%{t}W");
        // %{format}t
        // The time, in the form given by format,
        // which should be in strftime(3) format. (potentially localized)
    }

    @Override
    public Object init(String rawToken) {
        // The only token supported is t, i.e. %{t}W
        if ("t".equals(rawToken)) {
            return null;
        }
        return rawToken;
    }

    @Override
    public boolean set(StringBuilder accessLogEntry,
                       HttpResponseMessage response, HttpRequestMessage request, Object data) {

        if (data == null) {
            accessLogEntry.append("[");
            accessLogEntry.append(HttpDispatcher.getDateFormatter().getNCSATime(new Date(System.currentTimeMillis())));
            accessLogEntry.append("]");
        } else {
            // just print out what was there
            accessLogEntry.append("%{").append(data).append("}W");
        }

        return true;
    }

}
