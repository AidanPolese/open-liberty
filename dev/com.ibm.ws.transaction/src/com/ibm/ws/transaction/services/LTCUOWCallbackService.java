/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.transaction.services;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.ibm.tx.ltc.embeddable.impl.EmbeddableLTCUOWCallback;
import com.ibm.ws.uow.UOWScope;
import com.ibm.ws.uow.UOWScopeCallback;

/**
 * This service provides access to a UOWScopeCallback implementation
 */
@Component
public class LTCUOWCallbackService implements UOWScopeCallback {

    private UOWScopeCallback callback;

    @Activate
    protected void activate(ComponentContext ctxt) {
        //Get the instance
        callback = EmbeddableLTCUOWCallback.getUserTransactionCallback();
    }

    @Deactivate
    protected void deactivate(ComponentContext ctxt) {
        callback = null;
    }

    @Override
    public void contextChange(int typeOfChange, UOWScope scope) throws IllegalStateException {
        if (callback != null) {
            callback.contextChange(typeOfChange, scope);
        }
    }
}
