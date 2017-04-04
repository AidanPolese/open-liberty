/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2001, 2002
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

import java.rmi.*;

/**
 * UserTransactionEnabledContext provides an interface that BeanO classes
 * can implement to signify they provide functionality for obtaining
 * a UserTransaction through its Context.
 * 
 * Currently, Session beans and MessageDrivenBeans, are designed to
 * allow UserTransactions to be made available through their respective
 * contexts.
 **/
public interface UserTransactionEnabledContext {
    public int getIsolationLevel();

    public boolean enlist(ContainerTx tx) // d114677
    throws RemoteException;

    public BeanId getId();

    public int getModuleVersion();//d140003.20
}
