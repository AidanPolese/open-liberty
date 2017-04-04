/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2014
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.ws.webcontainer.osgi.request;

import com.ibm.websphere.servlet.request.IRequest;
import com.ibm.wsspi.http.HttpInboundConnection;

/**
 *
 */
public interface IRequestFactory {

    IRequest createRequest(HttpInboundConnection inboundConnection);
}
