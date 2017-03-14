//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2008
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//@(#) 1.1 SERV1/ws/code/http.channel.impl/src/com/ibm/ws/http/channel/values/impl/AccessLogRemoteIP.java, WAS.channel.http, WASX.SERV1, ff1301.02 9/1/11 10:20:36 [1/15/13 18:11:13]
//Change History:
//Date     UserId      Defect          Description
//--------------------------------------------------------------------------------
//08262011	cmmeyer		F009742			This file added to CMVC

package com.ibm.ws.http.channel.internal.values;

import com.ibm.ws.http.channel.internal.HttpRequestMessageImpl;
import com.ibm.wsspi.http.channel.HttpRequestMessage;
import com.ibm.wsspi.http.channel.HttpResponseMessage;

public class AccessLogRemoteIP extends AccessLogData {

    public AccessLogRemoteIP() {
        super("%a");
        // %a
        // Remote IP-address
    }

    @Override
    public boolean set(StringBuilder accessLogEntry,
                       HttpResponseMessage response, HttpRequestMessage request,
                       Object data) {
        HttpRequestMessageImpl requestMessageImpl = null;
        String hostIPAddress = null;
        if (request != null) {
            requestMessageImpl = (HttpRequestMessageImpl) request;
        }

        if (requestMessageImpl != null) {
            hostIPAddress = requestMessageImpl.getServiceContext().getRemoteAddr().toString();
            hostIPAddress = hostIPAddress.substring(hostIPAddress.indexOf('/') + 1);
        }

        if (hostIPAddress != null) {
            accessLogEntry.append(hostIPAddress);
        } else {
            accessLogEntry.append("-");
        }
        return true;
    }
}
