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

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Defines the injection target information specified via XML and/or annotations.
 * This class provides the implementation method target when there are multiple
 * parameters (i.e. an initializer method).
 */
public class InjectionTargetMultiParamMethod extends InjectionTargetMethod
{
    /**
     * Constructor to create an InjectionTarget for a method with multiple
     * parameters. <p>
     *
     * @param method - injection target method (initializer).
     * @param binding - the binding this target will be associated with.
     */
    protected InjectionTargetMultiParamMethod(Method method, InjectionBinding<?> binding)
        throws InjectionException
    {
        super(method, binding);
    }

    /**
     * Overridden because the method has multiple parameters, so a single type
     * cannot be determined.
     */
    @Override
    public Class<?> getInjectionClassType()
    {
        throw new IllegalStateException("Method " + ivMethod.getName() + " has multiple parameters;" +
                                        " a single type cannot be determined.");
    }

    @Override
    Class<?>[] getInjectionClassTypes()
    {
        return ivMethod.getParameterTypes();
    }

    @Override
    Object[] getInjectedObjects(Object injectedObject)
    {
        return (Object[]) injectedObject;
    }

    /**
     * Overridden because the method has multiple parameters, so a single generic
     * type cannot be determined.
     */
    @Override
    public Type getGenericType()
    {
        throw new IllegalStateException("Method " + ivMethod.getName() + " has multiple parameters;" +
                                        " a single generic type cannot be determined.");
    }

    /**
     * Perform the actual injection into the field or method. <p>
     *
     * Overridden to properly pass multiple parameters on method invoke. <p>
     *
     * @param objectToInject the object to inject into
     * @param dependentObject the dependent objects to inject, in an Object[].
     *
     * @throws Exception if the dependent object cannot be injected into the
     *             associated member of the specified object.
     */
    @Override
    protected void injectMember(Object objectToInject, Object dependentObject)
                    throws Exception
    {
        try {
            ivMethod.invoke(objectToInject, (Object[]) dependentObject);
        } finally {
            ivInjectionBinding.cleanAfterMethodInvocation();
        }
    }
}
