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
package com.ibm.ws.webcontainer31.session.impl.factory;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.webcontainer.httpsession.SessionManager;
import com.ibm.ws.webcontainer.session.impl.SessionContextRegistryImpl;
import com.ibm.ws.webcontainer.session.impl.SessionContextRegistryImplFactory;
import com.ibm.ws.webcontainer31.session.impl.SessionContextRegistry31Impl;

/**
 *
 */
@Component(service = SessionContextRegistryImplFactory.class, property = { "service.vendor=IBM" })
public class SessionContextRegistryImplFactoryImpl31 implements SessionContextRegistryImplFactory {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.webcontainer.session.impl.SessionContextRegistryImplFactory#createSessionContextRegistryImpl(com.ibm.ws.webcontainer.httpsession.SessionManager)
     */
    @Override
    public SessionContextRegistryImpl createSessionContextRegistryImpl(SessionManager smgr) {
        return new SessionContextRegistry31Impl(smgr);
    }

}
