/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2000
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

/**
 *  The <code>ServantManager</code> is responsible for satisfying key
 *  to object requests from the communications layer (ORB). <p>
 */

package com.ibm.websphere.csi;

public interface ServantManager {

    /**
     * Return <code>Object</code> instance that corresponds to the give
     * object key.
     * 
     * @exception RemoteException thrown if <code>ServantManager</code>
     *                is unable to may given key to an object instance. <p>
     */

    public Object keyToObject(byte[] key)
                    throws java.rmi.RemoteException;
}
