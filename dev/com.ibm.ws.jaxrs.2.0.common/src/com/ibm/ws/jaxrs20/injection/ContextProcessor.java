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
package com.ibm.ws.jaxrs20.injection;

import static com.ibm.ws.jaxrs20.internal.JaxRsCommonConstants.TR_GROUP;
import static com.ibm.ws.jaxrs20.internal.JaxRsCommonConstants.TR_RESOURCE_BUNDLE;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Reference;
import javax.ws.rs.core.Context;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Modified;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxrs20.component.injection.ContextObjectFactory;
import com.ibm.wsspi.injectionengine.InjectionBinding;
import com.ibm.wsspi.injectionengine.InjectionException;
import com.ibm.wsspi.injectionengine.InjectionSimpleProcessor;

/**
 * InjectionProcess to handle Context annotation injection
 */
public class ContextProcessor extends InjectionSimpleProcessor<Context> {

    private static final TraceComponent tc = Tr.register(ContextProcessor.class, TR_GROUP, TR_RESOURCE_BUNDLE);

    public ContextProcessor() {
        super(Context.class);
    }

    private Dictionary<String, Object> props = null;

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.injectionengine.InjectionProcessor#resolve(com.ibm.wsspi.injectionengine.InjectionBinding)
     * 
     * This method sets the ContextObjectFactory which injects the proxies
     */
    @Override
    public void resolve(InjectionBinding<Context> binding)
                    throws InjectionException {
        final String methodName = "resolve";
        if (tc.isEntryEnabled()) {
            Tr.entry(tc, methodName, binding);
        }
        Class<?> injectionType = binding.getInjectionClassType();

        if (tc.isDebugEnabled()) {
            Tr.debug(tc,
                     "Setting the ContextObjectFactory on the ContextInjectionBinding for type {0}",
                     injectionType);
        }
        Reference ref = new Reference(injectionType.getName());
        binding.setReferenceObject(ref, ContextObjectFactory.class);

        if (tc.isEntryEnabled())
            Tr.exit(tc, methodName, binding);
    }

    /*
     * Called by DS to activate service
     */
//    @SuppressWarnings("unchecked")
    @Activate
    protected void activate(ComponentContext cc) {
        props = cc.getProperties();
    }

    /*
     * Called by DS to modify service config properties
     */
//    @SuppressWarnings("unchecked")
    @Modified
    protected void modified(Map<?, ?> newProperties) {
        if (newProperties instanceof Dictionary) {
            props = (Dictionary<String, Object>) newProperties;
        } else {
            props = new Hashtable(newProperties);
        }
    }

}
