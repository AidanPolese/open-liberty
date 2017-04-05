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
package com.ibm.ws.jaxrs20.injection;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;

import javax.ws.rs.BeanParam;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxrs20.injection.metadata.ParamInjectionMetadata;
import com.ibm.wsspi.injectionengine.ComponentNameSpaceConfiguration;

public class BeanParamInjectionBinding extends AbstractParamInjectionBinding<BeanParam> {

    private static final TraceComponent tc = Tr.register(BeanParamInjectionBinding.class);

    /**
     * @param annotation
     * @param nameSpaceConfig
     */
    public BeanParamInjectionBinding(BeanParam annotation, ComponentNameSpaceConfiguration nameSpaceConfig) {
        super(annotation, nameSpaceConfig);

    }

    @Override
    protected Object getJAXRSValueFromContext(BeanParam annotation, Class<?> classType, Type genericType, Annotation[] memberAnnotations, Member member,
                                              ParamInjectionMetadata paramInjectionMetadata) throws IOException {
        if (tc.isEntryEnabled()) {
            Tr.entry(tc, "getJAXRSValueFromContext", new Object[] { annotation, classType,
                                                                   genericType, memberAnnotations, member, paramInjectionMetadata });
        }

        Object value = getInjectedObjectFromCXF(classType, genericType, memberAnnotations, paramInjectionMetadata);

        if (tc.isEntryEnabled()) {
            Tr.exit(tc, "getJAXRSValueFromContext", new Object[] { value });
        }

        return value;
    }

}
