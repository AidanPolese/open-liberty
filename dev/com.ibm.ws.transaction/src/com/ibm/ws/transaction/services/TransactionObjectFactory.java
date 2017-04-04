/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.transaction.services;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.wsspi.injectionengine.InjectionTargetContext;
import com.ibm.wsspi.injectionengine.factory.InjectionObjectFactory;

@Component(service = { ObjectFactory.class, TransactionObjectFactory.class })
public class TransactionObjectFactory implements InjectionObjectFactory {

    private TransactionJavaColonHelper txHelper;

    @Reference
    protected void setTransactionHelper(TransactionJavaColonHelper helper) {
        txHelper = helper;
    }

    @Override
    public Object getObjectInstance(Object o, Name n, Context c, Hashtable<?, ?> envmt) throws Exception {
        if (o instanceof javax.naming.Reference && getClass().getName().equals(((javax.naming.Reference) o).getFactoryClassName())) {
            return txHelper.getUserTransaction(false, null);
        }
        return null;
    }

    @Override
    public Object getInjectionObjectInstance(javax.naming.Reference ref, Object targetInstance, InjectionTargetContext targetContext) throws Exception {
        return txHelper.getUserTransaction(true, targetContext);
    }
}