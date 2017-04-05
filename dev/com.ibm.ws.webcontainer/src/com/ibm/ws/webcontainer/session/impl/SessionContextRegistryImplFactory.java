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
package com.ibm.ws.webcontainer.session.impl;

/**
 *
 */
public interface SessionContextRegistryImplFactory {
    
    public SessionContextRegistryImpl createSessionContextRegistryImpl(com.ibm.ws.webcontainer.httpsession.SessionManager smgr);

}
