/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.impl.managedobject;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.InjectionTarget;

import org.jboss.weld.construction.api.WeldCreationalContext;
import org.jboss.weld.context.CreationalContextImpl;
import org.jboss.weld.manager.api.WeldInjectionTargetFactory;

import com.ibm.ws.cdi.impl.weld.WebSphereBeanDeploymentArchive;
import com.ibm.ws.cdi.interfaces.CDIRuntime;
import com.ibm.ws.managedobject.ManagedObjectContext;
import com.ibm.ws.managedobject.ManagedObjectFactory;
import com.ibm.ws.managedobject.ManagedObjectInvocationContext;

public class CDIInterceptorManagedObjectFactoryImpl<T> extends AbstractManagedObjectFactory<T> implements ManagedObjectFactory<T> {
    public CDIInterceptorManagedObjectFactoryImpl(Class<T> classToManage, CDIRuntime cdiRuntime) {
        super(classToManage, cdiRuntime, false);
    }

    /**
     * Get the CreationalContext from an existing ManagedObjectInvocationContext
     */
    @Override
    protected WeldCreationalContext<T> getCreationalContext(ManagedObjectInvocationContext<T> invocationContext) {

        ManagedObjectContext managedObjectContext = invocationContext.getManagedObjectContext();

        @SuppressWarnings("unchecked")
        WeldCreationalContext<T> creationalContext = managedObjectContext.getContextData(WeldCreationalContext.class);

        if (creationalContext instanceof CreationalContextImpl<?>) {
            creationalContext = ((CreationalContextImpl<?>) creationalContext).getCreationalContext(getBean());
        }

        return creationalContext;
    }

    /**
     * {@inheritDoc} We need to override this so that a special interceptor instance was created instead of the common proxied one
     */
    @Override
    protected InjectionTarget<T> getInjectionTarget(boolean nonContextual) {

        InjectionTarget<T> injectionTarget = null;

        Class<T> clazz = getManagedObjectClass();

        WebSphereBeanDeploymentArchive bda = getCurrentBeanDeploymentArchive();
        if (bda != null) {
            injectionTarget = bda.getJEEComponentInjectionTarget(clazz);
        }

        if (injectionTarget == null) {
            AnnotatedType<T> annotatedType = getAnnotatedType(clazz, nonContextual);
            WeldInjectionTargetFactory<T> weldInjectionTargetFactory = getBeanManager().getInjectionTargetFactory(annotatedType);
            injectionTarget = weldInjectionTargetFactory.createInterceptorInjectionTarget();

            if (bda != null) {
                bda.addJEEComponentInjectionTarget(clazz, injectionTarget);
            }
        }

        return injectionTarget;
    }

    @Override
    public String toString() {
        return "CDI Interceptor Managed Object Factory for class: " + _managedClass.getName();
    }

}
