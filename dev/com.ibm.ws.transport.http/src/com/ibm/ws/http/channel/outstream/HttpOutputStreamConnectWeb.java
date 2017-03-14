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
package com.ibm.ws.http.channel.outstream;

import com.ibm.wsspi.http.HttpOutputStream;

/**
 *
 */
public abstract class HttpOutputStreamConnectWeb extends HttpOutputStream {

    public abstract void setObserver(HttpOutputStreamObserver obs);

    public abstract void setWebC_headersWritten(boolean headersWritten);

}
