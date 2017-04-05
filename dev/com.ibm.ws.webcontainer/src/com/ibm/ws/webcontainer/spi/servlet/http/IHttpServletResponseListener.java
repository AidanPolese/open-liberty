// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer.spi.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface IHttpServletResponseListener {

    /**
     * Called prior to writing the response headers out to the
     * client when processing and HttpServletResponse.
     * @param response the response object
     */
    public void preHeaderCommit(HttpServletRequest request, HttpServletResponse response);
}
