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

package com.ibm.ws.jaxrs20.component.injection;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import javax.ws.rs.HeaderParam;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxrs20.injection.HeaderParamProcessor;
import com.ibm.wsspi.injectionengine.InjectionSimpleProcessor;
import com.ibm.wsspi.injectionengine.InjectionSimpleProcessorProvider;

@Component(name = "com.ibm.ws.jaxrs20.component.injection.HeaderParamProcessorProvider", service = com.ibm.wsspi.injectionengine.InjectionProcessorProvider.class,
           property = { "service.vendor=IBM" })
public class HeaderParamProcessorProvider extends InjectionSimpleProcessorProvider<HeaderParam> {

    final private static TraceComponent tc = Tr.register(HeaderParamProcessorProvider.class);

    private Dictionary<String, Object> props = null;

    @Override
    /** {@inheritDoc} */
    public Class<HeaderParam> getAnnotationClass() {
        return HeaderParam.class;
    }

    @Override
    /** {@inheritDoc} */
    public InjectionSimpleProcessor<HeaderParam> createInjectionProcessor() {
        return new HeaderParamProcessor();
    }

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
    protected void modified(Map<?, ?> newProperties) {
        if (newProperties instanceof Dictionary) {
            props = (Dictionary<String, Object>) newProperties;
        } else {
            props = new Hashtable(newProperties);
        }
    }
}
