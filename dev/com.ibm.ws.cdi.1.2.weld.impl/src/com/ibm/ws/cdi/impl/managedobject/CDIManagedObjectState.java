/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2012, 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.impl.managedobject;

import org.jboss.weld.construction.api.WeldCreationalContext;

import com.ibm.ws.managedobject.ManagedObjectContext;

/**
 * Wrapper around state that CDI cares about for an Object
 */
public class CDIManagedObjectState implements ManagedObjectContext {

    private static final long serialVersionUID = 1L;

    private WeldCreationalContext<?> _cc = null;

    public CDIManagedObjectState(WeldCreationalContext<?> creationalContext) {
        _cc = creationalContext;
    }

    @Override
    public void release() {
        if (_cc != null)
            _cc.release();
    }

    @Override
    public <T> T getContextData(Class<T> klass) {
        if (klass == WeldCreationalContext.class)
            return klass.cast(_cc);
        return null;
    }

}
