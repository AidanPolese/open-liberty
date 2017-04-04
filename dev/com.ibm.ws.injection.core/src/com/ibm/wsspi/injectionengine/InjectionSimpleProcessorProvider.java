/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.injectionengine;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resources;

import com.ibm.ws.javaee.dd.common.JNDIEnvironmentRef;

/**
 * A provider for simple injection processors.
 */
public abstract class InjectionSimpleProcessorProvider<A extends Annotation>
                extends InjectionProcessorProvider<A, Resources>
{
    @Override
    public final Class<Resources> getAnnotationsClass()
    {
        return null;
    }

    @Override
    public final Class<? extends Annotation> getOverrideAnnotationClass()
    {
        return null;
    }

    @Override
    public final List<Class<? extends JNDIEnvironmentRef>> getJNDIEnvironmentRefClasses()
    {
        return Collections.emptyList();
    }

    /**
     * Creates a new processor instance. The annotation class passed to the
     * InjectionSimpleProcessor constructor must match the value returned from {@link #getAnnotationClass}.
     */
    @Override
    public abstract InjectionSimpleProcessor<A> createInjectionProcessor();
}
