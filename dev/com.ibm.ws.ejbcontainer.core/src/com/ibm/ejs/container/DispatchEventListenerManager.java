/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010, 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

import com.ibm.ws.csi.DispatchEventListenerCookie;
import com.ibm.ws.ejbcontainer.EJBMethodMetaData;

public interface DispatchEventListenerManager
{
    boolean dispatchEventListenersAreActive();

    DispatchEventListenerCookie[] getNewDispatchEventListenerCookieArray();

    void callDispatchEventListeners(int dispatchEventCode, DispatchEventListenerCookie[] cookies, EJBMethodMetaData methodInfo);
}
