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
package com.ibm.wsspi.http.logging;

import com.ibm.wsspi.http.channel.HttpRequestMessage;
import com.ibm.wsspi.http.channel.HttpResponseMessage;

public interface AccessLogRecordData {

    HttpRequestMessage getRequest();

    HttpResponseMessage getResponse();

    long getTimestamp();

    String getVersion();

    String getUserId();

    String getRemoteAddress();

    long getBytesWritten();

    long getStartTime();

    long getElapsedTime();

    String getLocalIP();

    String getLocalPort();
}
