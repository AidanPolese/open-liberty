/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 2000
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

/**
 *  A CSIServant is what is registered with the CSI object adapter.
 */

package com.ibm.websphere.csi;

public interface CSIServant
                extends java.rmi.Remote
{
    /**
     * Can this servant instance be wlm'ed?
     */

    public boolean wlmable()
                    throws java.rmi.RemoteException;
}
