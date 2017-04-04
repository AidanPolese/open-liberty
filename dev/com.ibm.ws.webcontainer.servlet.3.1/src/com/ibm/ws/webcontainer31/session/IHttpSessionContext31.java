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
package com.ibm.ws.webcontainer31.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionIdListener;

import com.ibm.ws.webcontainer.session.IHttpSessionContext;

/**
 * IHttpSessionContext interface specific to Servlet 3.1
 */
public interface IHttpSessionContext31 extends IHttpSessionContext {

    void addHttpSessionIdListener(HttpSessionIdListener listener, String J2EEName);
    
    public HttpSession generateNewId(HttpServletRequest _request, HttpServletResponse _response, HttpSession existingSession);
}
