/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.persistence.internal.eclipselink;

import javax.transaction.TransactionManager;

import org.eclipse.persistence.transaction.JTATransactionController;

import com.ibm.tx.jta.TransactionManagerFactory;
import com.ibm.websphere.ras.annotation.Trivial;

@Trivial
public class TransactionController extends JTATransactionController {
     @Override
     protected TransactionManager acquireTransactionManager() throws Exception {
          return TransactionManagerFactory.getTransactionManager();
     }

}
