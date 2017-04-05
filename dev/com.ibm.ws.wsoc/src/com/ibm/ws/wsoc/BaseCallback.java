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
package com.ibm.ws.wsoc;

import com.ibm.ws.wsoc.injection.InjectionThings;

/**
 * common methods both the Read and Write Callback can use
 */
public class BaseCallback {

    WsocConnLink connLink = null;

    InjectionThings it = null;

    public void setConnLinkCallback(WsocConnLink _link) {
        connLink = _link;
    }

    protected ClassLoader pushContexts() {

        it = connLink.pushContexts();

        return it.getOriginalCL();
    }

    protected void popContexts(ClassLoader originalCL) {

        connLink.popContexts(it);
    }
}
