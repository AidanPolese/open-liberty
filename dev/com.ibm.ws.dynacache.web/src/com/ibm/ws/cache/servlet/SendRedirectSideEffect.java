// 1.4, 2/10/05
//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
package com.ibm.ws.cache.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class SendRedirectSideEffect implements ResponseSideEffect
{
    private static final long serialVersionUID = -5172434918005316276L;
    private String url = null;

    public String toString() {
       StringBuffer sb = new StringBuffer("SendRedirect side effect:\n\t");
       sb.append("url: ").append(url).append("\n\t");
       return sb.toString();
    }

    public SendRedirectSideEffect(String url) {
       this.url = url;
    }

    public void performSideEffect(HttpServletResponse response) {
       try {
          response.sendRedirect(url);
       } catch (IOException e) {
         com.ibm.ws.ffdc.FFDCFilter.processException(e, "com.ibm.ws.cache.servlet.SendRedirectSideEffect.performSideEffect", "47", this);
          throw new IllegalStateException(e.getMessage());
       }
    }    
}
