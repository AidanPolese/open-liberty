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
import java.util.List;

import com.ibm.ws.javaee.dd.common.JNDIEnvironmentRef;

/**
 * A provider for injection processors.
 */
public abstract class InjectionProcessorProvider<A extends Annotation, AS extends Annotation>
{
    /**
     * The annotation class handled by the processor.
     */
    public abstract Class<A> getAnnotationClass();

    /**
     * The plural annotation class handled by the processor, or null if the
     * processor does not have a plural annotation class.
     */
    public abstract Class<AS> getAnnotationsClass();

    /**
     * The annotation class overridden by this processor. If this method
     * returns non-null, then the processor returned by {@link #createInjectionProcessor} must implement {@link OverrideInjectionProcessor}, and it must call
     * {@link InjectionProcessor#overrideProcessor} with this annotation.
     */
    public Class<? extends Annotation> getOverrideAnnotationClass()
    {
        return null;
    }

    /**
     * The reference class handled by the processor, or null if the processor
     * does not process references from the deployment descriptor.
     */
    public abstract List<Class<? extends JNDIEnvironmentRef>> getJNDIEnvironmentRefClasses();

    /**
     * Creates a new processor instance. The annotation and plural annotation
     * class passed to the InjectionProcessor constructor must match the values
     * returned from {@link #getAnnotationClass} and {@link #getAnnotationsClass}.
     */
    public abstract InjectionProcessor<A, AS> createInjectionProcessor();
}
