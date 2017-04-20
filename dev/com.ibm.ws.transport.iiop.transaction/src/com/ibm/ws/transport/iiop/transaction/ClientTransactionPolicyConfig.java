/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
/*
 * Some of the code was derived from code supplied by the Apache Software Foundation licensed under the Apache License, Version 2.0.
 */
package com.ibm.ws.transport.iiop.transaction;

import java.io.Serializable;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.omg.IOP.Codec;
import org.omg.PortableInterceptor.ClientRequestInfo;

/**
 * @version $Rev: 451417 $ $Date: 2006-09-29 13:13:22 -0700 (Fri, 29 Sep 2006) $
 */
public interface ClientTransactionPolicyConfig extends Serializable {
    Transaction exportTransaction(ClientRequestInfo ri, Codec codec);

    TransactionManager getTransactionManager();
}
