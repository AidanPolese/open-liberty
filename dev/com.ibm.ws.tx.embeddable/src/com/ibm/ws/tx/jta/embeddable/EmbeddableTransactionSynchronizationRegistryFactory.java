/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* @(#) 1.4 SERV1/ws/code/tx.embeddable/src/com/ibm/ws/tx/jta/TransactionSynchronizationRegistryFactory.java, WAS.transactions, WASX.SERV1 12/17/09 10:45:16 [6/27/11 12:08:05]                                                     */
/*                                                                                   */
/*  DESCRIPTION:                                                                     */
/*                                                                                   */
/*  Change History:                                                                  */
/*                                                                                   */
/*  YY-MM-DD  Developer  Defect    Description                                       */
/*  --------  ---------  ------    -----------                                       */
/*  09-12-17  johawkes   632700    Created from old JNDI factory                     */
/* ********************************************************************************* */

package com.ibm.ws.tx.jta.embeddable;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import javax.transaction.TransactionSynchronizationRegistry;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.tx.TranConstants;
import com.ibm.tx.jta.embeddable.impl.EmbeddableTransactionSynchronizationRegistryImpl;

public class EmbeddableTransactionSynchronizationRegistryFactory extends com.ibm.tx.jta.TransactionSynchronizationRegistryFactory implements ObjectFactory {
    private static final TraceComponent tc = Tr.register(EmbeddableTransactionSynchronizationRegistryFactory.class, TranConstants.TRACE_GROUP, null);

    @Override
    public synchronized Object getObjectInstance(Object referenceObject, Name name, Context context, Hashtable<?, ?> env) throws Exception {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "getObjectInstance", new Object[] { referenceObject, name, context, env, this });

        if (_instance == null) {
            _instance = new EmbeddableTransactionSynchronizationRegistryImpl();
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "getObjectInstance", _instance);
        return _instance;
    }

    public static synchronized TransactionSynchronizationRegistry getTransactionSynchronizationRegistry() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "getTransactionSynchronizationRegistry");

        if (_instance == null) {
            _instance = new EmbeddableTransactionSynchronizationRegistryImpl();
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "getTransactionSynchronizationRegistry", _instance);
        return _instance;
    }
}