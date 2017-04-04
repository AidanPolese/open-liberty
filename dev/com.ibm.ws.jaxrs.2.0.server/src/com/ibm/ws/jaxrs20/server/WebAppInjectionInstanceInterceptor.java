/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.server;

import java.util.concurrent.ConcurrentHashMap;

import com.ibm.ws.jaxrs20.support.JaxRsInstanceManager.InstanceInterceptor;
import com.ibm.ws.jaxrs20.support.JaxRsInstanceManager.InterceptException;
import com.ibm.ws.jaxrs20.support.JaxRsInstanceManager.InterceptorContext;
import com.ibm.ws.managedobject.ManagedObject;
import com.ibm.wsspi.webcontainer.annotation.AnnotationHelper;
import com.ibm.wsspi.webcontainer.annotation.AnnotationHelperManager;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

/**
 * 
 */
public class WebAppInjectionInstanceInterceptor implements InstanceInterceptor {

    private final IServletContext servletContext;
    private final ConcurrentHashMap<Object, ManagedObject> managedObjects = new ConcurrentHashMap<Object, ManagedObject>();

    public WebAppInjectionInstanceInterceptor(IServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void postNewInstance(InterceptorContext ctx) throws InterceptException {
        AnnotationHelperManager ahm = AnnotationHelperManager.getInstance(servletContext);
        if (ahm == null) {
            ahm = new AnnotationHelperManager(servletContext);
            com.ibm.wsspi.webcontainer.annotation.AnnotationHelperManager.addInstance(servletContext, ahm);
        }
        AnnotationHelper ah = ahm.getAnnotationHelper();

        Object instanceObject = ctx.getInstance();
        ManagedObject mo = ah.inject(instanceObject);
        managedObjects.put(instanceObject, mo);
    }

    @Override
    public void preDestroyInstance(InterceptorContext ctx) throws InterceptException {
        AnnotationHelperManager ahm = AnnotationHelperManager.getInstance(servletContext);
        if (ahm == null) {
            ahm = new AnnotationHelperManager(servletContext);
            com.ibm.wsspi.webcontainer.annotation.AnnotationHelperManager.addInstance(servletContext, ahm);
        }
        AnnotationHelper ah = ahm.getAnnotationHelper();
        Object instanceObject = ctx.getInstance();
        ah.doPreDestroy(instanceObject);
        ManagedObject mo = managedObjects.remove(instanceObject);
        if (null != mo)
            mo.release();
    }

    @Override
    public void postInjectInstance(InterceptorContext ctx) {}
}
