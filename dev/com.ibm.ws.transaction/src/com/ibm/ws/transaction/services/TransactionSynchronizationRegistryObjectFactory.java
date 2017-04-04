/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
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

import com.ibm.ws.tx.jta.embeddable.EmbeddableTransactionSynchronizationRegistryFactory;

public class TransactionSynchronizationRegistryObjectFactory implements ObjectFactory {

    @Override
    public Object getObjectInstance(Object o, Name n, Context c, Hashtable<?, ?> envmt) throws Exception {
        return EmbeddableTransactionSynchronizationRegistryFactory.getTransactionSynchronizationRegistry();
    }

}
