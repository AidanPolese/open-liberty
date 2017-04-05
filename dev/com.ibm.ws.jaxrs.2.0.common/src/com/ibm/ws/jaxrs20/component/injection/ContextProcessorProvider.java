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

package com.ibm.ws.jaxrs20.component.injection;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import javax.ws.rs.core.Context;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxrs20.injection.ContextProcessor;
import com.ibm.wsspi.injectionengine.InjectionSimpleProcessor;
import com.ibm.wsspi.injectionengine.InjectionSimpleProcessorProvider;

@Component(name = "com.ibm.ws.jaxrs20.component.injection.ContextProcessorProvider", service = com.ibm.wsspi.injectionengine.InjectionProcessorProvider.class,
           property = { "service.vendor=IBM" })
public class ContextProcessorProvider extends InjectionSimpleProcessorProvider<Context> {

    final private static TraceComponent tc = Tr.register(ContextProcessorProvider.class);

    private Dictionary<String, Object> props = null;

    /*
     * Called by DS to activate service
     */
    @SuppressWarnings("unchecked")
    @Activate
    protected void activate(ComponentContext cc) {
        props = cc.getProperties();
    }

    /*
     * Called by DS to modify service config properties
     */
    @SuppressWarnings("unchecked")
    @Modified
    protected void modified(Map<?, ?> newProperties) {
        if (newProperties instanceof Dictionary) {
            props = (Dictionary<String, Object>) newProperties;
        } else {
            props = new Hashtable(newProperties);
        }
    }

    @Override
    /** {@inheritDoc} */
    public Class<Context> getAnnotationClass() {
        return Context.class;
    }

    @Override
    /** {@inheritDoc} */
    public InjectionSimpleProcessor<Context> createInjectionProcessor() {
        return new ContextProcessor();
        //return processor;
        // if nothing is calling this class' ctor, then this will return null
        // TODO: Need to find out if injection engine is driving calls to the processor 
        // type's ctor
    }

}
