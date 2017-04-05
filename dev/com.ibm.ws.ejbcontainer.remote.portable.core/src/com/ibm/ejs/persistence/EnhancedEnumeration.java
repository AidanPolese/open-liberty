/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2005
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.persistence;

import java.rmi.RemoteException;
import java.util.Enumeration;

public interface EnhancedEnumeration extends Enumeration
{

    public boolean hasMoreElementsR()
                    throws RemoteException, EnumeratorException;

    public Object nextElementR()
                    throws RemoteException, EnumeratorException;

    public Object[] nextNElements(int n)
                    throws RemoteException, EnumeratorException;

    public Object[] allRemainingElements()
                    throws RemoteException, EnumeratorException;

}
