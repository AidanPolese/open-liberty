/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.injectionengine.processor;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.Resources;

import com.ibm.ws.javaee.dd.common.EnvEntry;
import com.ibm.ws.javaee.dd.common.JNDIEnvironmentRef;
import com.ibm.ws.javaee.dd.common.MessageDestinationRef;
import com.ibm.ws.javaee.dd.common.ResourceEnvRef;
import com.ibm.ws.javaee.dd.common.ResourceRef;
import com.ibm.wsspi.injectionengine.InjectionProcessor;
import com.ibm.wsspi.injectionengine.InjectionProcessorProvider;

public class ResourceProcessorProvider
                extends InjectionProcessorProvider<Resource, Resources>
{
    @SuppressWarnings("unchecked")
    List<Class<? extends JNDIEnvironmentRef>> REF_CLASSES = Arrays.<Class<? extends JNDIEnvironmentRef>> asList
                    (EnvEntry.class,
                     ResourceRef.class,
                     ResourceEnvRef.class,
                     MessageDestinationRef.class);

    @Override
    public Class<Resource> getAnnotationClass()
    {
        return Resource.class;
    }

    @Override
    public Class<Resources> getAnnotationsClass()
    {
        return Resources.class;
    }

    public List<Class<? extends JNDIEnvironmentRef>> getJNDIEnvironmentRefClasses()
    {
        return REF_CLASSES;
    }

    @Override
    public InjectionProcessor<Resource, Resources> createInjectionProcessor()
    {
        return new ResourceProcessor();
    }
}
