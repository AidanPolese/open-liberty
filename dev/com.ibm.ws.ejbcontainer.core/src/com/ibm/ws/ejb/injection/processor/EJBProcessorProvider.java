/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejb.injection.processor;

import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBs;

import com.ibm.ws.javaee.dd.common.EJBRef;
import com.ibm.ws.javaee.dd.common.JNDIEnvironmentRef;
import com.ibm.wsspi.injectionengine.InjectionProcessor;
import com.ibm.wsspi.injectionengine.InjectionProcessorProvider;

/**
 * Provides the EJB injection processor to the injection engine.
 */
public final class EJBProcessorProvider extends InjectionProcessorProvider<EJB, EJBs>
{
    List<Class<? extends JNDIEnvironmentRef>> REF_CLASSES =
                    Collections.<Class<? extends JNDIEnvironmentRef>> singletonList(EJBRef.class);

    @Override
    public Class<EJB> getAnnotationClass()
    {
        return EJB.class;
    }

    @Override
    public Class<EJBs> getAnnotationsClass()
    {
        return EJBs.class;
    }

    public List<Class<? extends JNDIEnvironmentRef>> getJNDIEnvironmentRefClasses()
    {
        return REF_CLASSES;
    }

    @Override
    public InjectionProcessor<EJB, EJBs> createInjectionProcessor()
    {
        return new EJBProcessor();
    }
}
