/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.clientcontainer.internal;

import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.security.auth.callback.CallbackHandler;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.clientcontainer.metadata.CallbackHandlerProvider;
import com.ibm.ws.container.service.app.deploy.ModuleInfo;

/**
 * An implementation of CallbackHandlerProvider.
 */
public class CallbackHandlerProviderImpl implements CallbackHandlerProvider {
    private static final TraceComponent tc = Tr.register(CallbackHandlerProviderImpl.class, "clientContainer", "com.ibm.ws.clientcontainer.resources.Messages");
    final private CallbackHandler callbackHandler;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public CallbackHandlerProviderImpl(ModuleInfo moduleInfo, String callbackHandlerName) {
        if (callbackHandlerName == null) {
            callbackHandler = null;
            return;
        }
        Class<?> callbackHandlerClass = null;
        ClassLoader cl = moduleInfo.getClassLoader();
        try {
            callbackHandlerClass = cl.loadClass(callbackHandlerName);
            final java.lang.reflect.Constructor c = callbackHandlerClass.getDeclaredConstructor((Class<?>[]) null);
            AccessController.doPrivileged(new PrivilegedAction() {
                @Override
                public Object run() {
                    c.setAccessible(true);
                    return c;
                }
            });
            callbackHandler = (CallbackHandler) c.newInstance((Object[]) null);
        } catch (NoSuchMethodException nme) {
            Tr.error(tc, "MISSING_NOARGS_CONSTRUCTOR_CWWKC2451E");
            throw new IllegalArgumentException(nme);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallbackHandler getCallbackHandler() {
        return callbackHandler;
    }
}
