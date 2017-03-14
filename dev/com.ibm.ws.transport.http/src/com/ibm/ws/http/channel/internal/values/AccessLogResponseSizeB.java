//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2008
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//@(#) 1.1 SERV1/ws/code/http.channel.impl/src/com/ibm/ws/http/channel/values/impl/AccessLogResponseSizeB.java, WAS.channel.http, WASX.SERV1, ff1301.02 9/1/11 10:20:42 [1/15/13 18:11:13]
//Change History:
//Date     UserId      Defect          Description
//--------------------------------------------------------------------------------
//08262011	cmmeyer		F009742			This file added to CMVC

package com.ibm.ws.http.channel.internal.values;

import com.ibm.ws.http.channel.internal.HttpResponseMessageImpl;
import com.ibm.wsspi.genericbnf.HeaderStorage;
import com.ibm.wsspi.http.channel.HttpRequestMessage;
import com.ibm.wsspi.http.channel.HttpResponseMessage;

public class AccessLogResponseSizeB extends AccessLogData {

    public AccessLogResponseSizeB() {
        super("%B");
        // %B - Size of response in bytes, excluding HTTP headers.
    }

    @Override
    public boolean set(StringBuilder accessLogEntry,
                       HttpResponseMessage response, HttpRequestMessage request,
                       Object data) {

        long responseSize = -999;
        HttpResponseMessageImpl responseMessageImpl = null;
        if (response != null) {
            responseMessageImpl = (HttpResponseMessageImpl) response;
            if (HeaderStorage.NOTSET != response.getContentLength()) {
                responseSize = response.getContentLength();
            }
        }

        if (responseMessageImpl != null) {
            if (responseSize == -1) {
                responseSize = responseMessageImpl.getServiceContext().getNumBytesWritten();
            }
        }

        if (responseSize != -999) {
            accessLogEntry.append(responseSize);
        } else {
            accessLogEntry.append("0");
        }

        return true;
    }

}
