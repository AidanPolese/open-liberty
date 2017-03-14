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

/**
 * Observes the events of an HttpOutputStream.
 */
public interface HttpOutputStreamObserver {

    /**
     * Notification that the OutputStream has been flushed to for the first time.
     */
    void alertOSFirstFlush();

}
