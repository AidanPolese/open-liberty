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
package com.ibm.ws.injection.mock;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.ibm.ws.container.service.naming.NamingConstants;
import com.ibm.ws.injectionengine.osgi.internal.OSGiInjectionScopeData;
import com.ibm.ws.injectionengine.osgi.internal.naming.InjectionJavaColonHelper;
import com.ibm.wsspi.injectionengine.InjectionBinding;

public class MockInjectionJavaColonHelper extends InjectionJavaColonHelper {

    private final NamingConstants.JavaColonNamespace namespace;
    private final Map<String, InjectionBinding<?>> compEnvBindings;
    private final Map<Class<?>, Map<String, InjectionBinding<?>>> compBindings;
    private final Map<Class<?>, Map<String, InjectionBinding<?>>> nonCompBindings;

    public MockInjectionJavaColonHelper(Map<String, InjectionBinding<?>> bindings) {
        this.namespace = NamingConstants.JavaColonNamespace.COMP;
        this.compEnvBindings = bindings;
        this.compBindings = null;
        this.nonCompBindings = null;
    }

    public MockInjectionJavaColonHelper(NamingConstants.JavaColonNamespace namespace, Map<Class<?>, Map<String, InjectionBinding<?>>> nonCompEnvBindings) {
        this.namespace = namespace;
        this.compEnvBindings = null;
        this.compBindings = namespace == NamingConstants.JavaColonNamespace.COMP ? nonCompEnvBindings : null;
        this.nonCompBindings = namespace == NamingConstants.JavaColonNamespace.COMP ? null : nonCompEnvBindings;
    }

    @Override
    protected OSGiInjectionScopeData getInjectionScopeData(NamingConstants.JavaColonNamespace namespace) {
        ReentrantReadWriteLock nonCompLock = new ReentrantReadWriteLock();
        OSGiInjectionScopeData isd = new OSGiInjectionScopeData(null, this.namespace, null, nonCompLock);
        ReadWriteLock compLock = isd.compLock();
        if (compBindings != null) {
            compLock.writeLock().lock();
            for (Map<String, InjectionBinding<?>> bindings : compBindings.values()) {
                for (Map.Entry<String, InjectionBinding<?>> entry : bindings.entrySet()) {
                    isd.addCompBinding(NamingConstants.JavaColonNamespace.COMP.unprefix(entry.getKey()), entry.getValue());
                }
            }
            compLock.writeLock().unlock();
        }
        if (compEnvBindings != null) {
            compLock.writeLock().lock();
            isd.addCompEnvBindings(compEnvBindings);
            compLock.writeLock().unlock();
        }
        if (nonCompBindings != null) {
            OSGiInjectionScopeData compScopeData = new OSGiInjectionScopeData(null, NamingConstants.JavaColonNamespace.COMP, null, null);

            nonCompLock.writeLock().lock();
            isd.addNonCompBindings(nonCompBindings, compScopeData);
            nonCompLock.writeLock().unlock();
        }
        return isd;
    }
}
