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
package com.ibm.ws.ejbcontainer.async.osgi.internal;

import com.ibm.ejs.container.AsyncMethodWrapper;
import com.ibm.ejs.container.EJSWrapperBase;

public class AsyncMethodWrapperImpl extends AsyncMethodWrapper {
    private final ServerAsyncResultImpl serverFuture;

    public AsyncMethodWrapperImpl(EJSWrapperBase theCallingWrapper,
                                  int theMethodId, Object[] theMethodArgs,
                                  ServerAsyncResultImpl theServerFuture) {
        super(theCallingWrapper, theMethodId, theMethodArgs, theServerFuture);
        serverFuture = theServerFuture;
    }

    @Override
    public void run() {
        // If another thread has cancelled the task before we started running,
        // then just do nothing.
        if (serverFuture == null || serverFuture.runOrCancel()) {
            super.run();
        }
    }
}