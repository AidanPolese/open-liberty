/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.http.channel.inputstream;

import com.ibm.wsspi.http.HttpInputStream;

/**
 *
 * The need of these APIs is to support Multiread
 *
 */
public abstract class HttpInputStreamConnectWeb extends HttpInputStream {

    public abstract void setISObserver(HttpInputStreamObserver obs);

    public abstract void restart();

    public abstract void setupforMultiRead(boolean set);

    public abstract void if_enableMultiReadofPostData_set(boolean set);

    public abstract void cleanupforMultiRead();

}
