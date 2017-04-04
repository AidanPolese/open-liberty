/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.wsoc.injection;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import com.ibm.ws.runtime.metadata.ComponentMetaData;

/**
 *
 */
public interface InjectionProvider {

    public <T> T getManagedEndpointInstance(Class<T> endpointClass, ConcurrentHashMap map) throws InstantiationException;

    public void releaseCC(Object key, ConcurrentHashMap map);

    public boolean activateAppContext(ComponentMetaData cmd);

    public boolean deActivateAppContext();

    public void startSesContext(HttpSession httpSession);

    public boolean deActivateSesContext();

}
