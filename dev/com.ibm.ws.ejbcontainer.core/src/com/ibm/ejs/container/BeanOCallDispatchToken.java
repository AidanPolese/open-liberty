/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

import com.ibm.ws.csi.DispatchEventListenerCookie;
import com.ibm.ws.ejbcontainer.EJBMethodMetaData;

public class BeanOCallDispatchToken extends Object {

    private DispatchEventListenerCookie[] dispatchEventListenerCookies = null;
    private EJBMethodMetaData methodMetaData = null;
    private boolean doAfterDispatch = false;

    /** Creates new BeanOCallDispatchToken */
    public BeanOCallDispatchToken() {}

    public void setDispatchEventListenerCookies(DispatchEventListenerCookie[] c) {
        dispatchEventListenerCookies = c;
    }

    public DispatchEventListenerCookie[] getDispatchEventListenerCookies() {
        return dispatchEventListenerCookies;
    }

    public void setMethodMetaData(EJBMethodMetaData m) {
        methodMetaData = m;
    }

    public EJBMethodMetaData getMethodMetaData() {
        return methodMetaData;
    }

    public void setDoAfterDispatch(boolean v) {
        doAfterDispatch = v;
    }

    public boolean getDoAfterDispatch() {
        return doAfterDispatch;
    }

}
