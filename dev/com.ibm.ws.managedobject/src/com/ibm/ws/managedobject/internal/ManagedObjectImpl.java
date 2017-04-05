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
package com.ibm.ws.managedobject.internal;

import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.ws.managedobject.ManagedObject;
import com.ibm.ws.managedobject.ManagedObjectContext;

public class ManagedObjectImpl<T> implements ManagedObject<T> {
    @Sensitive
    private final T object;

    public ManagedObjectImpl(@Sensitive T object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return super.toString() + '[' + object.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(object)) + ']';
    }

    @Sensitive
    @Override
    public T getObject() {
        return object;
    }

    @Override
    public ManagedObjectContext getContext() {
        return null;
    }

    @Override
    public <R> R getContextData(Class<R> klass) {
        return null;
    }

    @Override
    public void release() {}

    @Override
    public boolean isLifecycleManaged() {
        return false;
    }

    @Override
    public String getBeanScope() {
        return null;
    }
}
