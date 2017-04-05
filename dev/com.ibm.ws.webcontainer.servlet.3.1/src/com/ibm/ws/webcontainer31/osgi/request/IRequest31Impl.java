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
package com.ibm.ws.webcontainer31.osgi.request;

import com.ibm.websphere.servlet31.request.IRequest31;
import com.ibm.ws.webcontainer.osgi.request.IRequestImpl;
import com.ibm.wsspi.http.HttpInboundConnection;
import com.ibm.wsspi.http.ee7.HttpInputStreamEE7;


public class IRequest31Impl extends IRequestImpl implements IRequest31 {

    /**
     * @param connection
     */
    public IRequest31Impl(HttpInboundConnection connection) {
        super(connection);
    }
    
    /**
     * New API added in Servlet 3.1
     * @return
     */
    public long getContentLengthLong(){
      long rc = this.request.getContentLength();

      return rc;
    }
}
