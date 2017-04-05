/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2001, 2002, 2003
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.persistence;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.ejb.EJBObject;

public interface RemoteEnumerator extends Remote
{

    public EJBObject[] nextNElements(int n)
                    throws RemoteException, EnumeratorException;

    public EJBObject[] allRemainingElements()
                    throws RemoteException, EnumeratorException;

}
