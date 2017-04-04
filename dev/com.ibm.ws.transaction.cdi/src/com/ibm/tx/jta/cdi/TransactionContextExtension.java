/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.tx.jta.cdi;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.cdi.extension.WebSphereCDIExtension;

/**
 *
 */
@Component(service = WebSphereCDIExtension.class,
           property = { "bean.defining.annotations=javax.transaction.TransactionScoped" })
public class TransactionContextExtension implements Extension, WebSphereCDIExtension {

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager manager) {
        TransactionContext tc = new TransactionContext();
        com.ibm.tx.jta.impl.TransactionImpl.registerTransactionScopeDestroyer(tc);
        event.addContext(new TransactionContext());
    }
}