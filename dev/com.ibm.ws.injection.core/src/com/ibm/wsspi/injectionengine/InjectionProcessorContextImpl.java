/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010, 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.injectionengine;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.List;

/**
 * Subclass of InjectionProcessorContext that increases visibility.
 */
public class InjectionProcessorContextImpl
                extends InjectionProcessorContext
{
    /**
     * Convenience method to call and cast the result of {@link ComponentNameSpaceConfiguration#getInjectionProcessorContext}.
     */
    public static InjectionProcessorContextImpl get(ComponentNameSpaceConfiguration compNSConfig)
    {
        return (InjectionProcessorContextImpl) compNSConfig.getInjectionProcessorContext();
    }

    public static <AS extends Annotation> Class<AS> getAnnotationsClass(InjectionProcessor<?, AS> processor)
    {
        return processor.ivAnnotationsClass;
    }

    public void initProcessor(InjectionProcessor<?, ?> processor, ComponentNameSpaceConfiguration compNSConfig)
                    throws InjectionException
    {
        processor.initProcessor(compNSConfig, this);
    }

    public static void setOverrideProcessor(InjectionProcessor<?, ?> processor, InjectionProcessor<?, ?> overrideProcessor)
    {
        processor.ivOverrideProcessor = overrideProcessor;
    }

    public static <A extends Annotation> void addOrMergeInjectionBinding(InjectionProcessor<A, ?> processor,
                                                                         Class<?> instanceClass,
                                                                         Member member,
                                                                         A ann)
                    throws InjectionException
    {
        processor.addOrMergeInjectionBinding(instanceClass, member, ann);
    }

    public static void processBindings(InjectionProcessor<?, ?> processor)
                    throws InjectionException
    {
        processor.resolveInjectionBindings();
        processor.performJavaNameSpaceBinding();
    }

    public static List<InjectionTarget> getInjectionTargets(InjectionBinding<?> injectionBinding)
    {
        return injectionBinding.ivInjectionTargets;
    }
}
