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

import javax.ws.rs.MatrixParam;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxrs20.injection.MatrixParamProcessor;
import com.ibm.wsspi.injectionengine.InjectionSimpleProcessor;
import com.ibm.wsspi.injectionengine.InjectionSimpleProcessorProvider;

@Component(name = "com.ibm.ws.jaxrs20.component.injection.MatrixParamProcessorProvider", service = com.ibm.wsspi.injectionengine.InjectionProcessorProvider.class,
           property = { "service.vendor=IBM" })
public class MatrixParamProcessorProvider extends InjectionSimpleProcessorProvider<MatrixParam> {

    final private static TraceComponent tc = Tr.register(MatrixParamProcessorProvider.class);

    private Dictionary<String, Object> props = null;

    @Override
    /** {@inheritDoc} */
    public Class<MatrixParam> getAnnotationClass() {
        return MatrixParam.class;
    }

    @Override
    /** {@inheritDoc} */
    public InjectionSimpleProcessor<MatrixParam> createInjectionProcessor() {
        return new MatrixParamProcessor();
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
