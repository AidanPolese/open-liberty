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

import java.lang.reflect.Member;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import javax.ws.rs.PathParam;

import org.osgi.service.component.ComponentContext;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.injectionengine.InjectionBinding;
import com.ibm.wsspi.injectionengine.InjectionException;
import com.ibm.wsspi.injectionengine.InjectionSimpleProcessor;

public class PathParamProcessor extends InjectionSimpleProcessor<PathParam> {

    private static final TraceComponent tc = Tr.register(PathParamProcessor.class);

    private Dictionary<String, Object> props = null;

    public PathParamProcessor() {
        super(PathParam.class);
    }

    @Override
    public InjectionBinding<PathParam> createInjectionBinding(PathParam annotation, Class<?> instanceClass, Member member)
                    throws InjectionException {
        final String methodName = "createInjectionBinding";
        if (tc.isEntryEnabled()) {
            Tr.entry(tc, methodName, new Object[] { annotation, instanceClass, member });
        }

        PathParamInjectionBinding binding =
                        new PathParamInjectionBinding(annotation, ivNameSpaceConfig);

        if (tc.isEntryEnabled()) {
            Tr.exit(tc, methodName, binding);
        }
        return binding;
    }

    /*
     * Called by DS to activate service
     */
    @SuppressWarnings("unchecked")
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
