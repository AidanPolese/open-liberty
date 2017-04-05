/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2015
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.ws.jsp.webcontainerext.ws;

import javax.servlet.http.HttpServletRequest;
/**
 *
 */
public interface PrepareJspServletRequest {
    
    public void setQueryString(String string);

    public void setRequestURI(String string);

    public void setServletPath(String string);
    
    public HttpServletRequest getHttpServletRequest();

}
