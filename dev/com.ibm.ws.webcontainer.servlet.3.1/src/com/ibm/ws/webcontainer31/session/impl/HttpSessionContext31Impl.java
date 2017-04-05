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
package com.ibm.ws.webcontainer31.session.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ibm.ws.session.SessionApplicationParameters;
import com.ibm.ws.session.SessionData;
import com.ibm.ws.session.SessionManagerConfig;
import com.ibm.ws.session.SessionStoreService;
import com.ibm.ws.webcontainer.session.impl.HttpSessionContextImpl;
import com.ibm.ws.webcontainer31.session.IHttpSessionContext31;
import com.ibm.wsspi.session.SessionAffinityContext;


/**
 * HttpSessionContextImpl specific to Servlet 3.1
 */
public class HttpSessionContext31Impl extends HttpSessionContextImpl implements IHttpSessionContext31 {

    /**
     * @param smc
     * @param sap
     * @param sessionStoreService
     */
    public HttpSessionContext31Impl(SessionManagerConfig smc, SessionApplicationParameters sap, SessionStoreService sessionStoreService) {
        super(smc, sap, sessionStoreService, true);
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.webcontainer31.session.IHttpSessionContext31#generateNewId(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession)
     */
    @Override
    public HttpSession generateNewId(HttpServletRequest request, HttpServletResponse response, HttpSession existingSession) {
        SessionAffinityContext sac = getSessionAffinityContext(request);
        HttpSession session = (HttpSession) _coreHttpSessionManager.generateNewId(request, response, sac, ((SessionData) existingSession).getISession());
        return session;
    }
    

}
