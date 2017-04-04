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

import java.util.ArrayList;

import com.ibm.ws.session.SessionApplicationParameters;
import com.ibm.ws.session.SessionContext;
import com.ibm.ws.session.SessionManagerConfig;
import com.ibm.ws.webcontainer.httpsession.SessionManager;
import com.ibm.ws.webcontainer.session.IHttpSessionContext;
import com.ibm.ws.webcontainer.session.impl.SessionContextRegistryImpl;

/**
 *
 */
public class SessionContextRegistry31Impl extends SessionContextRegistryImpl {
    
    /**
     * @param smgr
     */
    public SessionContextRegistry31Impl(SessionManager smgr) {
        super(smgr);
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.ws.webcontainer.session.impl.SessionContextRegistryImpl#createSessionContextObject(com.ibm.ws.session.SessionManagerConfig, com.ibm.ws.session.SessionApplicationParameters)
     */
    protected IHttpSessionContext createSessionContextObject(SessionManagerConfig smc, SessionApplicationParameters sap)
    {
        return new HttpSessionContext31Impl(smc, sap, this.smgr.getSessionStoreService());
    } 
    
    /*
     * (non-Javadoc)
     * @see com.ibm.ws.webcontainer.session.impl.SessionContextRegistryImpl#addHttpSessionIdListeners(java.util.ArrayList, java.lang.String)
     */
    @Override
    protected void addHttpSessionIdListeners(ArrayList sessionIdListeners, String j2eeName, SessionContext sessCtx) {
        sessCtx.addHttpSessionIdListener(sessionIdListeners, j2eeName);
    }

}
