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

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.construction.api.WeldCreationalContext;
import org.jboss.weld.manager.api.WeldManager;

import com.ibm.ejs.util.Util;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.cdi.interfaces.CDIRuntime;
import com.ibm.ws.managedobject.ManagedObject;
import com.ibm.ws.managedobject.ManagedObjectContext;
import com.ibm.ws.managedobject.ManagedObjectFactory;
import com.ibm.ws.managedobject.ManagedObjectInvocationContext;
import com.ibm.wsspi.injectionengine.ReferenceContext;

public class CDIManagedObjectFactoryImpl<T> extends AbstractManagedObjectFactory<T> implements ManagedObjectFactory<T> {

    private static final TraceComponent tc = Tr.register(CDIManagedObjectFactoryImpl.class);

    public CDIManagedObjectFactoryImpl(Class<T> classToManage, CDIRuntime cdiRuntime, boolean requestManagingInjectionAndInterceptors) {
        super(classToManage, cdiRuntime, requestManagingInjectionAndInterceptors);
    }

    public CDIManagedObjectFactoryImpl(Class<T> classToManage, CDIRuntime cdiRuntime, boolean requestManagingInjectionAndInterceptors, ReferenceContext referenceContext) {
        super(classToManage, cdiRuntime, requestManagingInjectionAndInterceptors, referenceContext);
    }

    @Override
    public ManagedObject<T> existingInstance(T instance) {
        ManagedObject<T> moi = null;
        BeanManager beanManager = this.getBeanManager();
        if (beanManager != null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "existingInstance entered with: " + Util.identity(instance));
            }
            WeldCreationalContext<T> cc = getCreationalContext(null);
            moi = new CDIManagedObject<T>(instance, cc, null);
        }

        return moi;

    }

    @Override
    public ManagedObject<T> createManagedObject() throws Exception {
        return super.createManagedObject(null);
    }

    /**
     * Create a new non-Contextual CreationalContext
     */
    @Override
    protected WeldCreationalContext<T> getCreationalContext(ManagedObjectInvocationContext<T> invocationContext) {

        ManagedObjectContext moc;
        // for managed bean case, if invocation context is not null, use that
        if (invocationContext != null) {
            moc = invocationContext.getManagedObjectContext();

        } else {
            //otherwise, use the one created when creating this object(annotated with scope)
            moc = createContext();
        }
        @SuppressWarnings("unchecked")
        WeldCreationalContext<T> creationalContext = moc.getContextData(WeldCreationalContext.class);
        return creationalContext;
    }

    @Override
    public ManagedObjectContext createContext() {

        Bean<T> bean = getBean();
        //A ManagedBean may or may not be a CDI bean.
        //If it is a CDI bean then the creational context should be contextual
        //If not the creational context will be non-contextual

        WeldManager beanManager = getBeanManager();
        WeldCreationalContext<T> creationalContext = beanManager.createCreationalContext(bean);

        CDIManagedObjectState managedObjectState = new CDIManagedObjectState(creationalContext);
        return managedObjectState;

    }

    @Override
    public String toString() {
        return "CDI Managed Object Factory for class: " + _managedClass.getName();
    }
}
