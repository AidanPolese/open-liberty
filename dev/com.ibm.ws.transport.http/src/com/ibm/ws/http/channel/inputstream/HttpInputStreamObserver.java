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

/**
 * The need of these APIs from channel to webcontainer to support Multiread
 *
 */
public interface HttpInputStreamObserver {

    /*
     * Indicates that the input stream, obtained using either getInputStream
     * or getReader has been closed.
     */
    public void alertISOpen();

    /*
     * Indicates that the input stream, obtained using either getInputStream
     * or getReader has been closed.
     */
    public void alertISClose();

}
