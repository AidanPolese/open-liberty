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
package com.ibm.ws.jaxws.client.injection;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceRef;
import javax.xml.ws.WebServiceRefs;

import com.ibm.ws.javaee.dd.common.JNDIEnvironmentRef;
import com.ibm.ws.javaee.dd.common.wsclient.ServiceRef;
import com.ibm.wsspi.injectionengine.InjectionProcessor;
import com.ibm.wsspi.injectionengine.InjectionProcessorProvider;

public class WebServiceRefProcessorProvider extends InjectionProcessorProvider<WebServiceRef, WebServiceRefs> {
    private static final List<Class<? extends JNDIEnvironmentRef>> REF_CLASSES =
                    Collections.<Class<? extends JNDIEnvironmentRef>> singletonList(ServiceRef.class);

    /** {@inheritDoc} */
    @Override
    public InjectionProcessor<WebServiceRef, WebServiceRefs> createInjectionProcessor() {
        return new WebServiceRefProcessor();
    }

    /** {@inheritDoc} */
    @Override
    public Class<WebServiceRef> getAnnotationClass() {
        return WebServiceRef.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<WebServiceRefs> getAnnotationsClass() {
        return WebServiceRefs.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<? extends Annotation> getOverrideAnnotationClass() {
        return Resource.class;
    }

    /** {@inheritDoc} */
    @Override
    public List<Class<? extends JNDIEnvironmentRef>> getJNDIEnvironmentRefClasses() {
        return REF_CLASSES;
    }
}
