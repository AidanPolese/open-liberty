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
package com.ibm.ws.jpa.container.osgi.internal;

import java.util.Collections;
import java.util.List;

import javax.persistence.PersistenceUnit;
import javax.persistence.PersistenceUnits;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.javaee.dd.common.JNDIEnvironmentRef;
import com.ibm.ws.javaee.dd.common.PersistenceUnitRef;
import com.ibm.ws.jpa.management.JPAPUnitProcessor;
import com.ibm.wsspi.injectionengine.InjectionProcessor;
import com.ibm.wsspi.injectionengine.InjectionProcessorProvider;

@Component(service = InjectionProcessorProvider.class)
public class JPAPUnitProcessorProvider extends InjectionProcessorProvider<PersistenceUnit, PersistenceUnits> {
    private static final List<Class<? extends JNDIEnvironmentRef>> REF_CLASSES =
                    Collections.<Class<? extends JNDIEnvironmentRef>> singletonList(PersistenceUnitRef.class);

    /** {@inheritDoc} */
    @Override
    public InjectionProcessor<PersistenceUnit, PersistenceUnits> createInjectionProcessor() {
        return new JPAPUnitProcessor();
    }

    /** {@inheritDoc} */
    @Override
    public Class<PersistenceUnit> getAnnotationClass() {
        return PersistenceUnit.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<PersistenceUnits> getAnnotationsClass() {
        return PersistenceUnits.class;
    }

    /** {@inheritDoc} */
    @Override
    public List<Class<? extends JNDIEnvironmentRef>> getJNDIEnvironmentRefClasses() {
        return REF_CLASSES;
    }
}
