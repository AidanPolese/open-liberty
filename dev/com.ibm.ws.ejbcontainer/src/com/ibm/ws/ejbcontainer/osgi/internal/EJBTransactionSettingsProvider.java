/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi.internal;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.ejs.container.BeanMetaData;
import com.ibm.tx.jta.embeddable.GlobalTransactionSettings;
import com.ibm.tx.jta.embeddable.LocalTransactionSettings;
import com.ibm.tx.jta.embeddable.TransactionSettingsProvider;
import com.ibm.ws.ejbcontainer.osgi.internal.metadata.GlobalTranConfigDataImpl;
import com.ibm.ws.ejbcontainer.osgi.internal.metadata.LocalTranConfigDataImpl;
import com.ibm.ws.kernel.LibertyProcess;
import com.ibm.ws.runtime.metadata.ComponentMetaData;
import com.ibm.ws.threadContext.ComponentMetaDataAccessorImpl;

/**
 * Provide transaction settings on a per EJB basis.
 */
@Component(service = TransactionSettingsProvider.class)
public class EJBTransactionSettingsProvider implements TransactionSettingsProvider {

    @Reference(service = LibertyProcess.class, target = "(wlp.process.type=server)")
    protected void setLibertyProcess(ServiceReference<LibertyProcess> reference) {}

    protected void unsetLibertyProcess(ServiceReference<LibertyProcess> reference) {}

    @Override
    public boolean isActive() {
        return getBeanMetaData() != null;
    }

    @Override
    public GlobalTransactionSettings getGlobalTransactionSettings() {
        BeanMetaData bmd = getBeanMetaData();

        return bmd != null ? (GlobalTranConfigDataImpl) bmd._globalTran : null;
    }

    @Override
    public LocalTransactionSettings getLocalTransactionSettings() {
        BeanMetaData bmd = getBeanMetaData();

        return bmd != null ? (LocalTranConfigDataImpl) bmd._localTran : null;
    }

    private BeanMetaData getBeanMetaData() {
        ComponentMetaData cmd = ComponentMetaDataAccessorImpl.getComponentMetaDataAccessor().getComponentMetaData();

        return cmd instanceof BeanMetaData ? (BeanMetaData) cmd : null;
    }

}
