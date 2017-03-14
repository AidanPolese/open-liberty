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

/**
 * The Access log forwarder is invoked after each http request.
 */
public interface AccessLogForwarder {
    public void process(AccessLogRecordData logData);
}
